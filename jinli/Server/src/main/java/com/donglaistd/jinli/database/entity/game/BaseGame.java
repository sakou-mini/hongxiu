package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.GameEndEvent;
import com.donglaistd.jinli.event.LiveRecordEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static com.donglaistd.jinli.processors.handler.SwitchGameRequestHandler.gameListenerMap;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

public abstract class BaseGame extends CardGame implements Serializable {
    private static final Logger logger = Logger.getLogger(BaseGame.class.getName());
    @Transient
    protected final Map<User, Map<Game.BetType, Long>> betAmountMap = new ConcurrentHashMap<>();
    @Transient
    protected List<Game.BetType> betTypeList;

    @Transient
    protected LiveUser owner;

    @Transient
    protected List<Card> discard = new ArrayList<>();

    @Transient
    protected Map<User, Long> userWinCoinMap = new HashMap<>();
    @Transient
    protected long bettingTime;

    @Transient
    protected int delayFinishTime;
    @Transient
    protected AtomicBoolean aboutToEnd = new AtomicBoolean(false);
    @Transient
    protected Constant.GameStatus nextGameStatue = Constant.GameStatus.IDLE;
    @Transient
    protected BigDecimal payRate = BigDecimal.ONE;
    @Transient
    protected Constant.GameStatus gameStatus = Constant.GameStatus.PAUSED;
    @Transient
    protected int historyCount;

    protected static int getTotalLastPoint(List<Card> playerCards) {
        int value = 0;
        for (var card : playerCards) {
            value += card.getCardIntValue();
            if (value >= 10) value -= 10;
        }
        return value;
    }

    synchronized public LiveUser getOwner() {
        return owner;
    }

    synchronized public void setOwner(LiveUser owner) {
        this.owner = owner;
    }

    public long getBettingTime() {
        return bettingTime;
    }

    public void setBettingTime(long bettingTime) {
        this.bettingTime = bettingTime;
    }

    public Pair<Constant.ResultCode,Long> bet(User user, long betAmount, Game.BetType betType) {
        var checkBet = checkBetType(user, betType);
        if (checkBet != Constant.ResultCode.SUCCESS) return new Pair<>(checkBet, 0L);
        if (!checkGameBetLimit(user, betAmount, betType)) return new Pair<>(Constant.ResultCode.EXCEED_BET_LIMIT,0L) ;
        long maxBetAmount = calculateMaxBetAmount(betAmount, betType);
        if (user.getGameCoin() < maxBetAmount) return new Pair<>(Constant.ResultCode.NOT_ENOUGH_GAMECOIN, 0L);

        var betTypeIntegerMap = betAmountMap.computeIfAbsent(user, k -> new HashMap<>());
        var amount = betTypeIntegerMap.computeIfAbsent(betType, k -> 0L);
        betTypeIntegerMap.put(betType, amount + betAmount);
        return new Pair<>(Constant.ResultCode.SUCCESS,maxBetAmount)  ;
    }

    protected long calculateMaxBetAmount(long betAmount, Game.BetType betType) {
        return betAmount;
    }

