package com.donglaistd.jinli.database.entity.game.goldenflower;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.RaceGame;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.util.DataManager;
import com.google.protobuf.Message;
import org.springframework.data.annotation.Transient;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static com.donglaistd.jinli.Constant.GameStatus.IDLE;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

public abstract class RaceBetGame extends RaceGame implements PokerService{
    @Transient
    protected int dealer;
    @Transient
    protected int betAmount;
    @Transient
    protected Stack<Integer> freeSeatStack;
    @Transient
    protected volatile int nextTurn = 0;
    @Transient
    protected int roundMaxBet = 0;
    @Transient
    protected List<Integer> allSeatNum;
    @Transient
    protected AtomicLong endTime = new AtomicLong(0);
    @Transient
    protected List<Integer> donePlayerList = new ArrayList<>();
    @Transient
    protected Map<Integer, List<Card>> handCardsMap = new HashMap<>();
    @Transient
    protected Map<Integer, Long> winPlayersMap = new HashMap<>();
    @Transient
    protected List<RacePokerPlayer> waitPlayers = new CopyOnWriteArrayList<>();
    @Transient
    protected List<RacePokerPlayer> inGamePlayers = new CopyOnWriteArrayList<>();
    @Transient
    protected AtomicReference<Constant.GameStatus> gameState = new AtomicReference<>(IDLE);
    @Transient
    protected Map<Integer, Long> betMap = new LinkedHashMap<>();
    @Transient
    protected Map<Integer, Long> betRoundMap = new LinkedHashMap<>();


    public RaceBetGame(int dealer, Stack<Integer> freeSeatStack) {
        this.dealer = dealer;
        this.freeSeatStack = freeSeatStack;
        this.allSeatNum = new ArrayList<>(freeSeatStack);
    }

    public int getNextTurn() {
        return nextTurn;
    }

    public void setNextTurn(int nextTurn) {
        this.nextTurn = nextTurn;
    }

    public int getRoundMaxBet() {
        return roundMaxBet;
    }

    public void setRoundMaxBet(int roundMaxBet) {
        this.roundMaxBet = roundMaxBet;
    }

    public Map<Integer, List<Card>> getHandCardsMap() {
        return handCardsMap;
    }

    public Map<Integer, Long> getWinPlayersMap() {
        return winPlayersMap;
    }

    public void setWinPlayersMap(Map<Integer, Long> winPlayersMap) {
        this.winPlayersMap = winPlayersMap;
    }

    public int getBetAmount() {
        return betAmount;
    }

    public void setBetAmount(int betAmount) {
        this.betAmount = betAmount;
    }

    protected void addBetAmount(int chips) {
        this.betAmount += chips;
    }

    public void setHandCardsMap(Map<Integer, List<Card>> handCardsMap) {
        this.handCardsMap = handCardsMap;
    }

    public List<RacePokerPlayer> getWaitPlayers() {
        return waitPlayers;
    }

    public Map<Integer, Long> getBetMap() {
        return betMap;
    }

    public void setBetMap(Map<Integer, Long> betMap) {
        this.betMap = betMap;
    }

    public void setWaitPlayers(List<RacePokerPlayer> waitPlayers) {
        this.waitPlayers = waitPlayers;
    }

    public List<RacePokerPlayer> getInGamePlayers() {
        return inGamePlayers;
    }
    public Stack<Integer> getFreeSeatStack() {
        return freeSeatStack;
    }

    public void setFreeSeatStack(Stack<Integer> freeSeatStack) {
        this.freeSeatStack = freeSeatStack;
    }

    public void setInGamePlayers(List<RacePokerPlayer> inGamePlayers) {
        this.inGamePlayers = inGamePlayers;
    }

    public Map<Integer, Long> getBetRoundMap() {
        return betRoundMap;
    }

    public void setBetRoundMap(Map<Integer, Long> betRoundMap) {
        this.betRoundMap = betRoundMap;
    }

    public AtomicReference<Constant.GameStatus> getGameState() {
        return gameState;
    }

    public void setGameState(AtomicReference<Constant.GameStatus> gameState) {
        this.gameState = gameState;
    }

    public int getDealer() {
        return dealer;
    }

    public void setDealer(int dealer) {
        this.dealer = dealer;
    }

    protected void shuffle() {
        this.deck.reset();
        this.deck.shuffle();
    }
    protected void assignHandPoker(int size) {
        getInGamePlayers().forEach(p -> p.setHandPokers(getDeck().dealMultipleCards(size)));
    }

    protected synchronized void moveWaitPlayersToInGame() {
        while (getWaitPlayers().size() > 0) {
            getInGamePlayers().add(getWaitPlayers().get(0));
            getWaitPlayers().remove(0);
        }
    }

    protected synchronized void moveInGamePlayersToWait() {
        while (getInGamePlayers().size() > 0) {
            getWaitPlayers().add(getInGamePlayers().get(0));
            getInGamePlayers().remove(0);
        }
    }


