package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.GameFinishEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.GameType.NIUNIU;
import static com.donglaistd.jinli.constant.GameConstant.BULL_BULL_CARD_SIZE;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Document
public class BullBull extends BankerGame {

    private static final Logger logger = Logger.getLogger(BullBull.class.getName());

    @Id
    protected final ObjectId gameId = ObjectId.get();

    @Transient
    private final List<Card> bankerCards = new ArrayList<>();

    @Transient
    private final Map<Game.BetType, List<Card>> typeCards = new LinkedHashMap<>();

    @Transient
    private final List<Game.BullBullCardResult> cardHistory = new ArrayList<>();

    @Transient
    private int bullbullMinimalDeckCard;

    @Transient
    private int bankerMinimalCoin;
    @Transient
    private int systemBankerMinimalCoin;

    @Transient
    private int bankerContinueCoin;

    @Transient
    private int initShowNum;

    @Transient
    private int nonBullToSixBullPayout;
    @Transient
    private int sevenBullToNineBullPayout;
    @Transient
    private int overBullPayout;
    @Transient
    private long dealCardAnimationTime;

    public void setNonBullToSixBullPayout(int nonBullToSixBullPayout) {
        this.nonBullToSixBullPayout = nonBullToSixBullPayout;
    }

    public void setSevenBullToNineBullPayout(int sevenBullToNineBullPayout) {
        this.sevenBullToNineBullPayout = sevenBullToNineBullPayout;
    }

    public void setOverBullPayout(int overBullPayout) {
        this.overBullPayout = overBullPayout;
    }

    public void setBankerContinueCoin(int bankerContinueCoin) {
        this.bankerContinueCoin = bankerContinueCoin;
    }

    public void setBankerMinimalCoin(int bankerMinimalCoin) {
        this.bankerMinimalCoin = bankerMinimalCoin;
    }

    public void setSystemBankerMinimalCoin(int systemBankerMinimalCoin) {
        this.systemBankerMinimalCoin = systemBankerMinimalCoin;
    }

    public void setInitShowNum(int initShowNum) {
        this.initShowNum = initShowNum;
    }

    public List<Card> getCardsByType(Game.BetType type) {
        return typeCards.getOrDefault(type, new ArrayList<>());
    }


    public BullBull(Deck deck, int bullbullMinimalDeckCard) {
        this.deck = deck;
        this.bullbullMinimalDeckCard = bullbullMinimalDeckCard;
        betTypeList = new ArrayList<>(List.of(Game.BetType.SPADE_AREA, Game.BetType.HEART_AREA, Game.BetType.CLUB_AREA, Game.BetType.DIAMOND_AREA));
    }

    @Override
    public String getGameId() {
        return gameId.toString();
    }

    @Override
    protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {
        int totalBetNum = 0;
        for (Game.BetType type : betTypeList) {
            totalBetNum += getFuturePayout(type, betType, betAmount);
        }
        return totalBetNum * overBullPayout < bankerCoinAmount;
    }

    @Override
    protected long calculateMaxBetAmount(long betAmount, Game.BetType betType) {
        return  betAmount * overBullPayout;
    }

    @Override
    public int getBankerMinimalCoin() {
        return bankerMinimalCoin;
    }

    @Override
    public int getBankerContinueCoin() {
        return bankerContinueCoin;
    }

    @Override
    protected int getSystemBankerCoin() {
        return systemBankerMinimalCoin;
    }

    @Override
    protected int getMinimalDeckCardRequest() {
        return bullbullMinimalDeckCard;
    }

    @Override
    public Constant.GameType getGameType() {
        return NIUNIU;
    }

    public List<Card> getBankerCards() {
        return bankerCards;
    }

    public void setDealCardAnimationTime(long dealCardAnimationTime) {
        this.dealCardAnimationTime = dealCardAnimationTime;
    }

    @Override
    protected void dealCards() {
        if (deck.getLeftCardCount() < bullbullMinimalDeckCard) return;
        logger.info("bullbull dealing cards");
        bankerCards.clear();
        typeCards.clear();
        betTypeList.forEach(betType -> typeCards.put(betType, deck.dealMultipleCards(BULL_BULL_CARD_SIZE)));
        bankerCards.addAll(deck.dealMultipleCards(BULL_BULL_CARD_SIZE));
        sendShowCards();
    }

    private void sendShowCards() {
        logger.info("notify showCards");
        Jinli.CardShowBroadcastMessage.Builder message = Jinli.CardShowBroadcastMessage.newBuilder();
        message.setBullPartCards(BullBullBuilder.buildCardShowBroadcastMessage(this, initShowNum));
        if (getOwner() != null && DataManager.roomMap.get(owner.getRoomId()) != null) {
            message.setRoomId(owner.getRoomId());
            DataManager.roomMap.get(owner.getRoomId()).broadCastToAllPlatform(buildReply(message));
        }
    }

    public boolean compareWithBanker(List<Card> cards) {
        BullRulerResult playerBullResult;
        BullRulerResult bankerBullResult;
        playerBullResult = BullRulerResult.getBullResult(cards);
        bankerBullResult = BullRulerResult.getBullResult(bankerCards);
        if (playerBullResult.getBullType().equals(bankerBullResult.getBullType()))
            return playerBullResult.getMaxCard().greaterThanOtherCard(bankerBullResult.getMaxCard(),getGameType());
        return playerBullResult.getBullType().getNumber() > bankerBullResult.getBullType().getNumber();
    }