    protected abstract boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType);

    public int getDeckDealtCount() {
        return this.deck.getDealtCardCount();
    }

    public int getDeckLeftCount() {
        return deck.getLeftCardCount();
    }

    public Jinli.DealtCardsBroadcastMessage getDeckDealt() {
        Map<Constant.CardNumber, Integer> cardNumMap = new HashMap<>();
        for (Constant.CardNumber cn : Constant.CardNumber.values()) {
            if (cn == Constant.CardNumber.UNRECOGNIZED) {
                continue;
            }
            cardNumMap.put(cn, 0);
        }
        for (Card card : this.discard) {
            var count = cardNumMap.computeIfAbsent(card.getCardValue(), k -> 0);
            cardNumMap.put(card.getCardValue(), count + 1);
        }
        var dealtCards = Jinli.DealtCardsBroadcastMessage.newBuilder();
        dealtCards.setCardsAmount(getDeckLeftCount());
        dealtCards.setDealtsAmount(this.discard.size());
        for (var cardInfo : cardNumMap.entrySet()) {
            var dealt = Jinli.DealtCard.newBuilder();
            dealt.setCard(cardInfo.getKey());
            dealt.setAmount(cardInfo.getValue());
            dealtCards.addDealtCards(dealt);
        }
        return dealtCards.build();
    }

    protected void addDealtCard(List<Card> cards) {
        if (Objects.isNull(cards)) {
            return;
        }
        this.discard.clear();
        this.discard.addAll(cards);
    }

    public Constant.ResultCode checkBetType(User user, Game.BetType betType) {
        return (betTypeList.contains(betType) ? Constant.ResultCode.SUCCESS : Constant.ResultCode.ILLEGAL_BET_TYPE);
    }

    public long getBetAmount(User user, Game.BetType betType) {
        var betTypeIntegerMap = betAmountMap.get(user);
        if (betTypeIntegerMap == null) {
            return 0;
        }
        var amount = betTypeIntegerMap.get(betType);
        if (amount == null) {
            return 0;
        }
        return amount;
    }

    public Map<User, Map<Game.BetType, Long>> getBetAmountMap() {
        return betAmountMap;
    }

    public void setAboutToEnd(boolean aboutToEnd) {
        this.aboutToEnd.set(aboutToEnd);
    }

    public Constant.GameStatus getNextGameStatue() {
        return nextGameStatue;
    }

    public void setNextGameState(Constant.GameStatus nextGameStatue) {
        this.nextGameStatue = nextGameStatue;
    }

    synchronized public void endGame() {
        aboutToEnd.set(true);
        logger.fine("set game next status to end");
        setNextGameState(Constant.GameStatus.ENDED);
    }

    synchronized public void pauseGame() {
        aboutToEnd.set(true);
        setNextGameState(Constant.GameStatus.PAUSED);
    }

    public boolean isStop() {
        return gameStatus.equals(Constant.GameStatus.PAUSED) || gameStatus.equals(Constant.GameStatus.ENDED);
    }

    synchronized public void resumeGame() {
        setNextGameState(Constant.GameStatus.IDLE);
        beginGameLoop(bettingTime);
    }

    public void setPayRate(BigDecimal payRate) {
        this.payRate = payRate;
    }

    public void setDelayFinishTime(int delayFinishTime) {
        this.delayFinishTime = delayFinishTime;
    }

    public Constant.GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(Constant.GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    synchronized public void beginGameLoop(long bettingTime) {
        logger.info("starting game: " + getGameId());
        this.bettingTime = bettingTime;
        startGame();
    }

    synchronized public void finishGame() {
        aboutToEnd.set(false);
        var room = DataManager.roomMap.getOrDefault(owner.getRoomId(), DataManager.closeRoomInfo.get(owner.getRoomId()));
        switch (nextGameStatue) {
            case PAUSED:
                logger.info("pause game:" + getGameId());
                if (Objects.nonNull(room))
                    room.broadCastToAllPlatform(buildReply(Jinli.PauseGameBroadcastMessage.newBuilder().setRoomId(room.getId())));
                gameStatus = Constant.GameStatus.PAUSED;
                break;
            case ENDED:
                logger.info("remove game from DataManager:" + getGameId());
                DataManager.removeGame(getGameId());
                logger.info("send end Game:" + getGameId());
                if (Objects.nonNull(room)) {
                    room.broadCastToAllPlatform(buildReply(Jinli.EndGameBroadcastMessage.newBuilder().setRoomId(room.getId())));
                }
                gameStatus = Constant.GameStatus.ENDED;
                if(gameListenerMap.get(this) == null){
                    EventPublisher.publish(new LiveRecordEvent(room,this));
                }else{
                    logger.warning("has switchGame ,not write LiveRecord!");
                }
                break;
        }
        EventPublisher.publish(new GameEndEvent(this));
    }

    public void broadCastDecks() {
        Room room = DataManager.roomMap.getOrDefault(owner.getRoomId(), DataManager.closeRoomInfo.get(owner.getRoomId()));
        if (Objects.nonNull(room)) room.broadCastToAllPlatform(buildReply(getDeckDealt()));
    }

    synchronized public long getTimeCountDown() {
        return bettingTime - (Calendar.getInstance().getTimeInMillis() - gameStartTime);
    }

    public Map<User, Long> getEveryOneUserBetAmountMap() {
        Map<User, Long> map = new HashMap<>();
        betAmountMap.forEach((k, v) -> {
            var sum = v.values().stream().mapToLong(Long::longValue).sum();
            map.put(k, sum);
        });
        return map;
    }

    public Map<Game.BetType, Long> getBetTypeAmountMap() {
        Map<Game.BetType, Long> map = new HashMap<>();
        betAmountMap.forEach((k, v) -> v.forEach((k1, v1) -> {
            var amount = map.computeIfAbsent(k1, a -> 0L);
            amount += v1;
            map.put(k1, amount);
        }));
        return map;
    }

    protected void settlePool() {
        logger.info("start settling " + getGameType() + " game:" + getGameId());
        addDealtCard(this.deck.getDealtCards());
        gameStatus = Constant.GameStatus.SETTLING;
        var gameResult = getGameResult();
        processGameResult(gameResult);
        userWinCoinMap.clear();
        betAmountMap.clear();

        if (deck.getLeftCardCount() <= getMinimalDeckCardRequest()) {
            logger.fine("reset deck:" + getGameId());
            deck.reset();
            discard.clear();
        }
        ScheduledTaskUtil.schedule(this::startGame, getDelayFinishTime());
        broadCastDecks();
    }

    public int getDelayFinishTime() {
        return delayFinishTime;
    }

    public void startGame() {
        //check game is stop
        if (aboutToEnd.get()) {
            logger.info("stopping game:" + getGameId());
            finishGame();
            return;
        }
        dealCards();
        broadCastDecks();
        broadcastStartGameMessage();
        gameStartTime = Calendar.getInstance().getTimeInMillis();
        ScheduledTaskUtil.schedule(this::settlePool, bettingTime);
        gameStatus = Constant.GameStatus.BETTING;
    }

    protected abstract int getMinimalDeckCardRequest();

    protected void broadcastStartGameMessage() {
        logger.info("send broadcastStartGameMessage:" + getGameId());
        var message = Jinli.StartGameBroadcastMessage.newBuilder();
        message.setLeftCardCount(this.getDeckLeftCount());
        message.setDealtCardCount(this.getDeckDealtCount());
        message.setTimeToEnd(getAboutToEndTime());
        message.setGameType(getGameType());
        message.setRoomId(owner.getRoomId());
        DataManager.roomMap.get(owner.getRoomId()).broadCastToAllPlatform(buildReply(message));
    }

    protected long getAboutToEndTime(){
        return bettingTime;
    }

    protected abstract List<Game.BetType> getGameResult();

    protected abstract void dealCards();

    protected abstract void processGameResult(List<Game.BetType> gameResults);

    public void setHistoryCount(int historyCount) {
        this.historyCount = historyCount;
    }

    public abstract List<? extends Jinli.GameResult> getCardHistory();


    protected void totalUserWinAmount(User user, long coin) {
        var amount = userWinCoinMap.computeIfAbsent(user, k -> 0L);
        userWinCoinMap.put(user, amount + coin);
    }

    protected Map<User,Long> calculateUserBackCoin() {
        Map<User, Long> userBackCoinMap = new HashMap<>(userWinCoinMap.size());
        userWinCoinMap.forEach((user,coin)->{
            long userTotalBetAmount = getUserTotalBetCoin(user);
            long betMaxAmount = getUserCalculateMaxBetAmount(user);
            userBackCoinMap.put(user, betMaxAmount - userTotalBetAmount + coin);
        });
        return userBackCoinMap;
    }

    protected long getUserCalculateMaxBetAmount(User user){
        Map<Game.BetType, Long> userBetMap = betAmountMap.getOrDefault(user, new HashMap<>());
        AtomicLong userMaxBetAmount = new AtomicLong(0);
        userBetMap.forEach((k,v)-> userMaxBetAmount.addAndGet(calculateMaxBetAmount(v, k)));
        return userMaxBetAmount.longValue();
    }

    public long getUserTotalBetCoin(User user) {
        return betAmountMap.getOrDefault(user, new HashMap<>()).values().stream().reduce(0L, Long::sum);
    }

    protected long getFuturePayout(Game.BetType betType, Game.BetType playerBetType, long betAmount) {
        var values = betAmountMap.values();
        var totalBetNum = values.stream().filter(b -> b.containsKey(betType)).map(m -> m.get(betType)).reduce(0L, Long::sum);
        if (betType.equals(playerBetType)) {
            totalBetNum += betAmount;
        }
        return totalBetNum;
    }

    public BigDecimal getPayRate() {
        return payRate;
    }

    public Map<User, Long> getUserWinCoinMap() {
        return userWinCoinMap;
    }

    @Override
    public String toString() {
        return "{" + "gameId=" + getGameId() + '}';
    }
}
