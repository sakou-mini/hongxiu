package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import org.springframework.data.annotation.Transient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildBanker;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

public abstract class BankerGame extends BaseGame {
    private static final Logger logger = Logger.getLogger(BankerGame.class.getName());
    @Transient
    protected final int bankerMaxKeepCount = 10;
    @Transient
    protected final Map<User, Integer> bankerWaitList = new ConcurrentHashMap<>();
    @Transient
    protected User banker;
    @Transient
    protected int bankerCoinAmount = getSystemBankerCoin();
    @Transient
    protected int bankerKeepCount;
    @Transient
    protected int bankerWinAmount;
    @Transient
    boolean openBanker = true;

    public int getBankerWinAmount() {
        return bankerWinAmount;
    }

    public Constant.ResultCode addWaitingBanker(User user, int bringInCoin) {
        if (user.getGameCoin() < bringInCoin || bringInCoin < getBankerMinimalCoin()) {
            return Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
        }
        if (!bankerWaitList.containsKey(user)) {
            bankerWaitList.put(user, bringInCoin);
            EventPublisher.publish(new ModifyCoinEvent(user, -bringInCoin, this::sendBankerInfo));
            user.addGameCoin(-bringInCoin);
            return Constant.ResultCode.SUCCESS;
        }
        return Constant.ResultCode.ALREADY_IN_BANKER_QUEUE;
    }

    public Constant.ResultCode updateWaitingBankerBringCoin(User user,int bringInCoin){
        if(Objects.equals(banker,user)) return Constant.ResultCode.ALREADY_BECOME_BANKER;
        if (!bankerWaitList.containsKey(user)) return Constant.ResultCode.NOT_IN_BANKER_QUEUE;
        Integer lastBringCoin = bankerWaitList.get(user);
        int modifyCoin = bringInCoin - lastBringCoin;
        if (user.getGameCoin() < modifyCoin || bringInCoin < getBankerMinimalCoin()) {
            return Constant.ResultCode.NOT_ENOUGH_GAMECOIN;
        }
        bankerWaitList.put(user, bringInCoin);
        EventPublisher.publish(new ModifyCoinEvent(user, -modifyCoin, this::sendBankerInfo));
        user.addGameCoin(-modifyCoin);
        return Constant.ResultCode.SUCCESS;
    }

    public abstract int getBankerMinimalCoin();

    public abstract int getBankerContinueCoin();

    protected void setUpBanker() {
        while (banker == null && !bankerWaitList.isEmpty()) {
            extractBanker();
        }

        if (banker == null) {
            bankerCoinAmount = getSystemBankerCoin();
        }
        sendBankerInfo();
    }

    private void extractBanker() {
        User nextBanker = bankerWaitList.entrySet().iterator().next().getKey();
        if (nextBanker.isOnline()) {
            banker = nextBanker;
            bankerCoinAmount = bankerWaitList.entrySet().iterator().next().getValue();
        }
        bankerWaitList.remove(nextBanker);
    }

    private void sendBankerInfo() {
        DataManager.roomMap.get(owner.getRoomId()).broadCastToAllPlatform(buildReply(getBankerInfoMessage()));
    }

    public Jinli.BankerBroadcastMessage.Builder getBankerInfoMessage() {
        var bankerList = new ArrayList<Jinli.BankerInfo>();
        bankerWaitList.forEach((key, value) -> bankerList.add(buildBanker(key, value)));
        var bbm = Jinli.BankerBroadcastMessage.newBuilder();
        if (banker != null) {
            bbm.setCurrentBanker(buildBanker(banker, bankerCoinAmount));
        }
        return bbm.addAllWaitingBankers(bankerList);
    }


    public void broadCastBankerQuitMessage(){
        if(banker!=null){
            Jinli.BankerQuitBroadcastMessage.Builder bankerQuitBroadcast = Jinli.BankerQuitBroadcastMessage.newBuilder().setCurrentBanker(buildBanker(banker, bankerCoinAmount))
                    .setBankerKeepCount(bankerKeepCount).setWinOrLoseAmount(bankerWinAmount);
            DataManager.roomMap.get(owner.getRoomId()).broadCastToAllPlatform(buildReply(bankerQuitBroadcast));
        }
    }

