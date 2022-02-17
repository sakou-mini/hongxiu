package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.GameConstant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.exception.CardParameterException;
import com.donglaistd.jinli.util.CardRulerUtil;

import java.util.List;

import static com.donglaistd.jinli.Constant.GameType.GOLDENFLOWER;
import static com.donglaistd.jinli.constant.GameConstant.POKER_PAIR_CARD_SIZE;

public class GoldenFlowerResult {
    private final Constant.GoldenType goldenType;
    private final List<Card> cards;

    public Constant.GoldenType getGoldenType() {
        return goldenType;
    }

    public List<Card> getCards() {
        return cards;
    }

    private GoldenFlowerResult(Constant.GoldenType goldenType, List<Card> cards) {
        this.goldenType = goldenType;
        this.cards = cards;
    }

    public static GoldenFlowerResult getInstance(Constant.GoldenType goldenType,List<Card> cards){
        return new GoldenFlowerResult(goldenType, cards);
    }

    public static GoldenFlowerResult getGoldenFlowerResult(List<Card> cards){
        if(cards==null|| cards.size()!= GameConstant.GOLDEN_FLOWER_CARD_SIZE){
            throw new CardParameterException("Your param is error，card size required length："+GameConstant.GOLDEN_FLOWER_CARD_SIZE);
        }
        if(CardRulerUtil.isSameCardNumber(cards))
            return getInstance(Constant.GoldenType.Golden_Leopard, cards);
        else if(CardRulerUtil.isSameCardType(cards) && CardRulerUtil.isGoldenFlowerStraight(cards))
            return getInstance(Constant.GoldenType.Golden_SerialGold, cards);
        else if(CardRulerUtil.isSameCardType(cards))
            return getInstance(Constant.GoldenType.Golden_GoldenFlower, cards);
        else if(CardRulerUtil.isGoldenFlowerStraight(cards))
            return getInstance(Constant.GoldenType.Golden_Straight, cards);
        else if(CardRulerUtil.isPairCards(cards))
            return getInstance(Constant.GoldenType.Golden_Pair, cards);
        else
            return getInstance(Constant.GoldenType.Golden_Single, cards);
    }

    //compare
    public static boolean compareResult(GoldenFlowerResult currentResult, GoldenFlowerResult prevCardsResult) {
        List<Card> bankerCards = prevCardsResult.getCards();
        if (prevCardsResult.getGoldenType().equals(currentResult.getGoldenType()))
            switch (currentResult.getGoldenType()) {
                case Golden_Leopard:
                case Golden_GoldenFlower:
                    return CardRulerUtil.compareMaxCard(currentResult.getCards(),bankerCards,GOLDENFLOWER);
                case Golden_SerialGold:
                case Golden_Straight:
                    return CardRulerUtil.compareCardsForStraight(currentResult.getCards(),prevCardsResult.getCards(),GOLDENFLOWER);
                case Golden_Pair:
                    return CardRulerUtil.compareCardsForPair(currentResult.getCards(),prevCardsResult.getCards(),GOLDENFLOWER,POKER_PAIR_CARD_SIZE);
                case Golden_Single:
                    return CardRulerUtil.compareCardsForOneToOne(currentResult.getCards(),prevCardsResult.getCards(),GOLDENFLOWER);
            }
        return currentResult.getGoldenType().getNumber() > prevCardsResult.getGoldenType().getNumber();
    }
}
