package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.BullBull;
import com.donglaistd.jinli.database.entity.game.BullRulerResult;
import com.donglaistd.jinli.util.MessageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BullBullBuilder {
    @Value("${bullbull.minimal.deck.card}")
    private int bullbullMinimalDeckCard;

    @Value("${bullbull.banker.minimal.coin}")
    private int bankerMinimalCoin;

    @Value("${bullbull.banker.system.coin}")
    private int systemBankerMinimalCoin;

    @Value("${bullbull.banker.continue.coin}")
    private int bankerContinueCoin;

    @Value("${bullbull.game.history.count}")
    private int historyCount;

    @Value("${bullbull.game.init.card.show.num}")
    private int initShowNum;

    @Value("${bullbull.non.to.six.payout}")
    private int nonBullToSixBullPayout;

    @Value("${bullbull.seven.to.nine.payout}")
    private int sevenBullToNineBullPayout;

    @Value("${bullbull.over.bull.payout}")
    private int overBullPayout;

    @Value("${bullbull.betting.pay.rate}")
    private double payRate;

    @Value("${bullbull.game.finish.delay}")
    private int bullbullFinishDelay;

    @Value("${bullbull.betting.time}")
    private long bettingTime;
    @Value("${bullbull.game.dealCard.animation.time}")
    private long dealCardAnimationTime;

    public BullBull create(boolean openBanker) {
        BullBull bullBull = new BullBull(DeckBuilder.getNoJokerDeck(1), bullbullMinimalDeckCard);
        bullBull.setSystemBankerMinimalCoin(systemBankerMinimalCoin);
        bullBull.setBankerMinimalCoin(bankerMinimalCoin);
        bullBull.setBankerContinueCoin(bankerContinueCoin);
        bullBull.setHistoryCount(historyCount);
        bullBull.setInitShowNum(initShowNum);
        bullBull.setNonBullToSixBullPayout(nonBullToSixBullPayout);
        bullBull.setSevenBullToNineBullPayout(sevenBullToNineBullPayout);
        bullBull.setOverBullPayout(overBullPayout);
        bullBull.setPayRate(BigDecimal.valueOf(payRate));
        bullBull.setDelayFinishTime(bullbullFinishDelay);
        bullBull.setBettingTime(bettingTime);
        bullBull.setOpenBanker(openBanker);
        bullBull.setDealCardAnimationTime(dealCardAnimationTime);
        return bullBull;
    }

    static public Game.BullCardInfo.Builder getJinliBullBullCard(BullBull bullbull, List<Card> cardList) {
        var builder = Game.BullCardInfo.newBuilder();
        builder.setBullType(BullRulerResult.getBullResult(cardList).getBullType()).setIsWin(bullbull.compareWithBanker(cardList));
        cardList.forEach(card -> builder.addCards(MessageUtil.getJinliCard(card)));
        return builder;
    }

    //build the BullBull showCard
    static public Game.BullbullCardShow.Builder buildCardShowBroadcastMessage(BullBull bullbull, int showNum) {
        if (showNum > bullbull.getBankerCards().size())
            showNum = bullbull.getBankerCards().size();
        var bullCardShowBuilder = Game.BullbullCardShow.newBuilder();
        bullCardShowBuilder.addAllBankerCards(MessageUtil.getJinliCard(bullbull.getBankerCards().subList(0, showNum)));
        bullCardShowBuilder.addAllSpadeCards(MessageUtil.getJinliCard(bullbull.getCardsByType(Game.BetType.SPADE_AREA).subList(0, showNum)));
        bullCardShowBuilder.addAllHeartCards(MessageUtil.getJinliCard(bullbull.getCardsByType(Game.BetType.HEART_AREA).subList(0, showNum)));
        bullCardShowBuilder.addAllClubCards(MessageUtil.getJinliCard(bullbull.getCardsByType(Game.BetType.CLUB_AREA).subList(0, showNum)));
        bullCardShowBuilder.addAllDiamondCards(MessageUtil.getJinliCard(bullbull.getCardsByType(Game.BetType.DIAMOND_AREA).subList(0, showNum)));
        return bullCardShowBuilder;
    }

}
