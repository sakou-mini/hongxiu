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
import com.donglaistd.jinli.util.MessageUtil;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.GOLDEN_FLOWER_CARD_SIZE;

@Document
public class GoldenFlower extends BankerGame {
    private static final Logger logger = Logger.getLogger(GoldenFlower.class.getName());

    @Id
    private final ObjectId gameId = ObjectId.get();
    @Transient
    private int bankerMinimalCoin;
    @Transient
    private int systemBankerMinimalCoin;
    @Transient
    private int bankerContinueCoin;
    @Transient
    private int minimalDeckCard;

    @Transient
    private final List<Card> bankerCards = new ArrayList<>();

    @Transient
    private final Map<Game.BetType, List<Card>> typeCards = new LinkedHashMap<>();

    @Transient
    private final List<Game.GoldenFlowerCardResult> cardHistory = new ArrayList<>();

    @Transient
    private final Map<Constant.GoldenType, BigDecimal> payoutMap = new HashMap<>(6);

    @Transient
    private final Map<Game.BetType, Constant.GoldenType> goldenTypeMap = new HashMap<>();

    public GoldenFlower() {
    }

    public GoldenFlower(Deck deck, int minimalDeckCard) {
        this.deck = deck;
        this.minimalDeckCard = minimalDeckCard;
        this.betTypeList = new ArrayList<>(List.of(Game.BetType.SPADE_AREA, Game.BetType.HEART_AREA,
                Game.BetType.CLUB_AREA, Game.BetType.DIAMOND_AREA));
    }

    public void putGoldenTypePayout(Constant.GoldenType goldenType, BigDecimal payout) {
        payoutMap.put(goldenType, payout);
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
    public String getGameId() {
        return gameId.toString();
    }


    @Override
    protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {
        int totalBetNum = 0;
        for (Game.BetType type : betTypeList) {
            totalBetNum += getFuturePayout(type, betType, betAmount);
        }
        return bankerCoinAmount > totalBetNum * payoutMap.get(Constant.GoldenType.Golden_Leopard).intValue();
    }

    @Override
    protected long calculateMaxBetAmount(long betAmount, Game.BetType betType) {
        return betAmount * payoutMap.get(Constant.GoldenType.Golden_Leopard).longValue();
    }

    @Override
    protected int getMinimalDeckCardRequest() {
        return minimalDeckCard;
    }

    @Override
    public Constant.GameType getGameType() {
        return Constant.GameType.GOLDENFLOWER;
    }

    public void setBankerMinimalCoin(int bankerMinimalCoin) {
        this.bankerMinimalCoin = bankerMinimalCoin;
    }


    public void setSystemBankerMinimalCoin(int systemBankerMinimalCoin) {
        this.systemBankerMinimalCoin = systemBankerMinimalCoin;
    }

    public void setBankerContinueCoin(int bankerContinueCoin) {
        this.bankerContinueCoin = bankerContinueCoin;
    }

    @Override
    protected List<Game.BetType> getGameResult() {
        List<Game.BetType> results = new ArrayList<>();
        GoldenFlowerResult bankerResult = GoldenFlowerResult.getGoldenFlowerResult(bankerCards);
        typeCards.forEach((type, cards) -> {
            GoldenFlowerResult typeResult = GoldenFlowerResult.getGoldenFlowerResult(cards);
            goldenTypeMap.put(type, typeResult.getGoldenType());
            if (GoldenFlowerResult.compareResult(typeResult,bankerResult))
                results.add(type);
        });
        return results;
    }

    @Override
    protected void dealCards() {
        if (deck.getLeftCardCount() < minimalDeckCard)
            return;
        goldenTypeMap.clear();
        bankerCards.clear();
        typeCards.clear();
        bankerCards.addAll(deck.dealMultipleCards(GOLDEN_FLOWER_CARD_SIZE));
        betTypeList.forEach(betType -> typeCards.put(betType, deck.dealMultipleCards(GOLDEN_FLOWER_CARD_SIZE)));
    }

    @Override
    protected void processGameResult(List<Game.BetType> gameResults) {
        //1.get bankerCardResult
        GoldenFlowerResult bankerFlowResult = GoldenFlowerResult.getGoldenFlowerResult(bankerCards);
        BigDecimal bankerPayout = payoutMap.get(bankerFlowResult.getGoldenType());
        for (var betEntry : betAmountMap.entrySet()) {
            User player = betEntry.getKey();
            var betMapValues = betEntry.getValue();
            for (var playerBetEntry : betMapValues.entrySet()) {
                Game.BetType betType = playerBetEntry.getKey();
                BigDecimal betCoin = BigDecimal.valueOf(playerBetEntry.getValue());
                BigDecimal payout = payoutMap.get(goldenTypeMap.get(betType));
                BigDecimal winAmount = betCoin.multiply(payout);
                BigDecimal bankerAmount = betCoin.multiply(payout);
                if (gameResults.contains(betType)) {
                    winAmount = winAmount.multiply(payRate).add(betCoin);
                    bankerAmount = bankerAmount.negate().add(betCoin);
                } else {
                    winAmount = betCoin.multiply(bankerPayout).subtract(betCoin).negate();
                    bankerAmount = betCoin.multiply(bankerPayout).multiply(payRate);
                }
                totalUserWinAmount(player, winAmount.longValue());
                logger.fine("player " + player.getAccountName() + " get coin:" + winAmount);
                logger.fine("banker get coin:" + bankerAmount);
                processBanker(bankerAmount);
            }
        }
        Map<User, Long> userBackCoin = calculateUserBackCoin();
        var goldenFlowerCardResult = buildGoldenFlowerCardResult(gameResults);
        cardHistory.add(goldenFlowerCardResult);
        Jinli.GameResultBroadcastMessage.Builder builder = buildBaseGameResultBroadcast();
        builder.setGoldenFlowerCardResult(goldenFlowerCardResult);
        EventPublisher.publish(new ModifyCoinEvent(userBackCoin.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))));
        EventPublisher.publish(new GameFinishEvent(this, builder.build(), getEveryOneUserBetAmountMap(), gameResults));
    }

    public Game.GoldenFlowerCardResult buildGoldenFlowerCardResult(List<Game.BetType> gameResult) {
        var builder = Game.GoldenFlowerCardResult.newBuilder();
        GoldenFlowerResult bankerFlowResult = GoldenFlowerResult.getGoldenFlowerResult(bankerCards);
        var bankerInfo = Game.GoldenFlowerInfo.newBuilder()
                .setGoldenType(bankerFlowResult.getGoldenType()).addAllCards(MessageUtil.getJinliCard(bankerCards));
        builder.setBankerCards(bankerInfo);
        typeCards.forEach((type, cards) -> builder.addBetCards(Game.GoldenFlowerInfo.newBuilder().setIsWin(gameResult.contains(type))
                .addAllCards(MessageUtil.getJinliCard(cards))
                .setBetType(type).setGoldenType(goldenTypeMap.get(type))));
        return builder.build();
    }

    @Override
    public List<? extends Jinli.GameResult> getCardHistory() {
        var result = new ArrayList<Jinli.GameResult>();
        cardHistory.subList(Math.max(0, cardHistory.size() - historyCount), cardHistory.size()).
                forEach(h -> result.add(Jinli.GameResult.newBuilder().setGoldenFlowerCardResult(h).build()));
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GoldenFlower game = (GoldenFlower) o;
        return Objects.equals(gameId, game.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }

}