    synchronized public void beginGameLoop(long bettingTime) {
        super.beginGameLoop(bettingTime);
        setUpBanker();
    }

    public Constant.ResultCode quitBanker(User user,boolean quitAndExitGame) {
        if (Objects.equals(user, banker)) {
            banker.setQuitingBanker(true);
            banker.setQuiteGame(quitAndExitGame);
        } else if (bankerWaitList.containsKey(user)) {
            var bringCoin = bankerWaitList.remove(user);
            EventPublisher.publish(new ModifyCoinEvent(user, bringCoin));
            user.addGameCoin(bringCoin);
        } else {
            return Constant.ResultCode.USER_NOT_FOUND;
        }
        sendBankerInfo();
        return Constant.ResultCode.SUCCESS;
    }

    public void returnAllBankerListCoin(){
        if(Objects.equals(nextGameStatue, Constant.GameStatus.ENDED)){
            bankerWaitList.forEach((user,bringCoin)->{
                logger.info("back "+user.getAccountName() + "bankerCoin" +bringCoin);
                EventPublisher.publish(new ModifyCoinEvent(user, bringCoin));
            });
            bankerWaitList.clear();
            if(banker!=null){
                returnBankerLeftCoin();
            }
        }
    }

    private void returnBankerLeftCoin(){
        logger.info("return banker coin" + banker.getAccountName() + "for coin:"+ bankerCoinAmount);
        EventPublisher.publish(new ModifyCoinEvent(banker, bankerCoinAmount));
        bankerKeepCount = 0;
        bankerWinAmount = 0;
        banker = null;
    }

    @Override
    public synchronized void finishGame() {
        super.finishGame();
        returnAllBankerListCoin();
    }

    protected void processBanker(BigDecimal loseAmount) {
        if (banker != null) {
            bankerCoinAmount += loseAmount.intValue();
            bankerWinAmount += loseAmount.intValue();
        }
    }

    @Override
    public void startGame() {
        super.startGame();
        if (gameStatus.equals(Constant.GameStatus.BETTING))
            chooseBanker();
    }

    protected void chooseBanker() {
        if (banker == null || !banker.isOnline() || banker.checkQuitingBankerAndReset() || bankerCoinAmount < getBankerContinueCoin() ||
                (bankerKeepCount >= bankerMaxKeepCount && bankerWaitList.size() > 0)) {
            broadCastBankerQuitMessage();
            if (banker != null) {
                logger.fine("clearing old banker");
                returnBankerLeftCoin();
            }
            logger.fine("try to get a new banker");
            setUpBanker();
        } else {
            bankerKeepCount++;
        }
    }

    public int getBankerKeepCount() {
        return bankerKeepCount;
    }

    @Override
    public Pair<Constant.ResultCode,Long> bet(User user, long betAmount, Game.BetType betType) {
        if (user.equals(banker)) {
            return new Pair<>(BANKER_BET_NOT_ALLOWED, 0L);
        }
        return super.bet(user, betAmount, betType);
    }

    protected abstract int getSystemBankerCoin();

    protected Jinli.GameResultBroadcastMessage.Builder buildBaseGameResultBroadcast() {
        Jinli.GameResultBroadcastMessage.Builder builder = Jinli.GameResultBroadcastMessage.newBuilder()
                .setRoomId(owner.getRoomId()).setBankerWinAmount(bankerWinAmount);

        var sortedMap = userWinCoinMap.entrySet().stream()
                .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        sortedMap.forEach((user, coin) -> builder.addWinnerAndAmount(Jinli.WinnerAndAmount.newBuilder()
                .setWinAmount(coin)
                .setUserId(user.getId())
                .setAvatarUrl(user.getAvatarUrl())
                .setAccountName(user.getAccountName())
                .setDisPlayName(user.getDisplayName())
        ));
        return builder;
    }

    public User getBanker() {
        return this.banker;
    }

    public boolean isOpenBanker() {
        return openBanker;
    }

    public void setOpenBanker(boolean openBanker) {
        this.openBanker = openBanker;
    }

    public int getBankerSize(){
        return bankerWaitList.size();
    }
}