    @Override
    protected List<Game.BetType> getGameResult() {
        List<Game.BetType> gameResults = new ArrayList<>(4);
        for (Game.BetType betType : typeCards.keySet()) {
            if (compareWithBanker(typeCards.get(betType))) gameResults.add(betType);
        }
        return gameResults;
    }

    protected Map<Game.BetType, BigDecimal> getAllTypeCardPayout() {
        Map<Game.BetType, BigDecimal> betTypePayout = new HashMap<>();
        typeCards.forEach((type, cards) -> betTypePayout.put(type, getPayout(BullRulerResult.getBullResult(cards).getBullType())));
        return betTypePayout;
    }

    @Override
    protected void processGameResult(List<Game.BetType> gameResults) {
        BullRulerResult bankerRulerResult = BullRulerResult.getBullResult(bankerCards);
        BigDecimal bankerPayout = getPayout(bankerRulerResult.getBullType());

        Map<Game.BetType, BigDecimal> typePayout = getAllTypeCardPayout();
        assert typePayout.size() == typeCards.size();
        for (var betEntry : betAmountMap.entrySet()) {
            var betMapValues = betEntry.getValue();
            User player = betEntry.getKey();
            for (var entry : betMapValues.entrySet()) {
                Game.BetType betType = entry.getKey();
                BigDecimal betCoin = BigDecimal.valueOf(entry.getValue());
                //payout
                BigDecimal payout = typePayout.get(betType);
                BigDecimal lostAmount = betCoin.multiply(payout);
                BigDecimal winAmount = betCoin.multiply(payout);
                if (gameResults.contains(betType)) {    //player win
                    winAmount = winAmount.multiply(payRate).add(betCoin);
                    lostAmount = lostAmount.negate();
                } else {  //banker win
                    winAmount = betCoin.multiply(bankerPayout).subtract(betCoin).negate();
                    lostAmount = betCoin.multiply(bankerPayout).multiply(payRate);
                }
                logger.info(player.getAccountName() + "coin：" + winAmount);
                logger.info("banker coin：" + lostAmount);
                totalUserWinAmount(player, winAmount.intValue());
                processBanker(lostAmount);
            }
        }
        //buildReply
        Map<User, Long> userBackCoin = calculateUserBackCoin();
        Jinli.GameResultBroadcastMessage.Builder builder = buildBaseGameResultBroadcast();
        var bullCardResult = buildBullBullCardResult();
        cardHistory.add(bullCardResult);
        builder.setBullbullCardResult(bullCardResult);
        EventPublisher.publish(new ModifyCoinEvent(userBackCoin.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))));
        EventPublisher.publish(new GameFinishEvent(this, builder.build(), getEveryOneUserBetAmountMap(), gameResults));
    }

    @Override
    public List<? extends Jinli.GameResult> getCardHistory() {
        var result = new ArrayList<Jinli.GameResult>();
        cardHistory.subList(Math.max(0, cardHistory.size() - historyCount), cardHistory.size()).
                forEach(h -> result.add(Jinli.GameResult.newBuilder().setBullbullCardResult(h).build()));
        return result;
    }

    public Game.BullBullCardResult buildBullBullCardResult() {
        var cardResult = Game.BullBullCardResult.newBuilder();
        cardResult.setBankerCard(BullBullBuilder.getJinliBullBullCard(this, bankerCards));
        cardResult.setSpadeCard(BullBullBuilder.getJinliBullBullCard(this, typeCards.get(Game.BetType.SPADE_AREA)));
        cardResult.setHeartCard(BullBullBuilder.getJinliBullBullCard(this, typeCards.get(Game.BetType.HEART_AREA)));
        cardResult.setClubCard(BullBullBuilder.getJinliBullBullCard(this, typeCards.get(Game.BetType.CLUB_AREA)));
        cardResult.setDiamondCard(BullBullBuilder.getJinliBullBullCard(this, typeCards.get(Game.BetType.DIAMOND_AREA)));
        return cardResult.build();
    }

    @Override
    public synchronized void beginGameLoop(long bettingTime) {
        logger.fine("starting bullbull game ");
        super.beginGameLoop(bettingTime);
    }

    protected BigDecimal getPayout(Constant.BullType bullType) {
        if (bullType == null) return BigDecimal.valueOf(0);
        int typeNumber = bullType.getNumber();
        if (typeNumber >= Constant.BullType.BullBull_VALUE) return BigDecimal.valueOf(overBullPayout);
        else if (typeNumber >= Constant.BullType.Bull_7_VALUE) return BigDecimal.valueOf(sevenBullToNineBullPayout);
        else {
            return BigDecimal.valueOf(nonBullToSixBullPayout);
        }
    }

    public BullBull() {
    }

    public Game.BullbullCardShow getCardResult() {
        if (getGameStatus().equals(Constant.GameStatus.BETTING)) {
            return BullBullBuilder.buildCardShowBroadcastMessage(this, initShowNum).build();
        } else {
            return BullBullBuilder.buildCardShowBroadcastMessage(this, BULL_BULL_CARD_SIZE).build();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BullBull game = (BullBull) o;
        return Objects.equals(gameId, game.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }

    @Override
    protected long getAboutToEndTime() {
        return bettingTime - dealCardAnimationTime;
    }
}
