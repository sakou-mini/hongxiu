package com.donglaistd.jinli.database.entity.game.goldenflower;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.event.GoldenFlowerEndEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DelayTaskConsumer;
import com.donglaistd.jinli.util.GoldenFlowerCardUtil;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.donglaistd.jinli.Constant.GameStatus.*;
import static com.donglaistd.jinli.Constant.GameType.GOLDENFLOWER;
import static com.donglaistd.jinli.constant.GameConstant.GENERAL_NUMBER;
import static com.donglaistd.jinli.constant.GameConstant.ONLY_ONE_PLAYER;
import static com.donglaistd.jinli.util.MessageUtil.*;

public class FriedGoldenFlower extends RaceBetGame {
    private static final Logger logger = Logger.getLogger(FriedGoldenFlower.class.getName());
    @Id
    protected final ObjectId id = ObjectId.get();
    @Transient
    private GoldenFlowerConfig config;
    @Transient
    private volatile AtomicInteger finishCount = new AtomicInteger(1);
    @Transient
    private AtomicBoolean isEnd = new AtomicBoolean(false);
    @Transient
    private Map<Integer, Set<Integer>> compareRecord = new HashMap<>();
    @Transient
    private int gameCount;
    @Transient
    private int rewardAmount;
    @Transient
    private DelayTaskConsumer delayTaskConsumer = DelayTaskConsumer.newInstance();
    @Transient
    private int cost;
    @Transient
    private int roundDealer;

    public FriedGoldenFlower(int dealer, Stack<Integer> freeSeatStack, String raceId, GoldenFlowerConfig config, int gameCount) {
        super(dealer, freeSeatStack);
        this.raceId = raceId;
        this.config = config;
        this.gameCount = gameCount;
    }

    @Override
    public String getGameId() {
        return id.toString();
    }

    @Override
    public Constant.GameType getGameType() {
        return GOLDENFLOWER;
    }

