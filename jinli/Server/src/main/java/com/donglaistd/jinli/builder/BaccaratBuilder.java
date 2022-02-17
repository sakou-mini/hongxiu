package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.database.entity.game.Baccarat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class BaccaratBuilder {

    @Value("${baccarat.minimal.deck.card}")
    private int baccaratMinimalDeckCard;
    @Value("${baccarat.banker.minimal.coin}")
    private int bankerMinimalCoin;
    @Value("${baccarat.banker.continue.coin}")
    private int bankerContinueCoin;
    @Value("${baccarat.banker.system.coin}")
    private int systemBankerMinimalCoin;
    @Value("${baccarat.game.history.count}")
    private int historyCount;
    @Value("${baccarat.game.finish.delay}")
    private int baccaratFinishDelay;
    @Value("${baccarat.dealer.payout}")
    private int dealerPayout;
    @Value("${baccarat.dealer.pair.payout}")
    private int dealerPairPayout;
    @Value("${baccarat.player.pair.payout}")
    private int playerPairPayout;
    @Value("${baccarat.draw.payout}")
    private int drawPayout;
    @Value("${baccarat.player.payout}")
    private int playerPayout;
    @Value("${baccarat.betting.pay.rate}")
    private double payRate;
    @Value("${baccarat.betting.time}")
    private long bettingTime;
    @Value("${baccarat.game.extra.delay}")
    private long extraDelayTime;
    public Baccarat create(boolean openBanker) {
        var game = new Baccarat(DeckBuilder.getNoJokerDeck(8), baccaratMinimalDeckCard);
        game.setBankerMinimalCoin(bankerMinimalCoin);
        game.setBankerContinueCoin(bankerContinueCoin);
        game.setSystemBankerMinimalCoin(systemBankerMinimalCoin);
        game.setHistoryCount(historyCount);
        game.setDelayFinishTime(baccaratFinishDelay);
        game.setDealerPayout(BigDecimal.valueOf(dealerPayout));
        game.setPlayerPayout(BigDecimal.valueOf(playerPayout));
        game.setDealerPairPayout(BigDecimal.valueOf(dealerPairPayout));
        game.setPlayerPairPayout(BigDecimal.valueOf(playerPairPayout));
        game.setDrawPayout(BigDecimal.valueOf(drawPayout));
        game.setPayRate(BigDecimal.valueOf(payRate));
        game.setBettingTime(bettingTime);
        game.setOpenBanker(openBanker);
        game.setExtraDelayTime(extraDelayTime);
        return game;
    }
}
