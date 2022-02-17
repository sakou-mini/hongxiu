package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.game.GoldenFlower;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GoldenFlowerBuilder {
    @Value("${goldenflower.minimal.deck.card}")
    private int minimalDeckCard;
    @Value("${goldenflower.betting.pay.rate}")
    private double payRate;
    @Value("${goldenflower.banker.minimal.coin}")
    private int bankerMinimalCoin;
    @Value("${goldenflower.banker.continue.coin}")
    private int bankerContinueCoin;
    @Value("${goldenflower.banker.system.coin}")
    private int systemBankerMinimalCoin;
    @Value("${goldenflower.game.history.count}")
    private int historyCount;

    @Value("${goldenflower.game.finish.delay}")
    private int finishDelay;

    @Value("${goldenflower.single.payout}")
    private int singlePayout;
    @Value("${goldenflower.pair.payout}")
    private int pairPayout;
    @Value("${goldenflower.straight.payout}")
    private int straightPayout;
    @Value("${goldenflower.goldenflower.payout}")
    private int goldenflowerPayout;
    @Value("${goldenflower.serialgold.payout}")
    private int serialgoldPayout;
    @Value("${goldenflower.leopard.payout}")
    private int leopardPayout;
    @Value("${goldenflower.betting.time}")
    private long bettingTime;


    public GoldenFlower create(boolean openBanker) {
        GoldenFlower goldenFlower = new GoldenFlower(DeckBuilder.getNoJokerDeck(1), minimalDeckCard);
        goldenFlower.setPayRate(BigDecimal.valueOf(payRate));
        goldenFlower.setBankerMinimalCoin(bankerMinimalCoin);
        goldenFlower.setBankerContinueCoin(bankerContinueCoin);
        goldenFlower.setSystemBankerMinimalCoin(systemBankerMinimalCoin);
        goldenFlower.setHistoryCount(historyCount);
        goldenFlower.setDelayFinishTime(finishDelay);
        goldenFlower.setBettingTime(bettingTime);
        goldenFlower.setOpenBanker(openBanker);
        putGoldenTypePayOut(goldenFlower);
        return goldenFlower;
    }

    private void putGoldenTypePayOut(GoldenFlower goldenFlower) {
        goldenFlower.putGoldenTypePayout(Constant.GoldenType.Golden_Leopard, BigDecimal.valueOf(leopardPayout));
        goldenFlower.putGoldenTypePayout(Constant.GoldenType.Golden_SerialGold, BigDecimal.valueOf(serialgoldPayout));
        goldenFlower.putGoldenTypePayout(Constant.GoldenType.Golden_GoldenFlower, BigDecimal.valueOf(goldenflowerPayout));
        goldenFlower.putGoldenTypePayout(Constant.GoldenType.Golden_Straight, BigDecimal.valueOf(straightPayout));
        goldenFlower.putGoldenTypePayout(Constant.GoldenType.Golden_Pair, BigDecimal.valueOf(pairPayout));
        goldenFlower.putGoldenTypePayout(Constant.GoldenType.Golden_Single, BigDecimal.valueOf(pairPayout));
    }
}
