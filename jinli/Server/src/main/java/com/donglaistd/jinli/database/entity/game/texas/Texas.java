package com.donglaistd.jinli.database.entity.game.texas;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.TexasConfig;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.game.RaceGame;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.event.TexasEndEvent;
import com.donglaistd.jinli.metadata.Metadata;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.GameStatus.*;
import static com.donglaistd.jinli.util.MessageUtil.*;

@Document
public class Texas extends RaceGame {
    private static final Logger logger = Logger.getLogger(Texas.class.getName());
    @Id
    protected final ObjectId id = ObjectId.get();
    @Transient
    private int dealer;
    @Transient
    private int smallBetSeatNum;
    @Transient
    private int bigBetSeatNum;
    @Transient
    private AtomicReference<Constant.GameStatus> gameState = new AtomicReference<>(IDLE);
    // 状态（0，不可加入；1，可加入）
    @Transient
    private volatile int state = 1;
    @Transient
    private List<RacePokerPlayer> waitPlayers = new CopyOnWriteArrayList<>();
    @Transient
    protected List<RacePokerPlayer> inGamePlayers = new CopyOnWriteArrayList<>();
    @Transient
    protected List<Card> communityCards = new ArrayList<>();
    @Transient
    protected long betAmount;
    @Transient
    protected Map<Integer, Long> betMap = new LinkedHashMap<>();
    @Transient
    protected Map<Integer, Long> betRoundMap = new LinkedHashMap<>();
    @Transient
    public List<Integer> donePlayerList = new ArrayList<>();
    @Transient
    protected volatile int nextTurn = 0;
    @Transient
    protected volatile int roundTurn = 0;
    @Transient
    protected int roundMaxBet = 0;
    @Transient
    private Stack<Integer> freeSeatStack;
    @Transient
    protected Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> finalCardsMap = new HashMap<>();
    @Transient
    protected Map<Integer, List<Card>> handCardsMap = new HashMap<>();
    @Transient
    protected Map<Integer, Long> winPlayersMap = new HashMap<>();
    @Transient
    private TexasConfig config;
    @Transient
    private AtomicBoolean isOpenIncreaseBet = new AtomicBoolean(false);
    @Transient
    private AtomicInteger preBet = new AtomicInteger(0);
    @Transient
    private AtomicInteger increaseBetCount = new AtomicInteger(1);
    @Transient
    private int rewardAmount;
    @Transient
    private List<Integer> allSeatNum;
    @Transient
    private AtomicLong endTime = new AtomicLong(0);
    @Transient
    private AtomicInteger bigBet = new AtomicInteger(50);
    @Transient
    private AtomicInteger smallBet = new AtomicInteger(25);
    @Transient
    private  AtomicBoolean canStopIncreaseBet = new AtomicBoolean(false);
    @Transient
    public int maxPlayers;
    public AtomicReference<Constant.GameStatus> getGameState() {
        return gameState;
    }

    @Transient
    private AtomicInteger currentBigBets = new AtomicInteger(0);

    public synchronized int getCountdown() {
        return (int) (endTime.get() - System.currentTimeMillis());
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }

    public void setIsOpenIncreaseBet(boolean isOpen) {
        this.isOpenIncreaseBet.set(isOpen);
    }

    public List<Integer> getAllSeatNum() {
        return allSeatNum;
    }

    public String getRaceId() {
        return raceId;
    }

    public void setPreBet(int bet) {
        this.preBet.set(bet);
    }

    public synchronized void setCurrentBigBets(int bigBets) {
        this.currentBigBets.set(bigBets);
    }

    public Integer getCurrentBigBets() {
        return currentBigBets.get();
    }
    public int getDealer() {
        return dealer;
    }

    public void setDealer(int dealer) {
        this.dealer = dealer;
    }

    public int getSmallBetSeatNum() {
        return smallBetSeatNum;
    }

    public void setSmallBetSeatNum(int smallBetSeatNum) {
        this.smallBetSeatNum = smallBetSeatNum;
    }

    public int getBigBetSeatNum() {
        return bigBetSeatNum;
    }

