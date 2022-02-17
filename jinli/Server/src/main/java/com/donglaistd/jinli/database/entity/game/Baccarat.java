package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.GameFinishEvent;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.service.EventPublisher;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Game.BetType.*;
import static com.donglaistd.jinli.util.MessageUtil.getJinliCard;

@Document
public class Baccarat extends BankerGame {

    private static final Logger logger = Logger.getLogger(Baccarat.class.getName());
    @Id
    protected final ObjectId gameId = ObjectId.get();

    @Transient
    private final List<Game.BaccaratCardResult> cardHistory = new ArrayList<>();
    @Transient
    private final List<Card> dealerCards = new ArrayList<>();
    @Transient
    private final List<Card> playerCards = new ArrayList<>();
    @Transient
    private int baccaratMinimalDeckCard;
    //todo init in handler
    @Transient
    private int bankerMinimalCoin;
    @Transient
    private int systemBankerMinimalCoin;
    @Transient
    private int bankerContinueCoin;
    @Transient
    private BigDecimal dealerPayout;
    @Transient
    private BigDecimal playerPayout;
    @Transient
    private BigDecimal dealerPairPayout;
    @Transient
    private BigDecimal playerPairPayout;
    @Transient
    private BigDecimal drawPayout;
    @Transient
    private int extraCardNum;
    @Transient
    protected long extraDelayTime;

    public Baccarat() {
    }

    public Baccarat(Deck deck, int baccaratMinimalDeckCard) {
        this.deck = deck;
        this.baccaratMinimalDeckCard = baccaratMinimalDeckCard;
        betTypeList = new ArrayList<>(List.of(BACCARAT_DEALER, BACCARAT_PLAYER, BACCARAT_DRAW, BACCARAT_DEALER_PAIR, BACCARAT_PLAYER_PAIR));
    }

    @Override
    public List<Jinli.GameResult> getCardHistory() {
        var result = new ArrayList<Jinli.GameResult>();
        cardHistory.subList(Math.max(0, cardHistory.size() - historyCount), cardHistory.size()).
                forEach(h -> result.add(Jinli.GameResult.newBuilder().setBaccaratCardResult(h).build()));
        return result;
    }

