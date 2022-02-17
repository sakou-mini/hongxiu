package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.texas.CardsGroup;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.CardNumber.*;
import static com.donglaistd.jinli.Constant.CardType.*;
import static com.donglaistd.jinli.Constant.TexasType.*;

public class TexasCardUtilTest {
    @Test
    public void isTongHuaShun() {
        //同花顺牌
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Four, Spade));
        cards.add(new Card(Three, Spade));
        cards.add(new Card(Six, Spade));
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Ten, Diamond));
        cards.add(new Card(Jack, Club));
        Assert.assertTrue(TexasCardUtil.isFlush(cards));
        List<Card> list = TexasCardUtil.getMaxStraightFlush(cards);
        Assert.assertFalse(list.isEmpty());
        list.stream().findFirst().ifPresent(c -> Assert.assertEquals(c.getCardNumber(), Six_VALUE));

    }

    @Test
    public void isSiTiao() {
        //四条
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Two, Club));
        cards.add(new Card(Two, Diamond));
        cards.add(new Card(Six, Spade));
        cards.add(new Card(Ten, Diamond));
        cards.add(new Card(Two, Heart));
        cards.add(new Card(Six, Spade));
        Assert.assertTrue(TexasCardUtil.isFourOfAKind(cards));
        List<Card> maxFourOfAKind = TexasCardUtil.getMaxFourOfAKind(cards);
        Assert.assertFalse(maxFourOfAKind.isEmpty());
        long count = maxFourOfAKind.stream().map(Card::getCardNumber).filter(n -> n == Two_VALUE).count();
        Assert.assertEquals(4, count);
    }

    @Test
    public void isHuLu() {
        //葫芦
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Two, Club));
        cards.add(new Card(Two, Diamond));
        cards.add(new Card(Two, Heart));
        cards.add(new Card(Six, Diamond));
        cards.add(new Card(Six, Spade));
        cards.add(new Card(Six, Club));
        Assert.assertTrue(TexasCardUtil.isFullHouse(cards));
        List<Card> list = TexasCardUtil.getMaxFullHouse(cards);
        Assert.assertFalse(list.isEmpty());
        Map<Integer, List<Card>> collect = list.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        Assert.assertEquals(2, collect.size());
    }

    @Test
    public void isTongHua() {
        //同花
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Eight, Spade));
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Queen, Club));
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Two, Heart));
        cards.add(new Card(Six, Spade));
        cards.add(new Card(Ten, Spade));
        Assert.assertTrue(TexasCardUtil.isFlush(cards));
        List<Card> flush = TexasCardUtil.getMaxFlush(cards);
        Assert.assertFalse(flush.isEmpty());
        long count = flush.stream().map(Card::getCardType).count();
        Assert.assertEquals(5, count);
    }

    @Test
    public void isShunZi() {
        //顺子
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Ace, Spade));
        cards.add(new Card(Seven, Club));
        cards.add(new Card(Four, Diamond));
        cards.add(new Card(Six, Spade));
        cards.add(new Card(Jack, Spade));
        cards.add(new Card(Eight, Diamond));
        Assert.assertTrue(TexasCardUtil.isIncludeStraight(cards));
        List<Card> list = TexasCardUtil.getStraight_Card(cards);
        Assert.assertFalse(list.isEmpty());
        list.stream().findFirst().ifPresent(card -> Assert.assertEquals(Eight_VALUE, card.getCardNumber()));

        cards = new ArrayList<>();
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Ace, Spade));
        cards.add(new Card(Three, Club));
        cards.add(new Card(Queen, Club));
        cards.add(new Card(Four, Diamond));
        cards.add(new Card(Nine, Diamond));
        cards.add(new Card(Five, Spade));
        Assert.assertTrue(TexasCardUtil.isIncludeStraight(cards));
        List<Card> straight_card = TexasCardUtil.getStraight_Card(cards);

        cards = new ArrayList<>();
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Three, Club));
        cards.add(new Card(Four, Diamond));
        cards.add(new Card(Five, Spade));
        Assert.assertFalse(TexasCardUtil.isIncludeStraight(cards));

        cards = new ArrayList<>();
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Three, Club));
        cards.add(new Card(Four, Diamond));
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Seven, Spade));
        cards.add(new Card(Eight, Spade));
        Assert.assertFalse(TexasCardUtil.isIncludeStraight(cards));
    }

    @Test
    public void isSanTiao() {
        //三条
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Five, Diamond));
        cards.add(new Card(Five, Club));
        cards.add(new Card(Six, Club));
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Eight, Diamond));
        cards.add(new Card(Jack, Diamond));
        Collections.sort(cards);
        Assert.assertTrue(TexasCardUtil.isThreeOfAKind(cards));
        List<Card> list = TexasCardUtil.getMaxThreeOfAKind(cards);
        Assert.assertFalse(list.isEmpty());
        long count = list.stream().map(Card::getCardNumber).filter(n -> n == Five_VALUE).count();
        Assert.assertEquals(3, count);
    }

    @Test
    public void isLiangDui() {
        //两对
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Ten, Spade));
        cards.add(new Card(Two, Diamond));
        cards.add(new Card(Five, Club));
        cards.add(new Card(Eight, Spade));
        cards.add(new Card(Ace, Spade));
        cards.add(new Card(Eight, Diamond));
        Collections.sort(cards);
        Assert.assertTrue(TexasCardUtil.isTwoPair(cards));
        List<Card> list = TexasCardUtil.getMaxTwoPair(cards);
        Assert.assertFalse(list.isEmpty());
        Map<Integer, List<Card>> map = list.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        Assert.assertEquals(3, map.size());
    }

    @Test
    public void isYiDui() {
        //一对
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Five, Spade));
        cards.add(new Card(Five, Diamond));
        cards.add(new Card(Ten, Club));
        cards.add(new Card(Six, Spade));
        cards.add(new Card(Two, Spade));
        cards.add(new Card(Eight, Diamond));
        cards.add(new Card(Ace, Diamond));
        Collections.sort(cards);
        Assert.assertTrue(TexasCardUtil.isOnePair(cards));
        List<Card> list = TexasCardUtil.getMaxOnePair(cards);
        Assert.assertFalse(list.isEmpty());
        Map<Integer, List<Card>> collect = list.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        Assert.assertEquals(2, collect.get(Five_VALUE).size());
    }

    @Test
    public void testCompare() {
        // 同花顺
        // ♠ 2.3.4.5.6
        List<Card> handCards1 = new ArrayList<>();
        handCards1.add(new Card(Two, Spade));
        handCards1.add(new Card(Three, Spade));
        handCards1.add(new Card(Four, Spade));
        handCards1.add(new Card(Five, Spade));
        handCards1.add(new Card(Six, Spade));
        RacePokerPlayer racePokerPlayerA = new RacePokerPlayer();
        racePokerPlayerA.setHandPokers(handCards1);

        //♣ 10.J.Q.K.A
        List<Card> handCards2 = new ArrayList<>();
        handCards2.add(new Card(Ace, Club));
        handCards2.add(new Card(Ten, Club));
        handCards2.add(new Card(Jack, Club));
        handCards2.add(new Card(Queen, Club));
        handCards2.add(new Card(King, Club));
        RacePokerPlayer racePokerPlayerB = new RacePokerPlayer();
        racePokerPlayerB.setHandPokers(handCards2);
        int compare = TexasCardUtil.compareValue(new CardsGroup<>(StraightFlush, handCards1), new CardsGroup<>(RoyalStraightFlush, handCards2));
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void testCompareSameCardType() {
        List<Card> handCards1 = new ArrayList<>();
        handCards1.add(new Card(Two, Spade));
        handCards1.add(new Card(Four, Spade));
        handCards1.add(new Card(Three, Spade));
        handCards1.add(new Card(Six, Spade));
        handCards1.add(new Card(Five, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(handCards1);

        List<Card> handCards2 = new ArrayList<>();
        handCards2.add(new Card(Four, Club));
        handCards2.add(new Card(Two, Club));
        handCards2.add(new Card(Three, Club));
        handCards2.add(new Card(Five, Club));
        handCards2.add(new Card(Six, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(handCards2);
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(0, compare);
    }

    @Test
    public void testCompareStraightFlush() {
        List<Card> straightFlush1 = new ArrayList<>();
        straightFlush1.add(new Card(Two, Spade));
        straightFlush1.add(new Card(Four, Spade));
        straightFlush1.add(new Card(Three, Spade));
        straightFlush1.add(new Card(Nine, Spade));
        straightFlush1.add(new Card(Six, Spade));
        straightFlush1.add(new Card(Jack, Spade));
        straightFlush1.add(new Card(Five, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(straightFlush1);

        List<Card> straightFlush2 = new ArrayList<>();
        straightFlush2.add(new Card(Jack, Club));
        straightFlush2.add(new Card(Ace, Club));
        straightFlush2.add(new Card(Three, Club));
        straightFlush2.add(new Card(Five, Club));
        straightFlush2.add(new Card(Queen, Club));
        straightFlush2.add(new Card(King, Club));
        straightFlush2.add(new Card(Ten, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(straightFlush2);
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> straightFlush3 = new ArrayList<>();
        straightFlush3.add(new Card(Two, Club));
        straightFlush3.add(new Card(Ace, Club));
        straightFlush3.add(new Card(Three, Club));
        straightFlush3.add(new Card(Five, Club));
        straightFlush3.add(new Card(Three, Club));
        straightFlush3.add(new Card(King, Club));
        straightFlush3.add(new Card(Four, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(straightFlush3);
         compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup3);
        Assert.assertEquals(1, compare);
    }

    @Test
    public void testCompareFourOfAKind() {
        List<Card> straightFlush1 = new ArrayList<>();
        straightFlush1.add(new Card(Two, Spade));
        straightFlush1.add(new Card(Two, Club));
        straightFlush1.add(new Card(Two, Diamond));
        straightFlush1.add(new Card(Two, Heart));
        straightFlush1.add(new Card(Six, Spade));
        straightFlush1.add(new Card(Jack, Spade));
        straightFlush1.add(new Card(Five, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(straightFlush1);

        List<Card> straightFlush2 = new ArrayList<>();
        straightFlush2.add(new Card(Jack, Club));
        straightFlush2.add(new Card(Ace, Heart));
        straightFlush2.add(new Card(Four, Club));
        straightFlush2.add(new Card(Ace, Diamond));
        straightFlush2.add(new Card(Seven, Club));
        straightFlush2.add(new Card(Ace, Spade));
        straightFlush2.add(new Card(Ace, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(straightFlush2);
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> straightFlush3 = new ArrayList<>();
        straightFlush3.add(new Card(Seven, Club));
        straightFlush3.add(new Card(Ace, Club));
        straightFlush3.add(new Card(Three,Club));
        straightFlush3.add(new Card(Jack, Spade));
        straightFlush3.add(new Card(Jack, Heart));
        straightFlush3.add(new Card(Jack, Diamond));
        straightFlush3.add(new Card(Jack, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(straightFlush3);
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(1, compare);
    }

    @Test
    public void testCompareFullHose() {
        List<Card> fullHose1 = new ArrayList<>();
        fullHose1.add(new Card(Two, Spade));
        fullHose1.add(new Card(Two, Club));
        fullHose1.add(new Card(Two, Diamond));
        fullHose1.add(new Card(Six, Heart));
        fullHose1.add(new Card(Five, Diamond));
        fullHose1.add(new Card(Jack, Spade));
        fullHose1.add(new Card(Five, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(fullHose1);

        List<Card> fullHose2 = new ArrayList<>();
        fullHose2.add(new Card(Jack, Club));
        fullHose2.add(new Card(Ace, Heart));
        fullHose2.add(new Card(Four, Club));
        fullHose2.add(new Card(Ace, Diamond));
        fullHose2.add(new Card(Seven, Club));
        fullHose2.add(new Card(Seven, Spade));
        fullHose2.add(new Card(Ace, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(fullHose2);
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> fullHose3 = new ArrayList<>();
        fullHose3.add(new Card(Seven, Club));
        fullHose3.add(new Card(Ace, Club));
        fullHose3.add(new Card(Three,Club));
        fullHose3.add(new Card(Three, Spade));
        fullHose3.add(new Card(Jack, Heart));
        fullHose3.add(new Card(Jack, Diamond));
        fullHose3.add(new Card(Jack, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(fullHose3);
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(1, compare);
    }
    @Test
    public void testCompareFlush() {
        List<Card> fullHose1 = new ArrayList<>();
        fullHose1.add(new Card(Ten, Spade));
        fullHose1.add(new Card(Two, Spade));
        fullHose1.add(new Card(Nine, Spade));
        fullHose1.add(new Card(Six, Club));
        fullHose1.add(new Card(Five, Spade));
        fullHose1.add(new Card(Jack, Spade));
        fullHose1.add(new Card(Five, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(fullHose1);

        List<Card> fullHose2 = new ArrayList<>();
        fullHose2.add(new Card(Ten, Club));
        fullHose2.add(new Card(Two, Club));
        fullHose2.add(new Card(Nine, Club));
        fullHose2.add(new Card(Jack, Club));
        fullHose2.add(new Card(Five, Club));
        fullHose2.add(new Card(Seven, Spade));
        fullHose2.add(new Card(Ace, Diamond));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(fullHose2);
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(0, compare);
        List<Card> fullHose3 = new ArrayList<>();
        fullHose3.add(new Card(Ace, Diamond));
        fullHose3.add(new Card(Three, Diamond));
        fullHose3.add(new Card(Three,Club));
        fullHose3.add(new Card(Six, Diamond));
        fullHose3.add(new Card(Jack, Heart));
        fullHose3.add(new Card(Seven, Diamond));
        fullHose3.add(new Card(Jack, Diamond));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(fullHose3);
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void testCompareHighCard() {
        List<Card> highCard1 = new ArrayList<>();
        highCard1.add(new Card(Ten, Spade));
        highCard1.add(new Card(Nine, Club));
        highCard1.add(new Card(Six, Spade));
        highCard1.add(new Card(Four, Club));
        highCard1.add(new Card(Five, Diamond));
        highCard1.add(new Card(Jack, Spade));
        highCard1.add(new Card(Three, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(highCard1);
        Assert.assertEquals(HighCard, maxCardsGroup1.getTexasType());
        List<Card> highCard2 = new ArrayList<>();
        highCard2.add(new Card(Nine, Spade));
        highCard2.add(new Card(Ten, Club));
        highCard2.add(new Card(Queen, Spade));
        highCard2.add(new Card(King, Club));
        highCard2.add(new Card(Five, Club));
        highCard2.add(new Card(Seven, Spade));
        highCard2.add(new Card(Ace, Diamond));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(highCard2);
        Assert.assertEquals(HighCard, maxCardsGroup1.getTexasType());
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> highCard3 = new ArrayList<>();
        highCard3.add(new Card(Five, Heart));
        highCard3.add(new Card(Four, Diamond));
        highCard3.add(new Card(Three,Club));
        highCard3.add(new Card(Eight, Club));
        highCard3.add(new Card(Jack, Heart));
        highCard3.add(new Card(Seven, Diamond));
        highCard3.add(new Card(Two, Heart));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(highCard3);
        Assert.assertEquals(HighCard, maxCardsGroup1.getTexasType());
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(1, compare);
    }

    @Test
    public void testCompareThreeOfAKind() {
        List<Card> highCard1 = new ArrayList<>();
        highCard1.add(new Card(Ten, Spade));
        highCard1.add(new Card(Ten, Club));
        highCard1.add(new Card(Ten, Diamond));
        highCard1.add(new Card(Six, Club));
        highCard1.add(new Card(Two, Diamond));
        highCard1.add(new Card(Seven, Spade));
        highCard1.add(new Card(Three, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(highCard1);
        Assert.assertEquals(ThreeOfAKind, maxCardsGroup1.getTexasType());
        List<Card> highCard2 = new ArrayList<>();
        highCard2.add(new Card(Nine, Spade));
        highCard2.add(new Card(Ten, Club));
        highCard2.add(new Card(King, Spade));
        highCard2.add(new Card(King, Club));
        highCard2.add(new Card(Five, Club));
        highCard2.add(new Card(Seven, Spade));
        highCard2.add(new Card(King, Diamond));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(highCard2);
        Assert.assertEquals(ThreeOfAKind, maxCardsGroup1.getTexasType());
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> highCard3 = new ArrayList<>();
        highCard3.add(new Card(Three, Heart));
        highCard3.add(new Card(Three, Diamond));
        highCard3.add(new Card(Three,Club));
        highCard3.add(new Card(Eight, Club));
        highCard3.add(new Card(Jack, Heart));
        highCard3.add(new Card(Seven, Diamond));
        highCard3.add(new Card(Two, Heart));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(highCard3);
        Assert.assertEquals(ThreeOfAKind, maxCardsGroup1.getTexasType());
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(1, compare);
    }

    @Test
    public void testCompareTwoPair() {
        List<Card> highCard1 = new ArrayList<>();
        highCard1.add(new Card(Ten, Spade));
        highCard1.add(new Card(Ten, Club));
        highCard1.add(new Card(Six, Diamond));
        highCard1.add(new Card(Six, Club));
        highCard1.add(new Card(Two, Diamond));
        highCard1.add(new Card(Eight, Spade));
        highCard1.add(new Card(Three, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(highCard1);
        Assert.assertEquals(TwoPair, maxCardsGroup1.getTexasType());
        List<Card> highCard2 = new ArrayList<>();
        highCard2.add(new Card(Nine, Spade));
        highCard2.add(new Card(Ten, Club));
        highCard2.add(new Card(King, Spade));
        highCard2.add(new Card(King, Club));
        highCard2.add(new Card(Five, Club));
        highCard2.add(new Card(Seven, Spade));
        highCard2.add(new Card(Seven, Diamond));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(highCard2);
        Assert.assertEquals(TwoPair, maxCardsGroup1.getTexasType());
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> highCard3 = new ArrayList<>();
        highCard3.add(new Card(King, Heart));
        highCard3.add(new Card(King, Diamond));
        highCard3.add(new Card(Three,Club));
        highCard3.add(new Card(Seven, Club));
        highCard3.add(new Card(Jack, Heart));
        highCard3.add(new Card(Seven, Heart));
        highCard3.add(new Card(Two, Heart));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(highCard3);
        Assert.assertEquals(TwoPair, maxCardsGroup1.getTexasType());
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void testCompareOnePair() {
        List<Card> highCard1 = new ArrayList<>();
        highCard1.add(new Card(Ten, Spade));
        highCard1.add(new Card(Ten, Club));
        highCard1.add(new Card(Seven, Diamond));
        highCard1.add(new Card(Six, Club));
        highCard1.add(new Card(Two, Diamond));
        highCard1.add(new Card(Eight, Spade));
        highCard1.add(new Card(Three, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(highCard1);
        Assert.assertEquals(OnePair, maxCardsGroup1.getTexasType());
        List<Card> highCard2 = new ArrayList<>();
        highCard2.add(new Card(Nine, Spade));
        highCard2.add(new Card(Ten, Diamond));
        highCard2.add(new Card(King, Spade));
        highCard2.add(new Card(King, Club));
        highCard2.add(new Card(Five, Club));
        highCard2.add(new Card(Two, Spade));
        highCard2.add(new Card(Seven, Diamond));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(highCard2);
        Assert.assertEquals(OnePair, maxCardsGroup1.getTexasType());
        int compare = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1, compare);
        List<Card> highCard3 = new ArrayList<>();
        highCard3.add(new Card(King, Heart));
        highCard3.add(new Card(King, Diamond));
        highCard3.add(new Card(Three,Club));
        highCard3.add(new Card(Seven, Club));
        highCard3.add(new Card(Jack, Heart));
        highCard3.add(new Card(Four, Heart));
        highCard3.add(new Card(Ten, Heart));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(highCard3);
        Assert.assertEquals(OnePair, maxCardsGroup1.getTexasType());
        compare = TexasCardUtil.compareValue(maxCardsGroup2, maxCardsGroup3);
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void testCompareValue() {
        List<Card> communityCards = new ArrayList<>();
        communityCards.add(new Card(Five, Heart));
        communityCards.add(new Card(Four, Heart));
        communityCards.add(new Card(Seven, Club));
        communityCards.add(new Card(Three, Diamond));
        communityCards.add(new Card(Three, Heart));

        // win
        List<Card> handCards1 = new ArrayList<>();
        handCards1.add(new Card(King,Diamond));
        handCards1.add(new Card(Ten,Diamond));
        handCards1.addAll(communityCards);

        List<Card> handCards2 = new ArrayList<>();
        handCards2.add(new Card(Ten,Spade));
        handCards2.add(new Card(Five,Spade));
        handCards2.addAll(communityCards);

        List<Card> handCards3 = new ArrayList<>();
        handCards3.add(new Card(Nine,Club));
        handCards3.add(new Card(Queen,Heart));
        handCards3.addAll(communityCards);

        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(handCards1);
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(handCards2);
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup3 = TexasCardUtil.getMaxCardsGroup(handCards3);

        Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> finalCardsMap = new HashMap<>();
        finalCardsMap.put(0, maxCardsGroup1);
        finalCardsMap.put(1, maxCardsGroup2);
        finalCardsMap.put(2, maxCardsGroup3);
        List<Integer> winPlayerList = new ArrayList<>();
        CardsGroup<Constant.TexasType, List<Card>> listOld = null;
        for (Map.Entry<Integer, CardsGroup<Constant.TexasType, List<Card>>> entry : finalCardsMap.entrySet()) {
            if (listOld == null) {
                listOld = entry.getValue();
                winPlayerList.add(entry.getKey());
            } else {
                CardsGroup<Constant.TexasType, List<Card>> listNew = entry.getValue();
                int result = TexasCardUtil.compareValue(entry.getValue(), listOld);
                if (result == 1) {
                    winPlayerList.clear();
                    winPlayerList.add(entry.getKey());
                    listOld = listNew;
                } else if (result == 0) {
                    winPlayerList.add(entry.getKey());
                }
            }
        }
        Assert.assertFalse(winPlayerList.isEmpty());
    }

    @Test
    public void testCompareOnePairAndAce() {
        List<Card> onePair1 = new ArrayList<>();
        onePair1.add(new Card(King, Spade));
        onePair1.add(new Card(King, Club));
        onePair1.add(new Card(Seven, Diamond));
        onePair1.add(new Card(Six, Club));
        onePair1.add(new Card(Two, Diamond));
        onePair1.add(new Card(Eight, Spade));
        onePair1.add(new Card(Three, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(onePair1);

        List<Card> onePair2 = new ArrayList<>();
        onePair2.add(new Card(Ace, Heart));
        onePair2.add(new Card(Ace, Diamond));
        onePair2.add(new Card(Three,Club));
        onePair2.add(new Card(Seven, Club));
        onePair2.add(new Card(Jack, Heart));
        onePair2.add(new Card(Four, Heart));
        onePair2.add(new Card(Ten, Heart));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(onePair2);

        int compareValue = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1,compareValue);
    }

    @Test
    public void testCompareStraight() {
        List<Card> highCard1 = new ArrayList<>();
        highCard1.add(new Card(Ace, Club));
        highCard1.add(new Card(Two, Spade));
        highCard1.add(new Card(Three, Diamond));
        highCard1.add(new Card(Four, Spade));
        highCard1.add(new Card(Five, Diamond));
        highCard1.add(new Card(Jack, Spade));
        highCard1.add(new Card(Nine, Spade));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup1 = TexasCardUtil.getMaxCardsGroup(highCard1);

        List<Card> highCard2 = new ArrayList<>();
        highCard2.add(new Card(Seven, Spade));
        highCard2.add(new Card(Eight, Spade));
        highCard2.add(new Card(Nine, Diamond));
        highCard2.add(new Card(Ten, Spade));
        highCard2.add(new Card(Jack, Diamond));
        highCard2.add(new Card(Two, Diamond));
        highCard2.add(new Card(Four, Club));
        CardsGroup<Constant.TexasType, List<Card>> maxCardsGroup2 = TexasCardUtil.getMaxCardsGroup(highCard2);

        int compareValue = TexasCardUtil.compareValue(maxCardsGroup1, maxCardsGroup2);
        Assert.assertEquals(-1,compareValue);
    }

    @Test
    public void testCompareTwoPairAndCalc() {
        List<Card> communityCards = new ArrayList<>();
        communityCards.add(new Card(King, Diamond));
        communityCards.add(new Card(Queen, Club));
        communityCards.add(new Card(Six, Diamond));
        communityCards.add(new Card(Five, Heart));
        communityCards.add(new Card(Five, Diamond));

        List<Card> hands1 = new ArrayList<>();
        hands1.add(new Card(Queen,Diamond));
        hands1.add(new Card(Queen,Spade));
        hands1.add(new Card(Five,Spade));
        hands1.add(new Card(Five,Club));
        hands1.add(new Card(Seven,Club));

        List<Card> hands2 = new ArrayList<>();
        hands2.add(new Card(Queen,Diamond));
        hands2.add(new Card(Queen,Spade));
        hands2.add(new Card(Seven,Spade));
        hands2.add(new Card(Seven,Heart));
        hands2.add(new Card(Five,Heart));
        CardsGroup<Constant.TexasType, List<Card>> cardsGroup1 = TexasCardUtil.getMaxCardsGroup(hands1);
        CardsGroup<Constant.TexasType, List<Card>> cardsGroup2 = TexasCardUtil.getMaxCardsGroup(hands2);
        int compareValue = TexasCardUtil.compareValue(cardsGroup1, cardsGroup2);
        Assert.assertEquals(-1, compareValue);

        List<Card> hands3 = new ArrayList<>();
        hands3.add(new Card(Queen,Diamond));
        hands3.add(new Card(Queen,Spade));
        hands3.add(new Card(Five,Spade));
        hands3.add(new Card(Five,Club));
        hands3.add(new Card(Seven,Club));

        List<Card> hands4 = new ArrayList<>();
        hands4.add(new Card(Queen,Heart));
        hands4.add(new Card(Queen,Club));
        hands4.add(new Card(Five,Diamond));
        hands4.add(new Card(Five,Heart));
        hands4.add(new Card(Seven,Heart));
        CardsGroup<Constant.TexasType, List<Card>> cardsGroup3 = TexasCardUtil.getMaxCardsGroup(hands3);
        CardsGroup<Constant.TexasType, List<Card>> cardsGroup4 = TexasCardUtil.getMaxCardsGroup(hands4);
        compareValue = TexasCardUtil.compareValue(cardsGroup3, cardsGroup4);
        Assert.assertEquals(0, compareValue);
    }

    @Test
    public void testCompareHighCardTwo() {
        List<Card> communityCards = new ArrayList<>();
        communityCards.add(new Card(Nine, Diamond));
        communityCards.add(new Card(Nine, Club));
        communityCards.add(new Card(Nine, Diamond));
        communityCards.add(new Card(Eight, Spade));
        communityCards.add(new Card(Ace, Diamond));

        List<Card> hands1 = new ArrayList<>();
        hands1.add(new Card(Seven,Heart));
        hands1.add(new Card(Six,Club));
        hands1.addAll(communityCards);
        List<Card> hands2 = new ArrayList<>();
        hands2.add(new Card(King,Heart));
        hands2.add(new Card(Six,Heart));
        hands2.addAll(communityCards);
        List<Card> hands3 = new ArrayList<>();
        hands3.add(new Card(Five,Spade));
        hands3.add(new Card(Seven,Diamond));
        hands3.addAll(communityCards);

        CardsGroup<Constant.TexasType, List<Card>> cardsGroup1 = TexasCardUtil.getMaxCardsGroup(hands1);
        CardsGroup<Constant.TexasType, List<Card>> cardsGroup2 = TexasCardUtil.getMaxCardsGroup(hands2);
        CardsGroup<Constant.TexasType, List<Card>> cardsGroup3 = TexasCardUtil.getMaxCardsGroup(hands3);

        Map<Integer, CardsGroup<Constant.TexasType, List<Card>>> finalCardsMap = new HashMap<>();
        finalCardsMap.put(0, cardsGroup1);
        finalCardsMap.put(1, cardsGroup2);
        finalCardsMap.put(2, cardsGroup3);
        List<Integer> winPlayerList = new ArrayList<>();
        CardsGroup<Constant.TexasType, List<Card>> listOld = null;
        for (Map.Entry<Integer, CardsGroup<Constant.TexasType, List<Card>>> entry : finalCardsMap.entrySet()) {
            if (listOld == null) {
                listOld = entry.getValue();
                winPlayerList.add(entry.getKey());
            } else {
                CardsGroup<Constant.TexasType, List<Card>> listNew = entry.getValue();
                int result = TexasCardUtil.compareValue(entry.getValue(), listOld);
                if (result == 1) {
                    winPlayerList.clear();
                    winPlayerList.add(entry.getKey());
                    listOld = listNew;
                } else if (result == 0) {
                    winPlayerList.add(entry.getKey());
                }
            }
        }
       Assert.assertFalse(winPlayerList.isEmpty());
    }
}
