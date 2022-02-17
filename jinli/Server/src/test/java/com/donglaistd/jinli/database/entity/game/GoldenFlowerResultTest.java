package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.util.CardRulerUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.donglaistd.jinli.Constant.GameType.GOLDENFLOWER;
import static com.donglaistd.jinli.constant.GameConstant.POKER_PAIR_CARD_SIZE;

public class GoldenFlowerResultTest extends BaseTest {

    @Test
    public void GoldenFlowerResultTest(){
        //Golden_Pair test
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card2,card3,card1));
        GoldenFlowerResult result1 = GoldenFlowerResult.getGoldenFlowerResult(cardList1);
        Assert.assertEquals(Constant.GoldenType.Golden_Pair,result1.getGoldenType());

        Card card4 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        Card card5 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        Card card6 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card4,card5,card6));
        GoldenFlowerResult result2 = GoldenFlowerResult.getGoldenFlowerResult(cardList2);
        Assert.assertEquals(Constant.GoldenType.Golden_GoldenFlower,result2.getGoldenType());

        Card card7 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);
        Card card8 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        Card card9 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        List<Card> cardList3 = new ArrayList<>(Arrays.asList(card7,card8,card9));
        GoldenFlowerResult result3 = GoldenFlowerResult.getGoldenFlowerResult(cardList3);
        Assert.assertEquals(Constant.GoldenType.Golden_SerialGold,result3.getGoldenType());

        Card card10 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);
        Card card11 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card card12 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        List<Card> cardList4 = new ArrayList<>(Arrays.asList(card10,card11,card12));
        GoldenFlowerResult result4 = GoldenFlowerResult.getGoldenFlowerResult(cardList4);
        Assert.assertEquals(Constant.GoldenType.Golden_Straight,result4.getGoldenType());

        Card card13 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);
        Card card14 = new Card(Constant.CardNumber.Ten, Constant.CardType.Spade);
        Card card15 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        List<Card> cardList5 = new ArrayList<>(Arrays.asList(card13,card14,card15));
        GoldenFlowerResult result5 = GoldenFlowerResult.getGoldenFlowerResult(cardList5);
        Assert.assertEquals(Constant.GoldenType.Golden_Single,result5.getGoldenType());
    }

    @Test
    public void compareGoldenLeopardTest(){
        Card card1 = new Card(Constant.CardNumber.Nine, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Nine, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Nine, Constant.CardType.Club);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
        GoldenFlowerResult result1 = GoldenFlowerResult.getGoldenFlowerResult(cardList1);

        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card5 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card4, card5, card6));
        GoldenFlowerResult result2 = GoldenFlowerResult.getGoldenFlowerResult(cardList2);
        boolean bool = CardRulerUtil.compareMaxCard(result1.getCards(),result2.getCards(),GOLDENFLOWER);
        Assert.assertFalse(bool);
    }

    @Test
    public void compareContinuousCardsTest(){
        Card card1 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
        GoldenFlowerResult result1 = GoldenFlowerResult.getGoldenFlowerResult(cardList1);

        Card card4 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);
        Card card5 = new Card(Constant.CardNumber.Two, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card4, card5, card6));
        GoldenFlowerResult result2 = GoldenFlowerResult.getGoldenFlowerResult(cardList2);
        boolean bool = CardRulerUtil.compareCardsForStraight(result1.getCards(), result2.getCards(),GOLDENFLOWER);
        Assert.assertTrue(bool);
    }

    @Test
    public void compareContinuousCardsTest2(){
        Card card1 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card3 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
        GoldenFlowerResult result1 = GoldenFlowerResult.getGoldenFlowerResult(cardList1);

        Card card4 = new Card(Constant.CardNumber.Ten, Constant.CardType.Heart);
        Card card5 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card4, card5, card6));
        GoldenFlowerResult result2 = GoldenFlowerResult.getGoldenFlowerResult(cardList2);
        boolean bool = CardRulerUtil.compareCardsForStraight(result1.getCards(),result2.getCards(),GOLDENFLOWER);
        Assert.assertTrue(bool);
    }

    @Test
    public void comparePairCardsTest(){
        Card card1 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
        GoldenFlowerResult result1 = GoldenFlowerResult.getGoldenFlowerResult(cardList1);

        Card card4 = new Card(Constant.CardNumber.Two, Constant.CardType.Heart);
        Card card5 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card4, card5, card6));
        GoldenFlowerResult result2 = GoldenFlowerResult.getGoldenFlowerResult(cardList2);
        boolean bool = CardRulerUtil.compareCardsForPair(result1.getCards(),result2.getCards(),GOLDENFLOWER,POKER_PAIR_CARD_SIZE);
        Assert.assertFalse(bool);
    }

    @Test
    public void compareSingleCardsTest(){
        Card card1 = new Card(Constant.CardNumber.Five, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
        GoldenFlowerResult result1 = GoldenFlowerResult.getGoldenFlowerResult(cardList1);

        Card card4 = new Card(Constant.CardNumber.Five, Constant.CardType.Heart);
        Card card5 = new Card(Constant.CardNumber.Two, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card4, card5, card6));
        GoldenFlowerResult result2 = GoldenFlowerResult.getGoldenFlowerResult(cardList2);
        boolean bool = CardRulerUtil.compareCardsForOneToOne(result1.getCards(), result2.getCards(),GOLDENFLOWER);
        Assert.assertTrue(bool);
    }

}
