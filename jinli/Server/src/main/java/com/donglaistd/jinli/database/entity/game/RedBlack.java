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
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.getJinliCard;

@Document
@Configurable
public class RedBlack extends BankerGame {
    private static final Logger logger = Logger.getLogger(RedBlack.class.getName());
    @Id
    private final ObjectId gameId = ObjectId.get();
    @Transient
    private final List<Game.RedBlackCardResult> cardHistory = new ArrayList<>();
    @Transient
    private final List<Card> redCard = new ArrayList<>();
    @Transient
    private final List<Card> blackCard = new ArrayList<>();
    @Transient
    Constant.RedBlackType winType;
    @Transient
    private int minimalDeckCardRequest;
    @Transient
    private int bankerMinimalCoin;
    @Transient
    private int systemBankerMinimalCoin;
    @Transient
    private int bankerContinueCoin;
    @Transient
    private Map<Game.BetType, Integer> betOddsMap = new HashMap<>();
    @Transient
    private Map<Constant.RedBlackType, Integer> typeOddsMap = new HashMap<>();

    public RedBlack() {
    }

    public RedBlack(Deck deck, int minimalDeckCardRequest) {
        this.deck = deck;
        this.minimalDeckCardRequest = minimalDeckCardRequest;
        betTypeList = new ArrayList<>(List.of(Game.BetType.RED, Game.BetType.BLACK, Game.BetType.RED_BLACK_LUCk));
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

    public void setSystemBankerMinimalCoin(int systemBankerMinimalCoin) {
        this.systemBankerMinimalCoin = systemBankerMinimalCoin;
    }

    @Override
    public synchronized void beginGameLoop(long bettingTime) {
        super.beginGameLoop(bettingTime);
    }

    @Override
    public String getGameId() {
        return gameId.toString();
    }

    public void setBetOddsMap(Map<Game.BetType, Integer> betOddsMap) {
        this.betOddsMap = betOddsMap;
    }

    public void setTypeOddsMap(Map<Constant.RedBlackType, Integer> typeOddsMap) {
        this.typeOddsMap = typeOddsMap;
    }

    @Override
    protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {

        var batMap = getBetTypeAmountMap();
        var all = batMap.values().stream().mapToLong(Long::longValue).sum();
        var x = batMap.get(Game.BetType.RED) == null ? 0 : batMap.get(Game.BetType.RED);
        var y = batMap.get(Game.BetType.BLACK) == null ? 0 : batMap.get(Game.BetType.BLACK);
        var z = all - x - y;
        if (x > y)
            return (bankerCoinAmount + y) >= (x + 10 * z);
        else if (x < y)
            return (bankerCoinAmount + x) >= (y + 10 * z);
        else
            return (bankerCoinAmount + x) > (y + 10 * z);
    }

    @Override
    public Constant.ResultCode checkBetType(User user, Game.BetType betType) {
        Constant.ResultCode checkBet = super.checkBetType(user, betType);
        if (checkBet != Constant.ResultCode.SUCCESS) {
            return checkBet;
        }
        if (!this.betAmountMap.containsKey(user)) {
            if (betType == Game.BetType.RED || betType == Game.BetType.BLACK) {
                return Constant.ResultCode.SUCCESS;
            }
            return Constant.ResultCode.BETTYPE_COMBINATION_ERROR;
        }
        var userBet = this.betAmountMap.get(user);
        if (betType == Game.BetType.RED) {
            return userBet.containsKey(Game.BetType.BLACK) ? Constant.ResultCode.BETTYPE_MUTUAL_EXCLUSION : Constant.ResultCode.SUCCESS;
        } else if (betType == Game.BetType.BLACK) {
            return userBet.containsKey(Game.BetType.RED) ? Constant.ResultCode.BETTYPE_MUTUAL_EXCLUSION : Constant.ResultCode.SUCCESS;
        }
        if (userBet.containsKey(Game.BetType.RED) || userBet.containsKey(Game.BetType.BLACK)) {
            return Constant.ResultCode.SUCCESS;
        }
        return Constant.ResultCode.BETTYPE_COMBINATION_ERROR;
    }

    @Override
    protected int getMinimalDeckCardRequest() {
        return this.minimalDeckCardRequest;
    }

    @Override
    public Constant.GameType getGameType() {
        return Constant.GameType.REDBLACK;
    }

    private boolean canAddLuck(Constant.RedBlackType type, List<Card> cards) {
        switch (type) {
            case Leopard:
            case SAME_Flowers:
            case Flush_Flowers:
            case Flush:
                return true;
            case Single:
                return false;
            case Pair:
                return cards.get(0).getCardValue().compareTo(Constant.CardNumber.Eight) > 0;
            default:
                return false;
        }
    }

    @Override
    protected List<Game.BetType> getGameResult() {
        winType = null;
        RedBlackResult redResult = RedBlackResult.getRedBlackResult(redCard);
        RedBlackResult blackResult = RedBlackResult.getRedBlackResult(blackCard);
        List<Game.BetType> result = new ArrayList<>();
        if (redResult.getRedBlackType().compareTo(blackResult.getRedBlackType()) < 0) {
            // RED
            result.add(Game.BetType.RED);
            if (canAddLuck(redResult.getRedBlackType(), redResult.getCards())) {
                result.add(getBetType(redResult.getRedBlackType()));
                winType = redResult.getRedBlackType();
            }
        } else if (redResult.getRedBlackType().compareTo(blackResult.getRedBlackType()) > 0) {
            // BLACK
            result.add(Game.BetType.BLACK);
            if (canAddLuck(blackResult.getRedBlackType(), blackResult.getCards())) {
                result.add(getBetType(blackResult.getRedBlackType()));
                winType = blackResult.getRedBlackType();
            }
        } else {
            // SAME TYPE
            switch (redResult.getRedBlackType()) {
                case Leopard:
                case SAME_Flowers:
                    result.add(compareCardValue(redResult.getCards(), blackResult.getCards()));
                    result.add(getBetType(redResult.getRedBlackType()));
                    winType = redResult.getRedBlackType();
                    break;
                case Single:
                    result.add(compareCardValue(redResult.getCards(), blackResult.getCards()));
                    break;
                case Pair:
                    Game.BetType type = compareCardValue(redResult.getCards(), blackResult.getCards());
                    result.add(type);
                    Card c = type == Game.BetType.RED ? redResult.getCards().get(0) : blackResult.getCards().get(0);
                    if (c.getCardValue().compareTo(Constant.CardNumber.Eight) > 0) {
                        result.add(getBetType(redResult.getRedBlackType()));
                        winType = redResult.getRedBlackType();
                    }
                    break;

                case Flush_Flowers:
                case Flush:
                    result.add(compareFlush(redResult.getCards(), blackResult.getCards()));
                    result.add(getBetType(redResult.getRedBlackType()));
                    winType = redResult.getRedBlackType();
                    break;
            }
        }
        return result;
    }

    private Game.BetType getBetType(Constant.RedBlackType type) {
        return Game.BetType.RED_BLACK_LUCk;
    }

    private Game.BetType compareFlush(List<Card> red, List<Card> black) {
        boolean redMin = false;
        boolean blackMin = false;
        // A23
        if (red.get(0).getCardValue() == Constant.CardNumber.Ace &&
                red.get(1).getCardValue() == Constant.CardNumber.Three &&
                red.get(2).getCardValue() == Constant.CardNumber.Two) {
            redMin = true;
        }
        if (black.get(0).getCardValue() == Constant.CardNumber.Ace &&
                black.get(1).getCardValue() == Constant.CardNumber.Three &&
                black.get(2).getCardValue() == Constant.CardNumber.Two) {
            blackMin = true;
        }
        if (redMin && blackMin) {
            if (red.get(0).getCardType().compareTo(black.get(0).getCardType()) < 0) {
                return Game.BetType.RED;
            }
            return Game.BetType.BLACK;
        } else if (redMin) {
            return Game.BetType.BLACK;
        } else if (blackMin) {
            return Game.BetType.RED;
        } else {
            return compareCardValue(red, black);
        }

    }

    private Game.BetType compareCardValue(List<Card> red, List<Card> black) {
        var result = compareList(red, black, 0);

        if (result != null) {
            return result;
        }
        if (red.get(0).getCardType().compareTo(black.get(0).getCardType()) < 0) {
            return Game.BetType.RED;
        }
        return Game.BetType.BLACK;

    }

    private Game.BetType compareList(List<Card> red, List<Card> black, int index) {
        if (index >= red.size() || index >= black.size()) {
            return null;
        }
        if (red.get(index).getCardValue().compareTo(black.get(index).getCardValue()) > 0) {
            return Game.BetType.RED;
        } else if (red.get(index).getCardValue().compareTo(black.get(index).getCardValue()) < 0) {
            return Game.BetType.BLACK;
        } else {
            return compareList(red, black, index + 1);
        }

    }

    @Override
    protected void dealCards() {
        // 发牌
        redCard.clear();
        blackCard.clear();
        for (int i = 0; i < 3; i++) {
            redCard.add(deck.deal());
        }
        for (int i = 0; i < 3; i++) {
            blackCard.add(deck.deal());
        }
        gameStatus = Constant.GameStatus.BETTING;
    }

    @Override
    protected void processGameResult(List<Game.BetType> gameResults) {
        var cardResult = Game.RedBlackCardResult.newBuilder();
        redCard.forEach(card -> cardResult.addRedCards(getJinliCard(card)));
        blackCard.forEach(card -> cardResult.addBlackCards(getJinliCard(card)));
        cardResult.addAllWinAreaType(gameResults);
        cardHistory.add(cardResult.build());
        while (cardHistory.size() > historyCount) {
            cardHistory.remove(0);
        }
        for (var betMap : betAmountMap.entrySet()) {
            var winner = betMap.getKey();
            var betMapValue = betMap.getValue();
            if (!betMapValue.containsKey(gameResults.get(0))) {
                var loseAmount = BigDecimal.valueOf(betMapValue.values().stream().mapToLong(Long::longValue).sum());
                loseAmount = loseAmount.multiply(payRate);
                processBanker(loseAmount);
                continue;
            }
            for (var entry : betMapValue.entrySet()) {
                var loseAmount = BigDecimal.valueOf(entry.getValue());
                if (gameResults.contains(entry.getKey())) {
                    int betOdds = 0;
                    if (entry.getKey() == Game.BetType.RED_BLACK_LUCk) {
                        betOdds = this.typeOddsMap.get(winType);
                    } else {
                        betOdds = this.betOddsMap.get(entry.getKey());
                    }

                    BigDecimal winAmount = loseAmount.multiply(new BigDecimal(betOdds));
                    var actualWinAmount = winAmount.multiply(payRate).add(loseAmount).intValue();
                    totalUserWinAmount(winner, actualWinAmount);
                    loseAmount = winAmount.negate();
                } else {
                    totalUserWinAmount(winner, loseAmount.negate().intValue()); 
                    loseAmount = loseAmount.multiply(payRate);
                }
                processBanker(loseAmount);
            }
        }
        var builder = buildBaseGameResultBroadcast().setRedBlackCardResult(cardResult);
        EventPublisher.publish(new ModifyCoinEvent(userWinCoinMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))));
        EventPublisher.publish(new GameFinishEvent(this, builder.build(), getEveryOneUserBetAmountMap(), gameResults));
    }

    public int getTotalAmount() {
        int total = 0;
        for (var perUserBetAmount : betAmountMap.entrySet()) {
            for (var betAmount : perUserBetAmount.getValue().entrySet()) {
                total += betAmount.getValue();
            }
        }
        return total;
    }


    @Override
    public List<? extends Jinli.GameResult> getCardHistory() {
        var result = new ArrayList<Jinli.GameResult>();
        cardHistory.subList(Math.max(0, cardHistory.size() - historyCount), cardHistory.size()).
                forEach(h -> result.add(Jinli.GameResult.newBuilder().setRedBlackCardResult(h).build()));
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedBlack game = (RedBlack) o;
        return Objects.equals(gameId, game.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }
}
