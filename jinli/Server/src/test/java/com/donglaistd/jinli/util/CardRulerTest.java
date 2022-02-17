package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.donglaistd.jinli.Constant.GameType.GOLDENFLOWER;
import static com.donglaistd.jinli.Constant.GameType.TEXAS_GAME;

public class CardRulerTest extends BaseTest {
    @Test
    public void IsSameCardTypeTest(){
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);
        Card card3 = new Card(Constant.CardNumber.Nine, Constant.CardType.Spade);
        Card card4= new Card(Constant.CardNumber.Ten, Constant.CardType.Spade);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3,card4));
        boolean sameCardType = CardRulerUtil.isSameCardType(cardList);
        Assert.assertTrue(sameCardType);
    }

    @Test
    public void IsContinuousCardsTest(){
        Card card1 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Two, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3));
        boolean result1 = CardRulerUtil.isGoldenFlowerStraight(cardList);
        Assert.assertTrue(result1);

        Card card11 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card12 = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        Card card13 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card11, card12, card13));
        boolean result2 = CardRulerUtil.isGoldenFlowerStraight(cardList2);
        Assert.assertTrue(result2);

        Card card14 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);
        Card card15 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card card16 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card17 = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        Card card18 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        List<Card> cardList3 = new ArrayList<>(Arrays.asList(card14, card15, card16,card17,card18));
        boolean result3 = CardRulerUtil.isGoldenFlowerStraight(cardList3);
        Assert.assertFalse(result3);
    }

    @Test
    public void isSameCardNumberTest(){
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        //Card card4= new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3));
        boolean result = CardRulerUtil.isSameCardNumber(cardList);
        Assert.assertTrue(result);
    }

    @Test
    public void isPairCards(){
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card4= new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3,card4));
        boolean result = CardRulerUtil.isPairCards(cardList);
        Assert.assertTrue(result);
    }

    @Test
    public void compareGoldenFlowerCardsForPair(){
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3));

        Card card11 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card12 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        Card card13 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card11, card12, card13));
        boolean b = CardRulerUtil.compareCardsForPair(cardList, cardList2,GOLDENFLOWER,2);
        Assert.assertFalse(b);
    }

    @Test
    public void compareCardsForOneToOneTest(){
        Card card1 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Five, Constant.CardType.Heart);

        Card card11 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        Card card12 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        Card card13 = new Card(Constant.CardNumber.Ace, Constant.CardType.Club);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3));
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card11, card12, card13));
        boolean b = CardRulerUtil.compareCardsForOneToOne(cardList2, cardList1,GOLDENFLOWER);
        Assert.assertTrue(b);
    }

    @Test
    public void compareCardsForTwoSamePairCardTest(){
        Card card1 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Two, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Nine, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Five, Constant.CardType.Heart);
        Card card5 = new Card(Constant.CardNumber.Five, Constant.CardType.Diamond);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3,card4,card5));

        Card card11 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card card12 = new Card(Constant.CardNumber.Two, Constant.CardType.Diamond);
        Card card13 = new Card(Constant.CardNumber.Eight, Constant.CardType.Heart);
        Card card14 = new Card(Constant.CardNumber.Five, Constant.CardType.Spade);
        Card card15 = new Card(Constant.CardNumber.Five, Constant.CardType.Club);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card11, card12, card13,card14, card15));
        boolean result = CardRulerUtil.compareCardsForPair(cardList1, cardList2, TEXAS_GAME, 3);
        Assert.assertTrue(result);
    }

    @Test
    public void test(){
        List<String> strList = new ArrayList<>();
        strList.add("h");
        strList.add("e");
        strList.add("l");
        Collections.swap(strList,0,strList.size()-1);
        System.out.println(strList);
    }
}
