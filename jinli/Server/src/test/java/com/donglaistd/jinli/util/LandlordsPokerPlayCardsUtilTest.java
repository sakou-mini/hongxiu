package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.landlord.Landlords;
import com.donglaistd.jinli.database.entity.game.landlord.PokerRecord;
import com.donglaistd.jinli.util.landlords.LandlordsPokerPlayCardsUtil;
import com.donglaistd.jinli.util.landlords.LandlordsPokerUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LandlordsPokerPlayCardsUtilTest extends BaseTest {

    @Test
    public void singlePairTest(){
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Four, Constant.CardType.Spade);
        Card card3 = new Card(Constant.CardNumber.Ten, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Jack, Constant.CardType.Heart);
        Card card5 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card blackJoker = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card redJoker = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);

        List<Card> prevCards = new ArrayList<>();
        prevCards.add(blackJoker);

        List<Card> currentCards = Lists.newArrayList(card1, card2, card3,card4,card5,redJoker);
        List<Card> cards = LandlordsPokerPlayCardsUtil.playSingleCard(prevCards, currentCards);
        Assert.assertEquals(redJoker,cards.get(0));
    }

    @Test
    public void playPairTest(){
        List<Card> prevCards = new ArrayList<>();
        prevCards.add(new Card(Constant.CardNumber.Ten, Constant.CardType.Spade));
        prevCards.add(new Card(Constant.CardNumber.Ten, Constant.CardType.Spade));
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Jack, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card5 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card6 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card blackJoker = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card redJoker = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        List<Card> currentCards = Lists.newArrayList(card1, card2, card3,card4,card5,card6,blackJoker,redJoker);
        List<Card> resultCards = LandlordsPokerPlayCardsUtil.playPair(prevCards, currentCards);
        Assert.assertEquals(2,resultCards.size());
        Assert.assertTrue(resultCards.containsAll(Lists.newArrayList(card3,card4)));
    }

    @Test
    public void playSerialPairCardTest(){
        List<Card> prevCards = new ArrayList<>();
        for (int i = 0; i <2 ; i++) {
            prevCards.add(new Card(Constant.CardNumber.Nine, Constant.CardType.forNumber( Constant.CardType.Spade_VALUE+i)));
            prevCards.add(new Card(Constant.CardNumber.Ten, Constant.CardType.forNumber( Constant.CardType.Spade_VALUE+i)));
            prevCards.add(new Card(Constant.CardNumber.Jack,Constant.CardType.forNumber( Constant.CardType.Spade_VALUE+i)));
        }
        Card card1 = new Card(Constant.CardNumber.Queen, Constant.CardType.Heart);
        Card card2 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Jack, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Jack, Constant.CardType.Diamond);
        Card card5 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        Card card6 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        Card card7 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        List<Card> currentCards = Lists.newArrayList(card1, card2, card3,card4,card5,card6,card7);
        List<Card> resultCards = LandlordsPokerPlayCardsUtil.playSerialPairCard(prevCards, currentCards);
        Assert.assertEquals(6,resultCards.size());
        Assert.assertTrue(resultCards.containsAll( Lists.newArrayList(card1, card2, card3,card4,card5,card6)));
    }

    @Test
    public void autoPlayCardsTest(){
        List<Card> prevCards = new ArrayList<>();
        //1.straightTest
        for (int i = 0; i <5 ; i++) {
            prevCards.add(new Card(Constant.CardNumber.forNumber(Constant.CardNumber.Three_VALUE+i),Constant.CardType.Spade));
        }
        Constant.LandlordsType landlordsType = LandlordsPokerUtil.checkPokerType(prevCards);
        PokerRecord pokerRecord = PokerRecord.newInstance(landlordsType, prevCards, "");
        Card card1 = new Card(Constant.CardNumber.Nine, Constant.CardType.Heart);
        Card card2 = new Card(Constant.CardNumber.Ten, Constant.CardType.Diamond);
        Card card3 = new Card(Constant.CardNumber.Jack, Constant.CardType.Heart);
        Card card4 = new Card(Constant.CardNumber.Queen, Constant.CardType.Diamond);
        Card card5 = new Card(Constant.CardNumber.King, Constant.CardType.Spade);
        Card card6 = new Card(Constant.CardNumber.King, Constant.CardType.Heart);
        Card card7 = new Card(Constant.CardNumber.King, Constant.CardType.Diamond);
        Card card8 = new Card(Constant.CardNumber.Ace, Constant.CardType.Diamond);
        List<Card> userCards = Lists.newArrayList(card1, card2, card3,card4,card5,card6,card7,card8);
        List<Card> straightCards = LandlordsPokerPlayCardsUtil.autoPlayCards(pokerRecord, userCards);
        Assert.assertTrue(straightCards.containsAll(List.of(card1,card2,card3,card4,card5)));
    }

    @Test
    public void playFourAndTwoTest(){
        List<Card> prevCards = new ArrayList<>();
        for (int i = 0; i <4 ; i++) {
            prevCards.add(new Card(Constant.CardNumber.Four,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE)));
        }
        prevCards.add( new Card(Constant.CardNumber.Three, Constant.CardType.Diamond));
        prevCards.add( new Card(Constant.CardNumber.Three, Constant.CardType.Spade));

        List<Card> userCards = new ArrayList<>();
        for (int i = 0; i <4 ; i++) {
            userCards.add(new Card(Constant.CardNumber.Ace,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE)));
        }
        userCards.add(new Card(Constant.CardNumber.Jack,Constant.CardType.Spade));
        userCards.add(new Card(Constant.CardNumber.Jack,Constant.CardType.Diamond));
        userCards.add(new Card(Constant.CardNumber.Nine,Constant.CardType.Spade));
        userCards.add(new Card(Constant.CardNumber.Nine,Constant.CardType.Diamond));
        List<Card> playCards = LandlordsPokerPlayCardsUtil.playFourAndTwo(prevCards, userCards);
        Assert.assertEquals(6, playCards.size());
    }

    @Test
    public void playMinCardCombinationtest(){
        List<Card> prevCards = new ArrayList<>();
        for (int i = 0; i <4 ; i++) {
            prevCards.add(new Card(Constant.CardNumber.Four,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE)));
        }
        Card card1 = new Card(Constant.CardNumber.Three, Constant.CardType.Diamond);
        Card card2 = new Card(Constant.CardNumber.Three, Constant.CardType.Spade);
        Card card3 = new Card(Constant.CardNumber.Two, Constant.CardType.Spade);
        Card blackJoker = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card redJoker = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        prevCards.addAll(List.of(card1,card2,card3,blackJoker,redJoker));
        List<Card> cards = LandlordsPokerPlayCardsUtil.playMinCardCombination(prevCards);
        prevCards.removeAll(cards);
        Assert.assertEquals(1,cards.size());
        Assert.assertTrue(cards.contains(card3));

        cards = LandlordsPokerPlayCardsUtil.playMinCardCombination(prevCards);
        prevCards.removeAll(cards);
        Assert.assertEquals(2,cards.size());
        Assert.assertTrue(cards.containsAll(List.of(card1,card2)));

        cards = LandlordsPokerPlayCardsUtil.playMinCardCombination(prevCards);
        prevCards.removeAll(cards);
        Assert.assertEquals(4,cards.size());
        Assert.assertEquals(Constant.LandlordsType.Poker_bomb,LandlordsPokerUtil.checkPokerType(cards));

        cards = LandlordsPokerPlayCardsUtil.playMinCardCombination(prevCards);
        prevCards.removeAll(cards);
        Assert.assertEquals(2,cards.size());
        Assert.assertTrue(cards.containsAll(List.of(blackJoker,redJoker)));
    }

    @Test
    public void playPlanCardTest(){
        List<Card> prevCards = new ArrayList<>();
        //1.straightTest
        for (int i = 0; i <3 ; i++) {
            prevCards.add(new Card(Constant.CardNumber.Four,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE+i)));
            prevCards.add(new Card(Constant.CardNumber.Five,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE+i)));
        }
        prevCards.add(new Card(Constant.CardNumber.Three, Constant.CardType.Diamond));
        prevCards.add(new Card(Constant.CardNumber.Three, Constant.CardType.Spade));

        List<Card> userCards = new ArrayList<>();
        for (int i = 0; i <3 ; i++) {
            userCards.add(new Card(Constant.CardNumber.Eight,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE)));
            userCards.add(new Card(Constant.CardNumber.Nine,Constant.CardType.forNumber(Constant.CardType.Spade_VALUE)));
        }
        userCards.add(new Card(Constant.CardNumber.Three,Constant.CardType.Spade));
        userCards.add(new Card(Constant.CardNumber.Two,Constant.CardType.Diamond));
        PokerRecord pokerRecord = PokerRecord.newInstance(LandlordsPokerUtil.checkPokerType(prevCards), prevCards, "123");
        List<Card> cards = LandlordsPokerPlayCardsUtil.autoPlayCards(pokerRecord, userCards);
        Assert.assertEquals(8,cards.size());
    }
}