    protected int getNextSeatNum(int seatNum, boolean clockwise) {
        int begin = seatNum;
        while (true) {
            seatNum = getNextNum(seatNum, clockwise);
            RacePokerPlayer player = getPlayerBySeatNum(seatNum);
            if (player != null && !player.isFold()) {
                break;
            }
            if (begin == seatNum) {
                break;
            }
        }
        return seatNum;
    }
    public synchronized RacePokerPlayer getPlayerFromList(String userId) {
        return getInGamePlayers().stream().filter(p -> p.getUser().getId().equals(userId)).findFirst().orElse(null);
    }
    public synchronized RacePokerPlayer getPlayerBySeatNum(int seatNum) {
        Optional<RacePokerPlayer> player = getInGamePlayers().stream().filter(p -> p.getSeatNum() == seatNum).findFirst();
        return player.orElse(null);
    }

    public synchronized RacePokerPlayer getPlayerByAllSeatNum(int seatNum) {
        Optional<RacePokerPlayer> player =  Stream.of(getInGamePlayers(),getWaitPlayers()).flatMap(Collection::stream).filter(p -> p.getSeatNum() == seatNum).findFirst();
        return player.orElse(null);
    }

    protected int getNextNum(int seatNum, boolean clockwise) {
        if (clockwise) {
            return getNextNum(seatNum);
        } else {
            int nextSeatNum = seatNum - 1;
            if (nextSeatNum < 0) {
                nextSeatNum = allSeatNum.size() - 1;
            }
            return nextSeatNum;
        }
    }

    protected int getNextNum(int seatNum) {
        int nextSeatNum = seatNum + 1;
        if (nextSeatNum >= allSeatNum.size()) {
            nextSeatNum = 0;
        }
        return nextSeatNum;
    }

    protected synchronized void updateNextTurn( boolean clockwise) {
        int thisTurn = getNextTurn();
        thisTurn = getNextSeatNum(thisTurn, clockwise);
        setNextTurn(thisTurn);
    }

    protected synchronized void removeInGamePlayer(RacePokerPlayer player) {
        for (int i = 0; i < getInGamePlayers().size(); i++) {
            RacePokerPlayer p = getInGamePlayers().get(i);
            if (p.getUserId().equals(player.getUserId())) {
                getInGamePlayers().remove(p);
            }
        }
    }

    protected synchronized void removeWaitPlayer(RacePokerPlayer player) {
        for (int i = 0; i < getWaitPlayers().size(); i++) {
            RacePokerPlayer p = getWaitPlayers().get(i);
            if (p.getUserId().equals(player.getUserId())) {
                getWaitPlayers().remove(p);
            }
        }
    }

    protected void outGame(RacePokerPlayer player) {
        removeWaitPlayer(player);
        donePlayerList.remove(Integer.valueOf(player.getSeatNum()));
        if (player.getSeatNum() != -1) {
            freeSeatStack.push(player.getSeatNum());
        }
    }

    public abstract void checkEnd();
    protected synchronized  void changePlayerChips(RacePokerPlayer racePokerPlayer, Long chips) {
        racePokerPlayer.setBringInChips(racePokerPlayer.getBringInChips() + chips);
    }

    @Override
    public synchronized void endRoundOrNextTurn() {
        boolean roundEnd = checkRoundEnd();
        if (!roundEnd) {
            // 更新下一个玩家操作
            updateNextTurn(true);
            sendNextTurnMessage();
        }
    }

    protected void sendMessageToInGamePlayer(Message.Builder builder) {
        getInGamePlayers().stream().filter(p -> Objects.nonNull(p.getUser())).forEach(player -> {
            sendMessage(player.getUser().getId(), buildReply(builder));
        });
    }

    protected void sendMessageToWaitPlayers(Message.Builder builder) {
        getWaitPlayers().stream().filter(p -> Objects.nonNull(p.getUser())).forEach(player -> {
            sendMessage(player.getUser().getId(), buildReply(builder));
        });
    }

    protected void sendMsgToAllPlayers(Message.Builder builder) {
        sendMessageToInGamePlayer(builder);
        sendMessageToWaitPlayers(builder);
    }

    public synchronized void join(RacePokerPlayer player) {
        getWaitPlayers().add(player);
        Integer seatNum = getFreeSeatStack().pop();
        player.setSeatNum(seatNum);
        if (getFreeSeatStack().isEmpty()) {
//            this.setState(0);
        }
       checkStart();
    }
    public synchronized void joinAll(List<RacePokerPlayer> playerList) {
        for (RacePokerPlayer player : playerList) {
            getWaitPlayers().add(player);
            Integer seatNum = getFreeSeatStack().pop();
            player.setSeatNum(seatNum);
            if (getFreeSeatStack().isEmpty()) {
                // todo
            }
            DataManager.saveUserRace(player.getUserId(), UserRace.newInstance(player.getUserId(), getGameId(), getRaceId()));
        }
        checkStart();
    }
}
