package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.texas.CardsGroup;

import java.util.Collections;
import java.util.List;

import static com.donglaistd.jinli.Constant.GameType.GOLDENFLOWER;
import static com.donglaistd.jinli.Constant.GoldenType.*;
import static com.donglaistd.jinli.constant.GameConstant.POKER_PAIR_CARD_SIZE;

public class GoldenFlowerCardUtil {
    public static CardsGroup<Constant.GoldenType,List<Card>> getCardsGroupType(List<Card> list) {
        Constant.GoldenType type;
        if (CardRulerUtil.isSameCardNumber(list)) {
            type = Golden_Leopard;
        } else if (CardRulerUtil.isSameCardType(list)&&CardRulerUtil.isGoldenFlowerStraight(list)) {
            type = Golden_SerialGold;
        } else if (CardRulerUtil.isSameCardType(list)) {
            type = Golden_GoldenFlower;
        } else if (CardRulerUtil.isGoldenFlowerStraight(list)) {
            type = Golden_Straight;
        } else if (CardRulerUtil.isPairCards(list)) {
            type = Golden_Pair;
        } else {
            type = Golden_Single;
        }
        return new CardsGroup<>(type, list);
    }

    // 比较牌型 List1,List2比较大小 List1>List2返回1、List1等于List2返回0、List1<List2返回-1
    public static int compareValue(List<Card> list1, List<Card> list2) {
        CardsGroup<Constant.GoldenType, List<Card>> listNew = getCardsGroupType(list1);
        CardsGroup<Constant.GoldenType, List<Card>> listOld = getCardsGroupType(list2);
        Constant.GoldenType newType = listNew.getTexasType();
        Constant.GoldenType oldType = listOld.getTexasType();
        List<Card> cardsList1 = listNew.getCardsList();
        List<Card> cardsList2 = listOld.getCardsList();
        Collections.sort(cardsList1);
        Collections.sort(cardsList2);
        if ((newType.getNumber() - oldType.getNumber()) > 0) {
            return 1;
        } else if ((newType.getNumber() - oldType.getNumber()) < 0) {
            return -1;
        } else {
            switch (newType) {
                case Golden_Leopard:
                case Golden_SerialGold:
                case Golden_Straight:
                    return compareStraightCards(cardsList1, cardsList2);
                case Golden_GoldenFlower:
                case Golden_Single:
                    return compareOneByOne(cardsList1, cardsList2);
                case Golden_Pair:
                    if (CardRulerUtil.isSameCardPoints(cardsList1,cardsList2,GOLDENFLOWER)) return 0;
                    else return CardRulerUtil.compareCardsForPair(cardsList1, cardsList2,GOLDENFLOWER,POKER_PAIR_CARD_SIZE) ? 1 : -1;
            }
        }
        return 0;
    }
    public static int compareStraightCards(List<Card> list1, List<Card> list2) {
        if(CardRulerUtil.isSameCardPoints(list1,list2,GOLDENFLOWER)) return 0;
        return CardRulerUtil.compareCardsForStraight(list1, list2,GOLDENFLOWER) ? 1:-1;
    }

    public static int compareOneByOne(List<Card> list1, List<Card> list2) {
        if(CardRulerUtil.isSameCardPoints(list1,list2,GOLDENFLOWER)) return 0;
        return CardRulerUtil.compareCardsForOneToOne(list1, list2, GOLDENFLOWER) ? 1 : -1;
    }
}
