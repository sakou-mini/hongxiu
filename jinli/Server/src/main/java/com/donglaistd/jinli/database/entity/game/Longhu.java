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
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.getJinliCard;

@Document
@Configurable
public class Longhu extends BaseGame {
    @Id
    private final ObjectId gameId = ObjectId.get();
    @Transient
    private final List<Game.LonghuCardResult> cardHistory = new ArrayList<>();
    @Transient
    private int minimalDeckCardRequest;
    @Transient
    private Map<Game.BetType, Integer> betLimitMap = new HashMap<>();
    @Transient
    private Card longCard;
    @Transient
    private Card huCard;

    public Longhu() {

    }


    public Longhu(Deck deck, int minimalDeckCardRequest) {
        this.deck = deck;
        betTypeList = new ArrayList<>(List.of(Game.BetType.LONG, Game.BetType.HU, Game.BetType.LONGHU_DRAW));
        this.minimalDeckCardRequest = minimalDeckCardRequest;
    }

    public String getGameId() {
        return gameId.toString();
    }

    public void setBetLimitMap(Map<Game.BetType, Integer> betLimitMap) {
        this.betLimitMap = betLimitMap;
    }

    @Override
    protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {
        if (!betLimitMap.containsKey(betType)) return false;
        var limit = betLimitMap.get(betType);
        return limit > getBetAmount(user, betType) + betAmount;
    }

    @Override
    public Constant.GameType getGameType() {
        return Constant.GameType.LONGHU;
    }

    @Override
    protected void processGameResult(List<Game.BetType> gameResults) {
        Game.BetType gameResult = gameResults.get(0);
        var betTotal = new BigDecimal(0);
        var winnerMap = new HashMap<User, BigDecimal>();

        for (var betMap : betAmountMap.entrySet()) {
            var betMapValue = betMap.getValue();
            for (var entry : betMapValue.entrySet()) {
                if (entry.getKey().equals(gameResult)) {
                    winnerMap.put(betMap.getKey(), BigDecimal.valueOf(entry.getValue()));
                }
                betTotal = betTotal.add(BigDecimal.valueOf(entry.getValue()));
            }
        }

        betTotal = betTotal.multiply(payRate);
        var winnerBetTotal = new BigDecimal(0);

        for (var winnerBetAmount : winnerMap.values()) {
            winnerBetTotal = winnerBetTotal.add(winnerBetAmount);
        }

        var builder = Jinli.GameResultBroadcastMessage.newBuilder();
        builder.setRoomId(owner.getRoomId());
        var cardResult = Game.LonghuCardResult.newBuilder();
        cardResult.setLongCard(getJinliCard(longCard));
        cardResult.setHuCard(getJinliCard(huCard));
        builder.setLonghuCardResult(cardResult);
        cardHistory.add(cardResult.build());
        for (var entry : winnerMap.entrySet()) {
            var winner = entry.getKey();
            int winAmount = betTotal.multiply(entry.getValue()).divide(winnerBetTotal, RoundingMode.DOWN).intValue();
            totalUserWinAmount(winner, winAmount);
        }
        userWinCoinMap.forEach((user, coin) -> builder.addWinnerAndAmount(Jinli.WinnerAndAmount.newBuilder().setWinAmount(coin)
                .setUserId(String.valueOf(user.getId())).setUserCoin(user.getGameCoin()).setDisPlayName(user.getDisplayName())));
        EventPublisher.publish(new ModifyCoinEvent(userWinCoinMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue))));
        EventPublisher.publish(new GameFinishEvent(this, builder.build(), getEveryOneUserBetAmountMap(), gameResults));
    }

    @Override
    protected void dealCards() {
        longCard = deck.deal();
        huCard = deck.deal();
    }

    protected List<Game.BetType> getGameResult() {
        if (getLonghuValue(longCard) > getLonghuValue(huCard))
            return Collections.singletonList(Game.BetType.LONG);
        if (getLonghuValue(longCard) < getLonghuValue(huCard))
            return Collections.singletonList(Game.BetType.HU);
        return Collections.singletonList(Game.BetType.LONGHU_DRAW);
    }

    private int getLonghuValue(Card card) {
        if (card.getCardValue().equals(Constant.CardNumber.Ace)) {
            return 0;
        }
        return card.getCardIntValue();
    }


    @Override
    protected int getMinimalDeckCardRequest() {
        return minimalDeckCardRequest;
    }

    @Override
    public List<Jinli.GameResult> getCardHistory() {
        var result = new ArrayList<Jinli.GameResult>();
        cardHistory.subList(Math.max(0, cardHistory.size() - historyCount), cardHistory.size()).
                forEach(h -> result.add(Jinli.GameResult.newBuilder().setLonghuCardResult(h).build()));
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Longhu game = (Longhu) o;
        return Objects.equals(gameId, game.gameId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId);
    }

    @Override
    public String toString() {
        return "Longhu{" + "gameId=" + gameId + '}';
    }
}