    public void setBigBetSeatNum(int bigBetSeatNum) {
        this.bigBetSeatNum = bigBetSeatNum;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = 1;
    }

    public List<RacePokerPlayer> getWaitPlayers() {
        return waitPlayers;
    }

    public void setWaitPlayers(List<RacePokerPlayer> waitPlayers) {
        this.waitPlayers = waitPlayers;
    }

    public List<RacePokerPlayer> getInGamePlayers() {
        return inGamePlayers;
    }

    public void setInGamePlayers(List<RacePokerPlayer> inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public List<Card> getCommunityCards() {
        return communityCards;
    }

    public void setCommunityCards(List<Card> communityCards) {
        this.communityCards = communityCards;
    }

    public long getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(long betAmount) {
        this.betAmount = betAmount;
    }

    public Map<Integer, Long> getBetMap() {
        return betMap;
    }

    public void setBetMap(Map<Integer, Long> betMap) {
        this.betMap = betMap;
    }

    public Map<Integer, Long> getBetRoundMap() {
        return betRoundMap;
    }

    public int getBigBet() {
        return bigBet.get();
    }

    public void setBetRoundMap(Map<Integer, Long> betRoundMap) {
        this.betRoundMap = betRoundMap;
    }

    public List<Integer> getDonePlayerList() {
        return donePlayerList;
    }

    public void setDonePlayerList(List<Integer> donePlayerList) {
        this.donePlayerList = donePlayerList;
    }

    public int getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }

    public int getRoundTurn() {
        return roundTurn;
    }

    public void setRoundTurn(int roundTurn) {
        this.roundTurn = roundTurn;
    }

    public int getRoundMaxBet() {
        return roundMaxBet;
    }

    public void setRoundMaxBet(int roundMaxBet) {
        this.roundMaxBet = roundMaxBet;
    }

    public Stack<Integer> getFreeSeatStack() {
        return freeSeatStack;
    }

    public void setFreeSeatStack(Stack<Integer> freeSeatStack) {
        this.freeSeatStack = freeSeatStack;
        this.allSeatNum = new ArrayList<>(freeSeatStack);
    }

    public Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> getFinalCardsMap() {
        return finalCardsMap;
    }

    public void setFinalCardsMap(Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> finalCardsMap) {
        this.finalCardsMap = finalCardsMap;
    }

    public Map<Integer, List<Card>> getHandCardsMap() {
        return handCardsMap;
    }

    public void setHandCardsMap(Map<Integer, List<Card>> handCardsMap) {
        this.handCardsMap = handCardsMap;
    }

    public Map<Integer, Long> getWinPlayersMap() {
        return winPlayersMap;
    }

    public void setWinPlayersMap(Map<Integer, Long> winPlayersMap) {
        this.winPlayersMap = winPlayersMap;
    }

    @Override
    public String getGameId() {
        return id.toString();
    }