    @Override
    public void check(RacePokerPlayer player) {

    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public GoldenFlowerConfig getConfig() {
        return config;
    }

    public void setIsEnd(boolean isEnd) {
        this.isEnd.set(isEnd);
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    @Override
    public synchronized void fold(RacePokerPlayer player) {
        logger.info("begin fold card !");
        player.setFold(true);
        removeInGamePlayer(player);
        donePlayerList.remove(Integer.valueOf(player.getSeatNum()));
        getWaitPlayers().add(player);
        Jinli.FoldBroadcastMessage.Builder message = Jinli.FoldBroadcastMessage.newBuilder();
        message.setPlayer(player.toProto()).build();
        sendMsgToAllPlayers(message);
        if (player.getSeatNum() == nextTurn) {
            cancelTimer(true);
            return;
        }
        if (getInGamePlayers().size() == ONLY_ONE_PLAYER) {
            cancelTimer(false);
            endRoundOrNextTurn();
        }
    }

    @Override
    public void lookCards(RacePokerPlayer player) {
        synchronized (player) {
            player.setLook(true);
            Jinli.LookCardsBroadcastMessage.Builder message = Jinli.LookCardsBroadcastMessage.newBuilder();
            message.setSeatNum(player.getSeatNum());
            sendMsgToAllPlayers(message);
        }
    }

    @Override
    public void betChipIn(RacePokerPlayer player, int chip, boolean playerOpt) {
        synchronized (player) {
            if (playerOpt && player.getSeatNum() != nextTurn) {
                return;
            }
            boolean lookCard = player.isLook();
            long oldBetThisRound = 0;
            if (getBetRoundMap().get(player.getSeatNum()) != null) {
                oldBetThisRound = getBetRoundMap().get(player.getSeatNum());
            }
            if (playerOpt) {
                cancelTimer(false);
                int betBase;
                if (lookCard) {
                    betBase = config.getBaseBet() * 2;
                } else {
                    betBase = config.getBaseBet();
                }
                if (chip / betBase > getRoundMaxBet()) {
                    setRoundMaxBet(chip / betBase);
                }
            }
            addBetAmount(chip);
            if (!donePlayerList.contains(player.getSeatNum())) {
                donePlayerList.add(player.getSeatNum());
            }
            long beforeBet = 0L;
            if (getBetMap().get(player.getSeatNum()) != null) {
                beforeBet = getBetMap().get(player.getSeatNum());
            }
            getBetMap().put(player.getSeatNum(), beforeBet + chip);
            getBetRoundMap().put(player.getSeatNum(), chip + oldBetThisRound);
            player.addBodyChips(-chip);
            if (playerOpt) {
                player.setBetTimes(player.getBetTimes() + GENERAL_NUMBER);
                Jinli.BetChipsBroadcastMessage.Builder message = Jinli.BetChipsBroadcastMessage.newBuilder();
                message.setChips((int) player.getBringInChips()).setInChips(chip).setBetAmount(getBetAmount()).setSumChips((int) (chip + oldBetThisRound)).setSeatNum(player.getSeatNum());
                sendMsgToAllPlayers(message);
                endRoundOrNextTurn();
            }
        }

    }

    @Override
    public void sendNextTurnMessage() {
        List<RacePokerPlayer> collect = getInGamePlayers().stream().filter(p -> p.getBringInChips() == 0).collect(Collectors.toList());
        if (donePlayerList.contains(nextTurn) && !collect.isEmpty()) {
            compareCardsToList(collect.get(0));
            delayTaskConsumer.setRunAfter(() -> {
                if (getInGamePlayers().contains(collect.get(0))) {
                    endGame();
                } else {
                    endRoundOrNextTurn();
                }
            });
            delayTaskConsumer.startRun(config.getDelayTime());
        } else {
            Jinli.NextTurnBroadcastMessage.Builder message = Jinli.NextTurnBroadcastMessage.newBuilder();
            message.setNextTurn(nextTurn).setMultipleRate(getRoundMaxBet());
            sendMsgToAllPlayers(message);
            startTimer();
        }
    }

    @Override
    public synchronized void endGame() {
        if (this.gameState.compareAndSet(BETTING, SETTLING)) {
            logger.info("golden flower endGame begin...");
            Map<Integer, List<Card>> map = getInGamePlayers().stream().collect(Collectors.toMap(RacePokerPlayer::getSeatNum, RacePokerPlayer::getHandPokers));
            long betSum = betMap.values().stream().mapToLong(Long::longValue).sum();
            RacePokerPlayer winPlayer = null;
            if (getInGamePlayers().size() > 0) {
                winPlayer = getInGamePlayers().get(0);
            }
            if (Objects.nonNull(winPlayer)) {
                changePlayerChips(winPlayer, betSum);
                winPlayersMap.put(winPlayer.getSeatNum(), betSum);
                setDealer(winPlayer.getSeatNum());
                sendFlowerBroadcastMessage(winPlayer);
            }
            betMap.clear();
            betRoundMap.clear();
            betAmount = 0;
            finishCount.getAndIncrement();
            compareRecord.clear();
            moveInGamePlayersToWait();
            List<RacePokerPlayer> eliminated = getWaitPlayers().stream().filter(p -> p.getBringInChips() < cost).collect(Collectors.toList());
            if (!eliminated.isEmpty() || finishCount.get() == gameCount) {
                eliminated.forEach(super::outGame);
                EventPublisher.publish(new GoldenFlowerEndEvent(eliminated, this));
            }
            gameState.compareAndSet(SETTLING, IDLE);
            if (finishCount.get() == gameCount) return;
            checkStart();
        }
    }

    @Override
    public void startGame() {
        if (getGameState().compareAndSet(READY, BETTING)) {
            logger.info("begin start game ...");
            roundDealer = dealer;
            roundMaxBet = config.getBaseRate();
            handCardsMap.clear();
            winPlayersMap.clear();
            moveWaitPlayersToInGame();
            getInGamePlayers().forEach(RacePokerPlayer::reset);
            shuffle();
            assignHandPoker(config.getHandPokerSize());
            getInGamePlayers().forEach(p -> betChipIn(p,cost, false));
            nextTurn = getNextSeatNum(dealer, true);
            sendMsgToAllPlayers(buildMessage());
            sendNextTurnMessage();
        }
    }

    @Override
    public void startTimer() {
        ScheduledFuture<?> schedule = ScheduledTaskUtil.schedule(() -> {
            try {
                RacePokerPlayer player = getPlayerBySeatNum(getNextTurn());
                if (Objects.nonNull(player)) {
                    logger.info(player.getUser().getDisplayName() + " time is up,fold!");
                    fold(player);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, config.getOptTimeout());
        endTime.set(System.currentTimeMillis() + config.getOptTimeout());
        futureTaskWeakSet.add(schedule);
    }

    public synchronized void cancelTimer(boolean flag) {
        futureTaskWeakSet.clear();
        if (flag) {
            endRoundOrNextTurn();
        }
    }

    @Override
    public boolean checkStart() {
        logger.info("checkStart...");
        if (!gameState.get().equals(IDLE)) {
            return false;
        }
        if (getWaitPlayers().size() >= config.getMinPlayers() && getGameState().compareAndSet(IDLE, READY)) {
            sendMsgToAllPlayers(Jinli.ReadyStartBroadcastMessage.newBuilder()
                    .setGameId(getGameId())
                    .setGameCount(finishCount.get())
                    .setCost(cost)
                    .setReadyTime(config.getReadyCountDownTime()));
            futureTaskWeakSet.add(ScheduledTaskUtil.schedule(this::startGame, config.getReadyCountDownTime()));
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean checkRoundEnd() {
        boolean canEndRound = false;
        if (getInGamePlayers().size() == ONLY_ONE_PLAYER) {
            logger.info("golden flower only one InGamePlayers endgame start");
            endGame();
            return true;
        }
        int turn = getNextSeatNum(nextTurn, true);
        if (turn == nextTurn) {
            canEndRound = true;
        }
        if (canEndRound) {
            endGame();
        }
        return canEndRound;
    }

    public void compareCards(RacePokerPlayer player, int seatNumber) {
        synchronized (player) {
            if (player.getSeatNum() != nextTurn)
                return;
            if (player.isFold()) return;
            boolean success = betCompareCard(player);
            if (!success) return;
            if (player.getBringInChips() == 0) {
                compareCardsToList(player);
                delayTaskConsumer.setRunAfter(() -> {
                    if (getInGamePlayers().contains(player)) {
                        endGame();
                    } else {
                        endRoundOrNextTurn();
                    }
                });
                delayTaskConsumer.startRun(config.getDelayTime());
            } else {
                RacePokerPlayer comparePlayer = getPlayerBySeatNum(seatNumber);
                if (Objects.isNull(comparePlayer) || comparePlayer.isFold()) return;
                int compareValue = GoldenFlowerCardUtil.compareValue(player.getHandPokers(), comparePlayer.getHandPokers());
                RacePokerPlayer losePlayer = player;
                RacePokerPlayer winPlayer = comparePlayer;
                if (compareValue == GENERAL_NUMBER) {
                    losePlayer = comparePlayer;
                    winPlayer = player;
                }
                loseCompareCards(losePlayer, winPlayer, true);
            }
        }
    }


    public synchronized void loseCompareCards(RacePokerPlayer losePlayer, RacePokerPlayer winPlayer, boolean flag) {
        sendCompareCardsBroadcast(losePlayer, winPlayer);
        losePlayer.setFold(true);
        removeInGamePlayer(losePlayer);
        getWaitPlayers().add(losePlayer);
        Jinli.FoldBroadcastMessage.Builder message = Jinli.FoldBroadcastMessage.newBuilder();
        message.setPlayer(losePlayer.toProto()).build();
        sendMsgToAllPlayers(message);
        compareRecord.computeIfAbsent(losePlayer.getSeatNum(), k -> new HashSet<>()).add(winPlayer.getSeatNum());
        compareRecord.computeIfAbsent(winPlayer.getSeatNum(), k -> new HashSet<>()).add(losePlayer.getSeatNum());
        if (flag) {
            ScheduledTaskUtil.schedule(this::endRoundOrNextTurn, config.getDelayTime());
        }
    }

    private void sendCompareCardsBroadcast(RacePokerPlayer losePlayer, RacePokerPlayer winPlayer) {
        Jinli.CompareCardsBroadcastMessage.Builder message = Jinli.CompareCardsBroadcastMessage.newBuilder();
        Jinli.CompareInfo.Builder winBuilder = Jinli.CompareInfo.newBuilder();
        winBuilder.setUser(winPlayer.getUser().toSummaryProto()).setIsWin(true);
        Jinli.CompareInfo.Builder loseBuilder = Jinli.CompareInfo.newBuilder();
        loseBuilder.setUser(losePlayer.getUser().toSummaryProto()).setIsWin(false);
        message.addInfo(winBuilder.build()).addInfo(loseBuilder.build());
        sendMessageToWaitPlayers(message);
        for (RacePokerPlayer inGamePlayer : getInGamePlayers()) {
            if (inGamePlayer.equals(losePlayer) || inGamePlayer.equals(winPlayer)) {
                sendMessage(inGamePlayer.getUserId(), buildReply(buildShowMessage(inGamePlayer, message)));
            } else {
                sendMessage(inGamePlayer.getUserId(), buildReply(message));
            }
        }
    }

    private Jinli.CompareCardsBroadcastMessage.Builder buildShowMessage(RacePokerPlayer user, Jinli.CompareCardsBroadcastMessage.Builder message) {
        Jinli.CompareCardsBroadcastMessage.Builder clone = message.clone();
        for (int i = 0; i < clone.getInfoList().size(); i++) {
            if (clone.getInfoList().get(i).getUser().getUserId().equals(user.getUserId()) && user.isLook()) {
                Jinli.CompareInfo.Builder builder = clone.getInfoList().get(i).toBuilder();
                builder.addAllHandPokers(getJinliCard(user.getHandPokers()));
                clone.setInfo(i, builder);
                break;
            }
        }
        return clone;
    }

    public synchronized boolean betCompareCard(RacePokerPlayer player) {
        if (!(gameState.get().equals(BETTING))) {
            return false;
        }
        if (player.getSeatNum() != nextTurn) {
            return false;
        }
        boolean lookCard = player.isLook();
        long oldBetThisRound = 0;
        if (getBetRoundMap().get(player.getSeatNum()) != null) {
            oldBetThisRound = getBetRoundMap().get(player.getSeatNum());
        }
        cancelTimer(false);
        int betBase;
        if (lookCard) {
            betBase = config.getBaseBet() * 2;
        } else {
            betBase = config.getBaseBet();
        }
        int chip = getRoundMaxBet() * betBase * 2;
        if (chip >= player.getBringInChips()) {
            // 筹码不足
            chip = (int) player.getBringInChips();
        }
        addBetAmount(chip);
        Long beforeBet = 0L;
        if (getBetMap().get(player.getSeatNum()) != null) {
            beforeBet = getBetMap().get(player.getSeatNum());
        }
        getBetMap().put(player.getSeatNum(), beforeBet + chip);
        getBetRoundMap().put(player.getSeatNum(), chip + oldBetThisRound);
        player.addBodyChips(-chip);
        player.setBetTimes(player.getBetTimes() + GENERAL_NUMBER);
        Jinli.BetChipsBroadcastMessage.Builder message = Jinli.BetChipsBroadcastMessage.newBuilder();
        message.setChips((int) player.getBringInChips()).setIsCompareCardBet(true).setInChips(chip).setBetAmount(getBetAmount()).setSumChips((int) (chip + oldBetThisRound)).setSeatNum(player.getSeatNum());
        sendMsgToAllPlayers(message);
        return true;
    }

    private synchronized void compareCardsToList(RacePokerPlayer player) {
        for (RacePokerPlayer inGamePlayer : getInGamePlayers()) {
            if (inGamePlayer.equals(player)) continue;
            if (inGamePlayer.isFold()) continue;
            int result = GoldenFlowerCardUtil.compareValue(player.getHandPokers(), inGamePlayer.getHandPokers());
            if (result <= 0) {
                delayTaskConsumer.addExecMethod(() -> loseCompareCards(player, inGamePlayer, false));
                return;
            } else {
                delayTaskConsumer.addExecMethod(() -> loseCompareCards(inGamePlayer, player, false));
            }
        }
    }

    private Jinli.StartFlowerBroadcastMessage.Builder buildMessage() {
        Jinli.StartFlowerBroadcastMessage.Builder message = Jinli.StartFlowerBroadcastMessage.newBuilder();
        List<Jinli.FlowerUser> collect = getInGamePlayers().stream().map(RacePokerPlayer::toFlowerUser).collect(Collectors.toList());
        message.setBetAmount(betAmount)
                .setOptTimeout(config.getOptTimeout())
                .setCost(cost)
                .setDealer(dealer)
                .setBaseRate(roundMaxBet)
                .setNextTurn(nextTurn)
                .setBaseBet(config.getBaseBet())
                .addAllSeat(allSeatNum)
                .addAllInGamePlayers(collect);
        getBetMap().forEach((k, v) -> {
            message.addBetRoundMap(Jinli.BetRoundMap.newBuilder().setSeatNum(k).setBetAmount(v));
        });
        return message;
    }

    private void sendFlowerBroadcastMessage(RacePokerPlayer winPlayer) {
        List<Jinli.SettleInformation> infos = buildCommonSettleInformation(winPlayer);
        Jinli.EndFlowerBroadcastMessage message = buildEndFlowerBroadcastMessage(infos, winPlayer, winPlayer);
        sendMessage(winPlayer.getUserId(), buildReply(message));
        getWaitPlayers().forEach(p -> {
            sendMessage(p.getUserId(), buildReply(buildEndFlowerBroadcastMessage(infos, p, winPlayer)));
        });
    }

    private List<Jinli.SettleInformation> buildCommonSettleInformation(RacePokerPlayer winPlayer) {
        List<RacePokerPlayer> players = Stream.of(getInGamePlayers(), getWaitPlayers()).flatMap(Collection::stream).collect(Collectors.toList());
        return players.stream().map(p -> Jinli.SettleInformation.newBuilder()
                .setUser(p.getUser().toSummaryProto())
                .setIsWin(p.getUser().equals(winPlayer.getUser()))
                .setChips((int) p.getBringInChips())
                .setSeatNum(p.getSeatNum())
                .setIncome((int) (p.getBringInChips() - p.getInitCoin())).build()).collect(Collectors.toList());
    }

    public Jinli.EndFlowerBroadcastMessage buildEndFlowerBroadcastMessage(List<Jinli.SettleInformation> infos, RacePokerPlayer currentPlayer, RacePokerPlayer winner) {
        Jinli.EndFlowerBroadcastMessage.Builder userSettleMessage = Jinli.EndFlowerBroadcastMessage.newBuilder().addAllInfos(infos);
        userSettleMessage.addAllHandPokers(getJinliCard(currentPlayer.getHandPokers()));
        Set<Integer> records = compareRecord.computeIfAbsent(currentPlayer.getSeatNum(), k -> new HashSet<>());
        if (!currentPlayer.getUserId().equals(winner.getUserId())) {
            records.add(winner.getSeatNum());
        }
        Set<Jinli.CardsMap> cardsMaps = records.stream().map(seatNum -> Jinli.CardsMap.newBuilder().setSeatNum(seatNum)
                .addAllCards(getJinliCard(getPlayerByAllSeatNum(seatNum).getHandPokers())).build()).collect(Collectors.toSet());
        return userSettleMessage.addAllCardsMap(cardsMaps).build();
    }

    @Override
    public synchronized void checkEnd() {
        int playerCount = 0;
        for (RacePokerPlayer b : getInGamePlayers()) {
            if (!b.isFold() && b.getBringInChips() != 0) {
                playerCount++;
            }
        }
        if (playerCount < config.getMinPlayers()) {
            this.endGame();
        }
    }
    public synchronized int getCountdown() {
        return (int) (endTime.get() - System.currentTimeMillis());
    }
    public Jinli.UserGoldenFlowerGame buildGoldenFlowerGameInfo(String userId) {
        List<Jinli.FlowerUser> players = Stream.of(inGamePlayers, waitPlayers).flatMap(Collection::stream).sorted(Comparator.comparing(RacePokerPlayer::getSeatNum)).map(RacePokerPlayer::toFlowerUser).collect(Collectors.toList());
        Jinli.UserGoldenFlowerGame.Builder builder = Jinli.UserGoldenFlowerGame.newBuilder();
        builder.setBetAmount(betAmount)
                .setOptTimeout(config.getOptTimeout())
                .setCost(cost)
                .setDealer(roundDealer)
                .setBaseRate(roundMaxBet)
                .setBaseBet(config.getBaseBet())
                .setNextTurn(nextTurn)
                .setGameStatus(gameState.get())
                .setCountdown(getCountdown())
                .addAllInGamePlayers(players)
                .addAllSeat(allSeatNum);
        betRoundMap.forEach((k,v)->{
            builder.addBetRoundMap(Jinli.BetRoundMap.newBuilder().setSeatNum(k).setBetAmount(v));
        });

        players.stream().filter(f -> f.getUser().getUserId().equals(userId) && f.getIsLook()).findFirst().ifPresent(p -> {
            builder.addAllHandPokers(getJinliCard(getPlayerByAllSeatNum(p.getSeatNum()).getHandPokers()));
        });
        return builder.build();
    }
    public int getFinishCount() {
        return finishCount.get();
    }

    public int getGameCount() {
        return gameCount;
    }
}