    public String getGameId() {
        return gameId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Baccarat baccarat = (Baccarat) o;
        return Objects.equals(gameId, baccarat.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }

    @Override
    protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {
        var currentPotentialPayout = getFuturePayout(BACCARAT_DEALER_PAIR, betAmountMap.values(), betType, betAmount);
        currentPotentialPayout += getFuturePayout(BACCARAT_PLAYER_PAIR, betAmountMap.values(), betType, betAmount);
        var dealerPayout = getFuturePayout(BACCARAT_DEALER, betAmountMap.values(), betType, betAmount);
        var playerPayout = getFuturePayout(BACCARAT_PLAYER, betAmountMap.values(), betType, betAmount);
        var drawPayout = getFuturePayout(BACCARAT_DRAW, betAmountMap.values(), betType, betAmount);
        currentPotentialPayout += Math.max(Math.max(dealerPayout, playerPayout), drawPayout);
        return currentPotentialPayout < bankerCoinAmount;
    }

    private long getFuturePayout(Game.BetType type, Collection<Map<Game.BetType, Long>> values, Game.BetType futureBetType, long betAmount) {
        long payout = getPayoutByBetType(type, values);
        if (type.equals(futureBetType)) {
            payout += betAmount * getPayout(type).intValue();
        }
        return payout;
    }

    private Long getPayoutByBetType(Game.BetType type, Collection<Map<Game.BetType, Long>> values) {
        return values.stream().filter(b -> b.containsKey(type)).map(b -> b.get(type)).reduce(0L, Long::sum) * getPayout(type).longValue();
    }

    @Override
    protected int getMinimalDeckCardRequest() {
        return baccaratMinimalDeckCard;
    }

    @Override
    public Constant.GameType getGameType() {
        return Constant.GameType.BACCARAT;
    }

    public void setSystemBankerMinimalCoin(int systemBankerMinimalCoin) {
        this.systemBankerMinimalCoin = systemBankerMinimalCoin;
    }

    public void setDealerPayout(BigDecimal dealerPayout) {
        this.dealerPayout = dealerPayout;
    }

    public void setPlayerPayout(BigDecimal playerPayout) {
        this.playerPayout = playerPayout;
    }

    public void setDealerPairPayout(BigDecimal dealerPairPayout) {
        this.dealerPairPayout = dealerPairPayout;
    }

    public void setPlayerPairPayout(BigDecimal playerPairPayout) {
        this.playerPairPayout = playerPairPayout;
    }

    public void setDrawPayout(BigDecimal drawPayout) {
        this.drawPayout = drawPayout;
    }

    @Override
    protected void processGameResult(List<Game.BetType> gameResults) {
        Game.BetType gameResult = gameResults.get(0);
        for (var betMap : betAmountMap.entrySet()) {
            var betMapValue = betMap.getValue();
            for (var entry : betMapValue.entrySet()) {
                var winner = betMap.getKey();
                var loseAmount = BigDecimal.valueOf(entry.getValue());
                BigDecimal winAmount = loseAmount.multiply(getPayout(entry.getKey()));
                if (entry.getKey().equals(gameResult)) {
                    var actualWinAmount = winAmount.multiply(payRate).add(loseAmount).intValue();
                    totalUserWinAmount(winner, actualWinAmount);
                    loseAmount = winAmount.negate();
                } else {
                    loseAmount = loseAmount.multiply(payRate);
                }
                processBanker(loseAmount);
            }
        }

        var cardResult = Game.BaccaratCardResult.newBuilder();
        dealerCards.forEach(card -> cardResult.addDealerCards(getJinliCard(card)));
        playerCards.forEach(card -> cardResult.addPlayerCards(getJinliCard(card)));
        Jinli.GameResultBroadcastMessage.Builder builder = buildBaseGameResultBroadcast();
        builder.setBaccaratCardResult(cardResult);
        cardHistory.add(cardResult.build());
        EventPublisher.publish(new ModifyCoinEvent(userWinCoinMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))));
        EventPublisher.publish(new GameFinishEvent(this, builder.build(), getEveryOneUserBetAmountMap(),gameResults));
    }

    private BigDecimal getPayout(Game.BetType gameResult) {
        switch (gameResult) {
            case BACCARAT_DEALER:
                return dealerPayout;
            case BACCARAT_PLAYER:
                return playerPayout;
            case BACCARAT_DEALER_PAIR:
                return dealerPairPayout;
            case BACCARAT_PLAYER_PAIR:
                return playerPairPayout;
            case BACCARAT_DRAW:
                return drawPayout;
            default:
                throw new IllegalStateException("Unexpected value: " + gameResult);
        }
    }

    protected void dealCards() {
        playerCards.clear();
        dealerCards.clear();
        extraCardNum = 0;
        playerCards.add(deck.deal());
        playerCards.add(deck.deal());

        dealerCards.add(deck.deal());
        dealerCards.add(deck.deal());

        if (getTotalLastPoint(playerCards) < 6) {
            Card thirdPlayerCard = deck.deal();
            playerCards.add(thirdPlayerCard);
            increaseExtraCardNum();
            if (getTotalLastPoint(dealerCards) == 3 || getTotalLastPoint(dealerCards) == 4) {
                if (thirdPlayerCard.getCardValue().equals(Constant.CardNumber.Eight)) {
                    dealerCards.add(deck.deal());
                    increaseExtraCardNum();
                }
            }
        }
        int dealerTotalLastPoint = getTotalLastPoint(dealerCards);
        if (dealerTotalLastPoint < 3 || dealerTotalLastPoint == 5) {
            if (dealerCards.size() < 3) {
                dealerCards.add(deck.deal());
                increaseExtraCardNum();
            }
        }

        if (dealerCards.size() < 3 && playerCards.size() < 3) {
            if (getTotalLastPoint(playerCards) == 6 || getTotalLastPoint(playerCards) == 7) {
                if (getTotalLastPoint(dealerCards) < 6) {
                    dealerCards.add(deck.deal());
                }
            }
        }
    }

    private void increaseExtraCardNum(){
        extraCardNum ++;
    }

    @Override
    public int getDelayFinishTime() {
        return (int) (super.getDelayFinishTime() + (extraCardNum * extraDelayTime));
    }

    public void setExtraDelayTime(long extraDelayTime) {
        this.extraDelayTime = extraDelayTime;
    }

    protected List<Game.BetType> getGameResult() {
        if (dealerCards.get(0).getCardValue().equals(dealerCards.get(1).getCardValue()))
            return Collections.singletonList(Game.BetType.BACCARAT_DEALER_PAIR);
        if (playerCards.get(0).getCardValue().equals(playerCards.get(1).getCardValue()))
            return Collections.singletonList(Game.BetType.BACCARAT_PLAYER_PAIR);
        if (getTotalLastPoint(dealerCards) > getTotalLastPoint(playerCards))
            return Collections.singletonList(BACCARAT_DEALER);
        if (getTotalLastPoint(dealerCards) < getTotalLastPoint(playerCards))
            return Collections.singletonList(Game.BetType.BACCARAT_PLAYER);
        return Collections.singletonList(Game.BetType.BACCARAT_DRAW);
    }

    @Override
    public int getBankerMinimalCoin() {
        return bankerMinimalCoin;
    }

    public void setBankerMinimalCoin(int bankerMinimalCoin) {
        this.bankerMinimalCoin = bankerMinimalCoin;
    }

    @Override
    public int getBankerContinueCoin() {
        return bankerContinueCoin;
    }

    public void setBankerContinueCoin(int bankerContinueCoin) {
        this.bankerContinueCoin = bankerContinueCoin;
    }

    @Override
    protected int getSystemBankerCoin() {
        return systemBankerMinimalCoin;
    }
}