    @Override
    public Constant.GameType getGameType() {
        return Constant.GameType.TEXAS_GAME;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Texas(String raceId) {
        this.raceId = raceId;
    }

    public Texas(Deck deck, String raceId) {
        this.raceId = raceId;
        this.deck = deck;
    }

    public Texas(Deck deck, TexasConfig config, String raceId) {
        this.raceId = raceId;
        this.deck = deck;
        this.config = config;
    }

    public TexasConfig getConfig() {
        return config;
    }

    public void setConfig(TexasConfig config) {
        this.config = config;
    }

    public int getPreBet() {
        return this.preBet.get();
    }

    public synchronized void startGame() {
        if (getGameState().compareAndSet(READY, BETTING)){
            finalCardsMap.clear();
            handCardsMap.clear();
            winPlayersMap.clear();
            TexasUtil.movePlayers(this.getWaitPlayers(), this.getInGamePlayers());
            for (RacePokerPlayer p : inGamePlayers) {
                p.setHandPokers(null);
            }
            getInGamePlayers().forEach(p -> {
                p.setInitCoin((int) p.getBringInChips());
            });
            getInGamePlayers().forEach(texasPokerPlayer -> texasPokerPlayer.setFold(false));
            TexasUtil.updateNextDealer(this);
            setRoundMaxBet(bigBet.get());
            this.deck.reset();
            this.deck.shuffle();
            if (isOpenIncreaseBet.get()) {
                getInGamePlayers().forEach(p -> {
                    betChipIn(p, p.getBringInChips() <= getPreBet() ? (int) p.getBringInChips() : getPreBet(), false);
                });
            }
            int dealer = getDealer();
            int smallBets = smallBet.get();
            int bigBets = bigBet.get();
            setCurrentBigBets(bigBets);
            int smallBetSeat = TexasUtil.getNextSeatNum(dealer, this);
            if (getInGamePlayers().size() == 2) {
                smallBetSeat = dealer;
            }
            this.setSmallBetSeatNum(smallBetSeat);
            int bigBetSeat = TexasUtil.getNextSeatNum(smallBetSeat, this);
            this.setBigBetSeatNum(bigBetSeat);
            RacePokerPlayer smallBetPlayer = TexasUtil.getPlayerBySeatNum(smallBetSeat, getInGamePlayers());
            RacePokerPlayer bigBetPlayer = TexasUtil.getPlayerBySeatNum(bigBetSeat, getInGamePlayers());
            if (smallBetPlayer.getBringInChips() <= smallBets) {
                smallBets = (int) smallBetPlayer.getBringInChips();
            }
            if (bigBetPlayer.getBringInChips() <= bigBets) {
                bigBets = (int) bigBetPlayer.getBringInChips();
            }
            betChipIn(smallBetPlayer, smallBets, false);
            betChipIn(bigBetPlayer, bigBets, false);
            nextTurn = TexasUtil.getNextSeatNum(bigBetSeat, this);
            roundTurn = smallBetSeat;
            if (getInGamePlayers().size() == 2) {
                roundTurn = bigBetSeat;
            }
            TexasUtil.assignHandPoker(this);
            Jinli.TexasPrivateInfo privateInfo = toPrivateInfo();
            getInGamePlayers().forEach(player -> {
                Jinli.TexasPrivateInfo build = privateInfo.toBuilder().addAllHandPokers(getJinliCard(player.getHandPokers())).build();
                Jinli.StartTexasBroadcastMessage.Builder message = Jinli.StartTexasBroadcastMessage.newBuilder().setInfo(build);
                sendMessage(player.getUser().getId(), buildReply(message));
            });
            futureTaskWeakSet.add(ScheduledTaskUtil.schedule(this::sendStartNextTurnMessage, 2000));
        }
    }

    public synchronized void endGame() {
        if (this.gameState.compareAndSet(BETTING, SETTLING)) {
            logger.info("endGame begin");
            int allinCount = 0;
            for (RacePokerPlayer p : getInGamePlayers()) {
                if (p.getBringInChips() == 0) {
                    allinCount++;
                }
            }
            if (communityCards.size() < config.getCommunityCardSize() && getInGamePlayers().size() > 1) {
                logger.info("communityCards.size() < 5,send Public cards...");
                int assignCardCount = config.getCommunityCardSize() - communityCards.size();
                TexasUtil.assignCommonCardByNum(this, assignCardCount);
            }
            if (communityCards.size() == 5) {
                for (RacePokerPlayer p : getInGamePlayers()) {
                    List<Card> handPokerAndCommonCard = new ArrayList<>(getCommunityCards());
                    handPokerAndCommonCard.addAll(p.getHandPokers());
                    List<Card> handPoker = new ArrayList<>(p.getHandPokers());
                    CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup = TexasCardUtil.getMaxCardsGroup(handPokerAndCommonCard);
                    finalCardsMap.put(p.getSeatNum(), maxCardsGroup);
                    handCardsMap.put(p.getSeatNum(), handPoker);
                }
            }

            List<BetPool> betPoolList = new ArrayList<>();
            logger.info("sumBetPoolList begin");
            sumBetPoolList(betPoolList, betMap, inGamePlayers);
            for (BetPool betPool : betPoolList) {
                List<RacePokerPlayer> winPlayerList = new ArrayList<>();
                List<RacePokerPlayer> poolPlayers = betPool.getBetPlayerList();
                if (finalCardsMap.size() > 0) {
                    logger.info("compareCardsToWinList begin");
                    winPlayerList = compareCardsToWinList(poolPlayers, finalCardsMap);
                }
                if (winPlayerList.size() == 0) {
                    poolPlayers.stream().filter(p -> p != null && !p.isFold()).findFirst().ifPresent(winPlayerList::add);
                }
                Long win = 0L;
                if (winPlayerList.size() != 0) {
                    win = betPool.getBetSum() / winPlayerList.size();
                }
                for (RacePokerPlayer p : winPlayerList) {
                    TexasUtil.changePlayerChips(p, win);
                    Long lastPoolWin = winPlayersMap.get(p.getSeatNum());
                    if (lastPoolWin != null) {
                        win = win + lastPoolWin;
                    }
                    winPlayersMap.put(p.getSeatNum(), win);
                }
            }

            TexasUtil.sendMsgToAllPlayers(this, buildEndTexasMessage(), handCardsMap,finalCardsMap);
            betMap.clear();
            betRoundMap.clear();
            roundMaxBet = bigBet.get();
            donePlayerList.clear();
            betAmount = 0;
            communityCards.clear();
            deck.reset();
            deck.shuffle();
            TexasUtil.movePlayers(getInGamePlayers(), getWaitPlayers());
            ScheduledTaskUtil.schedule(() -> {
                List<RacePokerPlayer> eliminated = getWaitPlayers().stream().filter(p -> p.getBringInChips() <= 0).collect(Collectors.toList());
                if (eliminated.size() != 0) {
                    eliminated.forEach(TexasUtil::outGame);
                    EventPublisher.publish(new TexasEndEvent(eliminated, this));
                }
                this.gameState.compareAndSet(SETTLING, IDLE);
                ScheduledTaskUtil.schedule(this::checkStart, config.getRestBetweenGame());
            }, config.getDelayTime());
        }
    }

    private Jinli.EndTexasBroadcastMessage.Builder buildEndTexasMessage() {
        Jinli.EndTexasBroadcastMessage.Builder message = Jinli.EndTexasBroadcastMessage.newBuilder();
        this.winPlayersMap.forEach((k, v) -> {
            message.addWinPlayersMap(Jinli.WinPlayersMap.newBuilder().setSeatNum(k).setWin(v).build());
        });
        for (Integer integer : this.winPlayersMap.keySet()) {
            RacePokerPlayer player = TexasUtil.getPlayerBySeatNum(integer, getInGamePlayers());
            if (Objects.nonNull(player)) {
                Jinli.TexasPokerPlayer.Builder builder = player.toProto().toBuilder();
                message.addWinPlayers(builder);
            }
        }
        return message;
    }

    public synchronized void betChipIn(RacePokerPlayer racePokerPlayer, int chip, boolean playerOpt) {
        if (playerOpt && racePokerPlayer.getSeatNum() != nextTurn) {
            return;
        }
        if (playerOpt) {
            cancelTimer();
        }
        Texas thisTexas = racePokerPlayer.getTexas();
        long oldBetThisRound = 0;
        if (thisTexas.getBetRoundMap().get(racePokerPlayer.getSeatNum()) != null) {
            oldBetThisRound = thisTexas.getBetRoundMap().get(racePokerPlayer.getSeatNum());
        }
        if (playerOpt) {
            if (chip < racePokerPlayer.getBringInChips()) {
                if ((chip + oldBetThisRound) < thisTexas.getRoundMaxBet()) {
                    logger.warning("betChipIn error < getRoundMaxBet() chip:" + chip + "oldBetThisRound:"
                            + oldBetThisRound + " max:" + thisTexas.getRoundMaxBet());
                    if (thisTexas.getRoundMaxBet() - oldBetThisRound < racePokerPlayer.getBringInChips()) {
                        chip = (int) (thisTexas.getRoundMaxBet() - oldBetThisRound);
                    } else {
                        chip = (int) racePokerPlayer.getBringInChips();
                    }
                }
//                if ((chip + oldBetThisRound) != thisTexas.getRoundMaxBet()) {
//                    if (chip % 10 != 0) {
//                        logger.warning("betChipIn error % 10 != 0:" + chip);
//                        return;
//                    }
//                }
            }

        }
        try {

            if ((int) (chip + oldBetThisRound) > thisTexas.getRoundMaxBet()) {
                thisTexas.setRoundMaxBet((int) (chip + oldBetThisRound));
                donePlayerList.clear();
            }
            thisTexas.setBetAmount(getBetAmount() + chip);
            if (!thisTexas.donePlayerList.contains(racePokerPlayer.getSeatNum()) && playerOpt) {
                thisTexas.donePlayerList.add(racePokerPlayer.getSeatNum());
            }
            Long beforeBet = 0L;
            if (thisTexas.getBetMap().get(racePokerPlayer.getSeatNum()) != null) {
                beforeBet = thisTexas.getBetMap().get(racePokerPlayer.getSeatNum());
            }
            thisTexas.getBetMap().put(racePokerPlayer.getSeatNum(), beforeBet + chip);
            thisTexas.getBetRoundMap().put(racePokerPlayer.getSeatNum(), chip + oldBetThisRound);
            racePokerPlayer.setBringInChips(racePokerPlayer.getBringInChips() - chip);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (playerOpt) {
            Jinli.BetChipsBroadcastMessage.Builder message = Jinli.BetChipsBroadcastMessage.newBuilder();
            message.setChips((int) racePokerPlayer.getBringInChips()).setInChips(chip).setBetAmount((int) getBetAmount()).setSumChips((int) (chip + oldBetThisRound)).setSeatNum(racePokerPlayer.getSeatNum());
            TexasUtil.sendMsgToAllPlayers(this, message);
            endRoundOrNextTurn();
        }
    }

    public synchronized void fold(RacePokerPlayer racePokerPlayer) {
        try {
            cancelTimer();
            racePokerPlayer.setFold(true);
            Jinli.FoldBroadcastMessage.Builder message = Jinli.FoldBroadcastMessage.newBuilder();
            message.setPlayer(racePokerPlayer.toProto()).build();
            TexasUtil.sendMsgToAllPlayers(this, message);
            TexasUtil.removeInGamePlayer(racePokerPlayer);
            int index = donePlayerList.indexOf(racePokerPlayer.getSeatNum());
            if (index != -1) {
                donePlayerList.remove(index);
            }
            getWaitPlayers().add(racePokerPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        endRoundOrNextTurn();
    }

    public synchronized void check(RacePokerPlayer racePokerPlayer) {
        cancelTimer();
        if (!donePlayerList.contains(racePokerPlayer.getSeatNum())) {
            donePlayerList.add(racePokerPlayer.getSeatNum());
        }
        Jinli.CheckBroadcastMessage.Builder message = Jinli.CheckBroadcastMessage.newBuilder();
        message.setPlayer(racePokerPlayer.toProto()).build();
        TexasUtil.sendMsgToAllPlayers(this, message);
        endRoundOrNextTurn();
    }


    public synchronized void endRoundOrNextTurn() {
        boolean roundEnd = checkRoundEnd();
        if (!roundEnd) {
            TexasUtil.updateNextTurn(this);
            sendNextTurnMessage();
        }
    }

    public void startTimer() {
        ScheduledFuture<?> schedule = ScheduledTaskUtil.schedule(() -> {
            try {
                RacePokerPlayer racePokerPlayer = TexasUtil.getPlayerBySeatNum(getNextTurn(), getInGamePlayers());
                if (Objects.nonNull(racePokerPlayer)) {
                    Long max = betRoundMap.getOrDefault(racePokerPlayer.getSeatNum(), 0L);
                    if (max == getRoundMaxBet()) {
                        logger.info(racePokerPlayer.getUser().getDisplayName() + " time is up and is roundMaxBet ,check!");
                        check(racePokerPlayer);
                    } else {
                        logger.info(racePokerPlayer.getUser().getDisplayName() + " time is up,fold!");
                        fold(racePokerPlayer);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, config.getOptTimeout());
        endTime.set(System.currentTimeMillis() + config.getOptTimeout());
        futureTaskWeakSet.add(schedule);
    }

    public synchronized void cancelTimer() {
        futureTaskWeakSet.clear();
    }

    public synchronized boolean checkRoundEnd() {
        boolean canEndRound = true;
        if (getInGamePlayers().size() == 1) {
            logger.info("only one InGamePlayers endgame start");
            endGame();
            return true;
        }
        for (RacePokerPlayer racePokerPlayer : getInGamePlayers()) {
            if (racePokerPlayer.getBringInChips() == 0 && !donePlayerList.contains(racePokerPlayer.getSeatNum())) {
                donePlayerList.add(racePokerPlayer.getSeatNum());
            }
        }
        if (donePlayerList.size() == getInGamePlayers().size()&& getInGamePlayers().size() > 1) {
            long betMax = 0L;
            for (int i = 0; i < getInGamePlayers().size(); i++) {
                RacePokerPlayer b = getInGamePlayers().get(i);
                if (getBetMap().get(b.getSeatNum()) != null && getBetMap().get(b.getSeatNum()) > betMax) {
                    betMax = getBetMap().get(b.getSeatNum());
                }
            }
            logger.info("checkRoundEnd betMax:" + betMax);
            for (int i = 0; i < getInGamePlayers().size(); i++) {
                RacePokerPlayer b = getInGamePlayers().get(i);
                if (b == null || b.isFold()) {
                    continue;
                }
                if (getBetMap().get(b.getSeatNum()) == null) {
                    logger.info("checkRoundEnd no bet seatNum:" + b.getSeatNum());
                    canEndRound = false;
                    break;
                }
                if (b.getBringInChips() > 0) {
                    if (betMax > betMap.get(b.getSeatNum())) {
                        canEndRound = false;
                        break;
                    }
                }
            }
        } else {
            logger.info("checkRoundEnd getDonePlayerList().size() < getIngamePlayers().size()");
            canEndRound = false;
        }

        int turn = TexasUtil.getNextSeatNum(nextTurn, this);
        if (turn == nextTurn) {
            canEndRound = true;
        }
        if (canEndRound) {
            logger.info("canEndRound is true");
            setRoundMaxBet(1);
            Jinli.RoundStartBroadcastMessage.Builder message = TexasUtil.buildBetRoundMap(getBetAmount(), betRoundMap);
            TexasUtil.sendMsgToAllPlayers(this, message);
            betRoundMap.clear();
            donePlayerList.clear();
            for (int i = 0; i < getInGamePlayers().size(); i++) {
                RacePokerPlayer racePokerPlayer = getInGamePlayers().get(i);
                if (racePokerPlayer != null && racePokerPlayer.getBringInChips() <= 0) {
                    donePlayerList.add(racePokerPlayer.getSeatNum());
                }
            }
            if (communityCards.size() < config.getCommunityCardSize() && donePlayerList.size() < getInGamePlayers().size()) {
                if (getInGamePlayers().size() - donePlayerList.size() != 1) {
                    RacePokerPlayer firstTurnUser = TexasUtil.getPlayerBySeatNum(roundTurn, getInGamePlayers());
                    if (firstTurnUser == null || firstTurnUser.isFold() || firstTurnUser.getBringInChips() == 0) {
                        roundTurn = TexasUtil.getNextSeatNum(roundTurn, this);
                    }
                    setNextTurn(roundTurn);
                    sendNextTurnMessage();
                    int assignCardCount = communityCards.size() == 0 ? 3 : 1;
                    TexasUtil.assignCommonCardByNum(this, assignCardCount);
                } else {
                    endGame();
                }
            } else {
                endGame();
            }
        }
        return canEndRound;
    }

    public synchronized void sumBetPoolList(List<BetPool> betPoolList, Map<Integer, Long> betMap, List<RacePokerPlayer> inGamePlayers) {
        betMap = TexasUtil.sortMapByValue(betMap);
        boolean complete = false;
        while (!complete) {
            complete = true;
            long betSum = 0L;
            long thisBet = 0L;
            BetPool pool = new BetPool();
            for (Map.Entry<Integer, Long> e : betMap.entrySet()) {
                if (e.getValue() != 0) {
                    complete = false;
                    if (thisBet == 0) {
                        thisBet = e.getValue();
                    }
                    betSum = betSum + thisBet;
                    if (e.getValue() - thisBet < 0) {
                        throw new RuntimeException("bet map sorting error");
                    }
                    e.setValue(e.getValue() - thisBet);
                    RacePokerPlayer p = TexasUtil.getPlayerBySeatNum(e.getKey(), inGamePlayers);
                    if (Objects.nonNull(p)) {
                        pool.getBetPlayerList().add(p);
                    }
                }
            }
            pool.setBetSum(betSum);
            if (pool.getBetSum() != 0) {
                betPoolList.add(pool);
            }
        }
        betMap.clear();
    }

    public synchronized List<RacePokerPlayer> compareCardsToWinList(List<RacePokerPlayer> poolPlayers, Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> finalCardsMap) {
        List<RacePokerPlayer> winPlayerList = new ArrayList<>();
        CardsGroup<Constant.TexasType, List<Card>> listOld = null;
        if (finalCardsMap.size() == 0) {
            return null;
        }
        for (Map.Entry<Integer, CardsGroup<Constant.TexasType, List<Card>>> e : finalCardsMap.entrySet()) {
            boolean inThisPool = false;
            for (RacePokerPlayer b : poolPlayers) {
                if (b != null && !b.isFold() && e.getKey() == b.getSeatNum()) {
                    inThisPool = true;
                    break;
                }
            }
            if (!inThisPool) {
                continue;
            }
            if (listOld == null) {
                listOld = e.getValue();
                RacePokerPlayer wp = TexasUtil.getPlayerBySeatNum(e.getKey(), poolPlayers);
                if (wp != null) {
                    winPlayerList.add(wp);
                } else {
                    logger.info("winPlayerList.add e.getKey():" + e.getKey() + "wp not in poolPlayers");
                }
                logger.info("winPlayerList.add e.getKey():" + e.getKey());
            } else {
                CardsGroup<Constant.TexasType, List<Card>> listNew = e.getValue();
                logger.info("compareCardsToWinList CardUtil.compareValue listNew:" + listNew + " listOld" + listOld);
                int result = TexasCardUtil.compareValue(e.getValue(), listOld);
                logger.info("compareCardsToWinList CardUtil.compareValue result:" + result);
                if (result == 1) {
                    winPlayerList.clear();
                    winPlayerList.add(TexasUtil.getPlayerBySeatNum(e.getKey(), poolPlayers));
                    listOld = listNew;
                } else if (result == 0) {
                    winPlayerList.add(TexasUtil.getPlayerBySeatNum(e.getKey(), poolPlayers));
                }
            }
        }
        return winPlayerList;
    }

    public synchronized boolean checkStart() {
        if (!(gameState.get().equals(IDLE))) {
            return false;
        }
        if (getWaitPlayers().size() >= config.getMinPlayers() && getGameState().compareAndSet(IDLE, READY)){
            TexasUtil.sendMsgToAllPlayers(this, Jinli.ReadyStartBroadcastMessage.newBuilder().setGameId(getGameId()).setReadyTime(config.getReadyCountDownTime()));
            futureTaskWeakSet.add(ScheduledTaskUtil.schedule(this::startGame, config.getReadyCountDownTime()));
            return true;
        }
        return false;
    }

    public synchronized void sendNextTurnMessage() {
        Jinli.NextTurnBroadcastMessage.Builder message = Jinli.NextTurnBroadcastMessage.newBuilder();
        message.setNextTurn(getNextTurn());
        TexasUtil.sendMsgToAllPlayers(this, message);
        startTimer();
    }

    public synchronized void sendStartNextTurnMessage() {
        if (!checkRoundEnd()) {
            Jinli.NextTurnBroadcastMessage.Builder message = Jinli.NextTurnBroadcastMessage.newBuilder();
            message.setNextTurn(getNextTurn());
            TexasUtil.sendMsgToAllPlayers(this, message);
            startTimer();
        }
    }

    public synchronized void join(RacePokerPlayer player) {
        getWaitPlayers().add(player);
        Integer seatNum = getFreeSeatStack().pop();
        player.setSeatNum(seatNum);
        if (getFreeSeatStack().isEmpty()) {
            this.setState(0);
        }
        player.setTexas(this);
        this.checkStart();
    }

    public synchronized void joinAll(List<RacePokerPlayer> players) {
        for (RacePokerPlayer player : players) {
            getWaitPlayers().add(player);
            Integer seatNum = getFreeSeatStack().pop();
            player.setSeatNum(seatNum);
            if (getFreeSeatStack().isEmpty()) {
                this.setState(0);
            }
            player.setTexas(this);
            DataManager.saveUserRace(player.getUserId(), UserRace.newInstance(player.getUserId(), getGameId(), getRaceId()));
        }
        setRewardAmount(config.getBaseReward() * players.size());
        if (this.checkStart()) {
            startIncreaseBet();
        }
    }

    public synchronized RacePokerPlayer getPlayerFromList(String userId) {
        return getInGamePlayers().stream().filter(p -> p.getUser().getId().equals(userId)).findFirst().orElseThrow(RuntimeException::new);
    }

    public synchronized boolean isNextTurn(int playerSeatNum) {
        return playerSeatNum == nextTurn;
    }

    public Jinli.TexasPrivateInfo toPrivateInfo() {
        Jinli.TexasPrivateInfo.Builder info = Jinli.TexasPrivateInfo.newBuilder();
        info.addAllCommunityCards(getJinliCard(getCommunityCards()));
        List<Jinli.TexasPokerPlayer> waitPlayers =
                getWaitPlayers().stream().sorted(Comparator.comparing(RacePokerPlayer::getSeatNum)).map(RacePokerPlayer::toProto).collect(Collectors.toList());
        List<Jinli.TexasPokerPlayer> inGamePlayers =
                getInGamePlayers().stream().sorted(Comparator.comparing(RacePokerPlayer::getSeatNum)).map(RacePokerPlayer::toProto).collect(Collectors.toList());
        info.setBetAmount(this.getBetAmount())
                .setOptTimeout(this.config.getOptTimeout())
                .setBigBet(this.bigBet.get())
                .setRoundMaxBet(this.getRoundMaxBet())
                .setDealer(this.getDealer())
                .setSmallBetSeatNum(this.getSmallBetSeatNum())
                .setPreBet(preBet.intValue())
                .addAllWaitPlayers(waitPlayers)
                .addAllSeat(allSeatNum)
                .addAllInGamePlayers(inGamePlayers);
        getBetMap().forEach((k, v) -> {
            info.addBetRoundMap(Jinli.BetRoundMap.newBuilder().setSeatNum(k).setBetAmount(v));
        });
        return info.build();
    }

    // 涨盲
    public void startIncreaseBet() {
        ScheduledTaskUtil.schedule(() -> {
            logger.warning("Start increaseBet ... in:---->" + System.currentTimeMillis());
            increaseBetCount.incrementAndGet();
            Metadata.IncreaseBetDefine define = MetaUtil.getIncreaseBetDefineByLevel(increaseBetCount.get());
            if (Objects.isNull(define)) return;
            smallBet.set(define.getBet());
            bigBet.set(define.getBet() * 2);
            if (define.getPreBet() > 0) {
                setIsOpenIncreaseBet(true);
                setPreBet(define.getPreBet());
            }
            if (canStopIncreaseBet.get()) return;
            startIncreaseBet();
        }, 30000);
    }

    public void cancelIncreaseBetTimer() {
        canStopIncreaseBet.compareAndSet(false, true);
    }
}
