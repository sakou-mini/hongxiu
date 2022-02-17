package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.RedBlack;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;

import static com.donglaistd.jinli.Game.BetType.BLACK;
import static com.donglaistd.jinli.Game.BetType.RED;

@Component
public class RedBlackBuilder {

    @Value("${redblack.betting.red.odds}")
    private int redOdds;
    @Value("${redblack.betting.black.odds}")
    private int blackOdds;
    @Value("${redblack.betting.leopard.odds}")
    private int leopardOdds;
    @Value("${redblack.betting.flushFlowers.odds}")
    private int flushFlowersOdds;
    @Value("${redblack.betting.sameFlowers.odds}")
    private int sameFlowersOdds;
    @Value("${redblack.betting.flush.odds}")
    private int flushOdds;
    @Value("${redblack.betting.pair.odds}")
    private int pairOdds;


    @Value("${redblack.minimal.deck.card}")
    private int redBlackMinimalDeckCard;
    @Value("${redblack.game.history.count}")
    private int historyCount;

    @Value("${redblack.betting.time}")
    private int bettingTieme;

    @Value("${redblack.game.finish.delay}")
    private int finishDelay;

    @Value("${redblack.betting.pay.rate}")
    private String payRate;
    @Value("${redblack.banker.minimal.coin}")
    private int minimalCoin;
    @Value("${redblack.banker.system.coin}")
    private int systemCoin;
    @Value("${redblack.banker.continue.coin}")
    private int continueCoin;
    @Value("${redblack.betting.time}")
    private long bettingTime;

    public RedBlack create(boolean openBanker) {
        RedBlack redBlack = new RedBlack(DeckBuilder.getOneNoJokerDeck(), redBlackMinimalDeckCard);

        redBlack.setPayRate(new BigDecimal(payRate));
        redBlack.setHistoryCount(historyCount);
        redBlack.setBankerContinueCoin(continueCoin);
        redBlack.setBankerMinimalCoin(minimalCoin);
        redBlack.setSystemBankerMinimalCoin(systemCoin);

        redBlack.setDelayFinishTime(finishDelay);

        var betOddsMap = new HashMap<Game.BetType, Integer>();
        betOddsMap.put(RED, redOdds);
        betOddsMap.put(BLACK, blackOdds);
        redBlack.setBetOddsMap(betOddsMap);

        var typeOddsMap = new HashMap<Constant.RedBlackType,Integer>();
        typeOddsMap.put(Constant.RedBlackType.Leopard, leopardOdds);
        typeOddsMap.put(Constant.RedBlackType.Flush_Flowers, flushFlowersOdds);
        typeOddsMap.put(Constant.RedBlackType.SAME_Flowers, sameFlowersOdds);
        typeOddsMap.put(Constant.RedBlackType.Flush, flushOdds);
        typeOddsMap.put(Constant.RedBlackType.Pair, pairOdds);
        redBlack.setTypeOddsMap(typeOddsMap);

        redBlack.setHistoryCount(historyCount);
        redBlack.setBettingTime(bettingTime);
        redBlack.setOpenBanker(openBanker);
        return redBlack;
    }
}
