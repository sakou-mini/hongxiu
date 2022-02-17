package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.BullBullBuilder;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.exception.CardParameterException;
import com.donglaistd.jinli.util.CardRulerUtil;
import com.donglaistd.jinli.util.ComparatorUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BullRulerResultTest extends BaseTest {
    @Autowired
    BullBullBuilder bullBullBuilder;

    @Test

    public void maxCardTest() {
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Constant.CardNumber.King, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Queen, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.King, Constant.CardType.Heart));
        cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.Diamond));
        Card maxCard1 = CardRulerUtil.getMaxCardFromList(cards, ComparatorUtil.getCardComparatorForGameType(Constant.GameType.NIUNIU));
        Assert.assertEquals(maxCard1, new Card(Constant.CardNumber.King, Constant.CardType.Spade));

        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        List<Card> cards2 = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));
        Card maxCard2 = CardRulerUtil.getMaxCardFromList(cards2,ComparatorUtil.getCardComparatorForGameType(Constant.GameType.NIUNIU));
        Assert.assertEquals(maxCard2, card1);
    }

    @Test

    public void isBombOrSmallBullTest() {
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));
        Assert.assertFalse(CardRulerUtil.isSmallBull(cardList));
        Assert.assertTrue(CardRulerUtil.isBomb(cardList));

        Card card6 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        Card card7 = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        Card card8 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);
        Card card9 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card10 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card6, card7, card8, card9, card10));
        Assert.assertTrue(CardRulerUtil.isSmallBull(cardList2));
    }


    @Test

    public void isFigureBullTest() {
        Card card1 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));

        Assert.assertEquals(Constant.BullType.FigureBull_5,CardRulerUtil.getFigureBullType(cardList));
        Card card6 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        Card card7 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card8 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        Card card9 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card10 = new Card(Constant.CardNumber.Ten, Constant.CardType.Spade);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card6, card7, card8, card9, card10));
        Assert.assertEquals(Constant.BullType.FigureBull_4,CardRulerUtil.getFigureBullType(cardList2));
    }

    @Test

    public void bullTypeTest() {
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));

        Constant.BullType bullType = CardRulerUtil.getBullType(cardList);
        Assert.assertEquals(Constant.BullType.BullBull, bullType);

        Card card11 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        Card card12 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card13 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card14 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card15 = new Card(Constant.CardNumber.Ace, Constant.CardType.Spade);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card11, card12, card13, card14, card15));
        Constant.BullType bullType2 = CardRulerUtil.getBullType(cardList2);
        Assert.assertEquals(Constant.BullType.No_Bull, bullType2);
    }

    @Test

    public void getResultTest() throws CardParameterException {
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Ten, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Six, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);

        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));
        BullRulerResult bullResult = BullRulerResult.getBullResult(cardList);
        Assert.assertEquals(Constant.BullType.Bull_9, bullResult.getBullType());
        Assert.assertEquals(card1, bullResult.getMaxCard());

    }

    @Test

    public void compareBullCardTypeTest() {
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        List<Card> cardList1 = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5));
        Assert.assertEquals(CardRulerUtil.getBullType(cardList1), Constant.BullType.Bull_7);

        Card card11 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);
        Card card12 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card13 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card14 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card15 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        List<Card> cardList2 = new ArrayList<>(Arrays.asList(card11, card12, card13, card14, card15));
        Assert.assertEquals(CardRulerUtil.getBullType(cardList2), Constant.BullType.Bull_7);

        Card card21 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card card22 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        Card card23 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        Card card24 = new Card(Constant.CardNumber.Five, Constant.CardType.Spade);
        Card card25 = new Card(Constant.CardNumber.Six, Constant.CardType.Spade);
        List<Card> cardList3 = new ArrayList<>(Arrays.asList(card21, card22, card23, card24, card25));
        Assert.assertEquals(CardRulerUtil.getBullType(cardList3), Constant.BullType.BullBull);
    }

}
