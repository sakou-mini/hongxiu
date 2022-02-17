package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.builder.FriedGoldenFlowerBuilder;
import com.donglaistd.jinli.database.entity.game.texas.CardsGroup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.donglaistd.jinli.Constant.CardNumber.*;
import static com.donglaistd.jinli.Constant.CardType.*;
import static com.donglaistd.jinli.Constant.GoldenType.*;

public class GoldenFlowerCardUtilTest extends BaseTest {
    @Autowired
    private FriedGoldenFlowerBuilder builder;
    @Test
    public void testGoldenFlowerCardUtil() {
        List<Card> cards = new ArrayList<>();
        Collections.addAll(cards, new Card(Four, Spade), new Card(Four, Heart), new Card(Four, Club));
        CardsGroup<Constant.GoldenType, List<Card>> group = GoldenFlowerCardUtil.getCardsGroupType(cards);
        Assert.assertEquals(Golden_Leopard,group.getTexasType());

        cards.clear();
        Collections.addAll(cards, new Card(Four, Spade), new Card(Five, Spade), new Card(Six, Spade));
        group = GoldenFlowerCardUtil.getCardsGroupType(cards);
        Assert.assertEquals(Golden_SerialGold,group.getTexasType());

        cards.clear();
        Collections.addAll(cards, new Card(Ten, Spade), new Card(Five, Spade), new Card(Jack, Spade));
        group = GoldenFlowerCardUtil.getCardsGroupType(cards);
        Assert.assertEquals(Golden_GoldenFlower,group.getTexasType());

        cards.clear();
        Collections.addAll(cards, new Card(Ten, Club), new Card(Nine, Heart), new Card(Jack, Spade));
        group = GoldenFlowerCardUtil.getCardsGroupType(cards);
        Assert.assertEquals(Golden_Straight,group.getTexasType());

        cards.clear();
        Collections.addAll(cards, new Card(Jack, Club), new Card(Nine, Heart), new Card(Jack, Spade));
        group = GoldenFlowerCardUtil.getCardsGroupType(cards);
        Assert.assertEquals(Golden_Pair,group.getTexasType());
    }


    @Test
    public void testCompareValue() {
        List<Card> cards1 = new ArrayList<>();
        List<Card> cards2 = new ArrayList<>();
        List<Card> cards3 = new ArrayList<>();
        Collections.addAll(cards1, new Card(Four, Spade), new Card(Four, Heart), new Card(Four, Club));

        Collections.addAll(cards2, new Card(Ten, Club), new Card(Nine, Heart), new Card(Jack, Spade));

        Collections.addAll(cards3, new Card(Ten, Club), new Card(Nine, Diamond), new Card(Jack, Club));
        // 比较牌型 List1,List2比较大小 List1>List2返回1、List1等于List2返回0、List1<List2返回-1
        Assert.assertEquals(1, GoldenFlowerCardUtil.compareValue(cards1, cards2));
        Assert.assertEquals(0, GoldenFlowerCardUtil.compareValue(cards2, cards3));
        Assert.assertEquals(-1, GoldenFlowerCardUtil.compareValue(cards3, cards1));

    }
}
