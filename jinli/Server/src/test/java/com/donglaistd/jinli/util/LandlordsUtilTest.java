package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.DeckBuilder;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.database.entity.game.landlord.PokerRecord;
import com.donglaistd.jinli.util.landlords.LandlordsPokerUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static com.donglaistd.jinli.Constant.GameType.LANDLORD_GAME;
import static com.donglaistd.jinli.Constant.LandlordsType.*;

public class LandlordsUtilTest extends BaseTest {

    @Test
    public void chinesePokerSortTest() {
        Card redJokerRed = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        Card blackJokerRed = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card diamondAce = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        Card spadeTwo = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card clubTwo = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card clubTen = new Card(Constant.CardNumber.Ten, Constant.CardType.Club);
        List<Card> cards = new ArrayList<>(Lists.newArrayList(diamondAce, redJokerRed, spadeTwo, blackJokerRed, clubTen, clubTwo));
        cards.sort(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME));
        Assert.assertEquals(cards.get(0), redJokerRed);
        Assert.assertEquals(cards.get(1), blackJokerRed);
        Assert.assertEquals(cards.get(cards.size() - 1), clubTen);

        Deck deck = DeckBuilder.getJokerDeck();
        List<Card> cards2 = deck.dealMultipleCards(17);
        cards2.sort(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME));
        System.out.println(cards2);
    }


    @Test
    public void isSameCardTest() {
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        Card card4 = new Card(Constant.CardNumber.Seven, Constant.CardType.Club);
        List<Card> cards = new ArrayList<>(Lists.newArrayList(card1, card2, card3, card4));
        cards.sort(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME));
        boolean sameCard = LandlordsPokerUtil.hasSameCountCard(cards, 3);
        Assert.assertTrue(sameCard);
    }

    @Test
    public void isPokerThreeAndTwoTest() {
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card5 = new Card(Constant.CardNumber.Seven, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Seven, Constant.CardType.Spade);
        Card card4 = new Card(Constant.CardNumber.Seven, Constant.CardType.Club);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        List<Card> cards = new ArrayList<>(Lists.newArrayList(card1, card2, card3, card4, card5));
        boolean sameCard = LandlordsPokerUtil.isPokerThreeAttachTwo(cards);
        Assert.assertTrue(sameCard);
    }

    @Test
    public void isPokerStraightTest() {
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        Card card3 = new Card(Constant.CardNumber.Five, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Six, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Seven, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Eight, Constant.CardType.Heart);
        Card card7 = new Card(Constant.CardNumber.Nine, Constant.CardType.Heart);
        Card card8 = new Card(Constant.CardNumber.Ten, Constant.CardType.Heart);
        Card card9 = new Card(Constant.CardNumber.Jack, Constant.CardType.Heart);
        Card card10 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card11 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        Card card12 = new Card(Constant.CardNumber.Ace, Constant.CardType.Heart);
        List<Card> cards = new ArrayList<>(Lists.newArrayList(card1, card2, card3, card4, card5, card6, card7, card8, card9, card10, card11, card12));
        Collections.shuffle(cards);
        boolean isStraight = LandlordsPokerUtil.isPokerStraight(cards);
        Assert.assertTrue(isStraight);
        boolean isStraight2 = LandlordsPokerUtil.isPokerStraight(Lists.newArrayList(card1, card2, card3, card4));
        Assert.assertFalse(isStraight2);
    }

    @Test
    public void isSerialPairTest() {
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        Card card3 = new Card(Constant.CardNumber.Two, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Four, Constant.CardType.Diamond);
        Card card6 = new Card(Constant.CardNumber.Four, Constant.CardType.Heart);
        Card card7 = new Card(Constant.CardNumber.Five, Constant.CardType.Heart);
        Card card8 = new Card(Constant.CardNumber.Five, Constant.CardType.Diamond);
        boolean result = LandlordsPokerUtil.isPokerSerialPair(Lists.newArrayList(card1, card2, card3, card4, card5, card6, card7));
        Assert.assertFalse(result);
        boolean result2 = LandlordsPokerUtil.isPokerSerialPair(Lists.newArrayList(card1, card2, card3, card4, card5, card6));
        Assert.assertFalse(result2);
        boolean result3 = LandlordsPokerUtil.isPokerSerialPair(Lists.newArrayList(card1, card2, card5, card6, card7, card8));
        Assert.assertTrue(result3);
    }

    @Test
    public void pokerThreeTest() {
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        Card card3 = new Card(Constant.CardNumber.Three, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Two, Constant.CardType.Club);
        boolean threeAndOne = LandlordsPokerUtil.isPokerThreeAttachOne(Lists.newArrayList(card1, card2, card4, card3));
        Assert.assertTrue(threeAndOne);
        Card card5 = new Card(Constant.CardNumber.Four, Constant.CardType.Diamond);
        boolean threeAndTwo = LandlordsPokerUtil.isPokerThreeAttachTwo(Lists.newArrayList(card1, card2, card4, card3, card5));
        Assert.assertFalse(threeAndTwo);
    }

    @Test
    public void planeTest() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        boolean plane = LandlordsPokerUtil.isPlane(cards);
        Assert.assertTrue(plane);

        //planeAndSingle
        cards.add(new Card(Constant.CardNumber.Six, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        boolean planeAndSingle = LandlordsPokerUtil.isPlaneAttachSingle(cards);
        Assert.assertTrue(planeAndSingle);
        cards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Spade));
        boolean planeAndSingle2 = LandlordsPokerUtil.isPlaneAttachSingle(cards);
        Assert.assertFalse(planeAndSingle2);

        //planeAndPair
        cards.clear();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        cards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Queen, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Queen, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.King, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.King, Constant.CardType.Spade));
        boolean planeAndPair = LandlordsPokerUtil.isPlaneAttachPair(cards);
        Assert.assertTrue(planeAndPair);
        cards.add(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        boolean planeAndPair2 = LandlordsPokerUtil.isPlaneAttachPair(cards);
        Assert.assertFalse(planeAndPair2);

        cards.clear();
        //We don't allow two consecutive bombs to be planes
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        boolean result = LandlordsPokerUtil.isPlaneAttachSingle(cards);
        Assert.assertFalse(result);
    }

    @Test
    public void fourAttachTwoTest() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.Ace, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        boolean bomb = CardRulerUtil.isBomb(cards);
        Assert.assertTrue(bomb);
        boolean fourAttachTwo = LandlordsPokerUtil.isFourAttachTwo(cards);
        Assert.assertFalse(fourAttachTwo);

        cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        boolean fourAttachTwo2 = LandlordsPokerUtil.isFourAttachTwo(cards);
        Assert.assertFalse(fourAttachTwo2);

        cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        boolean fourAttachTwo3 = LandlordsPokerUtil.isFourAttachTwo(cards);
        Assert.assertTrue(fourAttachTwo3);

        cards.clear();
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        } boolean fourAttachTwo4 = LandlordsPokerUtil.isFourAttachTwo(cards);
        Assert.assertTrue(fourAttachTwo4);

        cards.clear();
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        cards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Queen, Constant.CardType.Spade));
        boolean fourAttachTwo5 = LandlordsPokerUtil.isFourAttachTwo(cards);
        Assert.assertTrue(fourAttachTwo5);

        cards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Eight, Constant.CardType.Spade));
        boolean fourAttachTwo6 = LandlordsPokerUtil.isFourAttachTwo(cards);
        Assert.assertFalse(fourAttachTwo6);
    }

    @Test
    public void chekCardTypeTest() {
        List<Card> cards = new ArrayList<>();
        //1.straight
        for (int i = 0; i < 6; i++) {
            cards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Three_VALUE + i), Constant.CardType.Spade));
        }
        Constant.LandlordsType type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(type, Poker_straight);
        //2.jokerBomb
        cards.clear();
        Card redJokerRed = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        Card blackJokerRed = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        cards = Lists.newArrayList(redJokerRed, blackJokerRed);
        type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(type, Poker_jokerBomb);

        //3.plane
        cards.clear();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Six, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(type, Poker_plane);
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Ten_VALUE + i), Constant.CardType.Spade));
        }
        type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(type, Poker_planeAndSingle);

        //4.illegalCard
        cards.clear();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Three_VALUE + i), Constant.CardType.Spade));
        }
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.Seven, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(type, Poker_null);

        //fourAndTwo
        cards.clear();
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(type, Poker_fourAndTwo);
    }

    @Test
    public void compareCardsTest() {
        List<Card> prevCards = new ArrayList<>();
        List<Card> currentCards = new ArrayList<>();
        //1.straight compare bomb
        for (int i = 0; i < 6; i++) {
            prevCards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Three_VALUE + i), Constant.CardType.Spade));
        }
        for (int i = 0; i < 5; i++) {
            currentCards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Eight_VALUE + i), Constant.CardType.Spade));
        }
        boolean result = LandlordsPokerUtil.comparePoker(PokerRecord.newInstance(prevCards,""), PokerRecord.newInstance(currentCards,""));
        Assert.assertFalse(result);

        //pair compare pair
        prevCards.clear();
        currentCards.clear();
        for (int i = 0; i < 2; i++) {
            prevCards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        for (int i = 0; i < 2; i++) {
            currentCards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Club_VALUE + i)));
        }

        result = LandlordsPokerUtil.comparePoker(PokerRecord.newInstance(prevCards,""), PokerRecord.newInstance(currentCards,""));
        Assert.assertFalse(result);

        //single compare single
        prevCards.clear();
        currentCards.clear();
        prevCards.add(new Card(Constant.CardNumber.Ace, Constant.CardType.Spade));
        currentCards.add(new Card(Constant.CardNumber.Two, Constant.CardType.Spade));
        result = LandlordsPokerUtil.comparePoker(PokerRecord.newInstance(prevCards,""), PokerRecord.newInstance(currentCards,""));
        Assert.assertTrue(result);

        prevCards.clear();
        currentCards.clear();
        for (int i = 0; i < 4; i++) {
            prevCards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        for (int i = 0; i < 4; i++) {
            currentCards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        result = LandlordsPokerUtil.comparePoker(PokerRecord.newInstance(prevCards,""), PokerRecord.newInstance(currentCards,""));
        Assert.assertFalse(result);

        //plane compare
        prevCards.clear();
        currentCards.clear();
        for (int i = 0; i < 3; i++) {
            prevCards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            prevCards.add(new Card(Constant.CardNumber.Six, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            prevCards.add(new Card(Constant.CardNumber.Seven, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            prevCards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Jack_VALUE + i), Constant.CardType.Spade));
        }
        for (int i = 0; i < 3; i++) {
            currentCards.add(new Card(Constant.CardNumber.Eight, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            currentCards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        currentCards.add(new Card(Constant.CardNumber.Jack, Constant.CardType.Club));
        currentCards.add(new Card(Constant.CardNumber.Queen, Constant.CardType.Spade));
        result = LandlordsPokerUtil.comparePoker(PokerRecord.newInstance(prevCards,""), PokerRecord.newInstance(currentCards,""));
        Assert.assertFalse(result);
    }

    @Test
    public void testSingleCardCompare() {
        Card redJokerRed = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);

        Card card1 = new Card(Constant.CardNumber.Ten, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Ten, Constant.CardType.Joker);
        Card card3 = new Card(Constant.CardNumber.Ten, Constant.CardType.Spade);
        Card card4 = new Card(Constant.CardNumber.Ten, Constant.CardType.Heart);
        boolean result = LandlordsPokerUtil.comparePoker(PokerRecord.newInstance(Lists.newArrayList(redJokerRed),""),
                PokerRecord.newInstance(Lists.newArrayList(card1, card2, card3, card4),""));
        Assert.assertTrue(result);
    }

    @Test
    public void PlaneAttachSingleTest() {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        boolean plane = LandlordsPokerUtil.isPlane(cards);
        Assert.assertTrue(plane);

        //planeAndSingle
        cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        boolean planeAndSingle = LandlordsPokerUtil.isPlaneAttachSingle(cards);
        Assert.assertTrue(planeAndSingle);

        cards.clear();
        for (int i = 0; i < 3; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));

        }
        for (int i = 0; i < 4; i++) {
            cards.add(new Card(Constant.CardNumber.Eight, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.Spade));
        boolean pair = LandlordsPokerUtil.isPlaneAttachPair(cards);
        Assert.assertTrue(pair);
        Constant.LandlordsType type = LandlordsPokerUtil.checkPokerType(cards);
        Assert.assertEquals(Poker_planeAndPair, type);
    }


    @Test
    public void getGroupPairNumTest() {
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card2 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Club);
        Card card5 = new Card(Constant.CardNumber.Queen, Constant.CardType.Spade);
        Card card6 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        Card card7 = new Card(Constant.CardNumber.King, Constant.CardType.Club);
        List<Card> cardList = new ArrayList<>(Arrays.asList(card1, card2, card3, card4, card5, card6, card7));
        int pairNum = LandlordsPokerUtil.getGroupPairNum(cardList);
        Assert.assertEquals(3, pairNum);
    }

    @Test
    public void getAttachCardsTest(){
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            cards.add(new Card(Constant.CardNumber.Three, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Four, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Five, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Six, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        Card attCard1 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card attCard2 = new Card(Constant.CardNumber.Ten, Constant.CardType.Spade);
        Card attCard3 = new Card(Constant.CardNumber.Nine, Constant.CardType.Spade);
        Card attCard4 = new Card(Constant.CardNumber.Jack, Constant.CardType.Spade);
        cards.addAll(Set.of(attCard1,attCard2,attCard3,attCard4));
        List<Card> attachCards = LandlordsPokerUtil.splitAttachCards(cards);
        Assert.assertEquals(4, attachCards.size());
        Assert.assertTrue(Set.of(attCard1,attCard2,attCard3,attCard4).containsAll(attachCards));

        cards.clear();
        cards.addAll(List.of(new Card(Constant.CardNumber.Two, Constant.CardType.Spade), new Card(Constant.CardNumber.Two, Constant.CardType.Diamond)));
        attachCards = LandlordsPokerUtil.splitAttachCards(cards);
        Assert.assertEquals(0, attachCards.size());

        cards.clear();
        for (int i = 0; i <4 ; i++) {
            cards.add(new Card(Constant.CardNumber.Two, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
            cards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.forNumber(Constant.CardType.Spade_VALUE + i)));
        }
        attachCards = LandlordsPokerUtil.splitAttachCards(cards);
        Assert.assertEquals(4, attachCards.size());
    }

}
