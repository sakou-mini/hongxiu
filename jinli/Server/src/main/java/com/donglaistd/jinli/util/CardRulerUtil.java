package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.exception.CardParameterException;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.*;

public class CardRulerUtil {

    public static Map<Integer, List<Card>> groupCardsByPoints(List<Card> cards, Constant.GameType gameType){
        return cards.stream().collect(Collectors.groupingBy(card -> CardRulerUtil.getCardPointByGameType(card,gameType)));
    }

    //card point
    public static int getCardRealPoint(Card card) {
        int cardPoint = card.getCardIntValue();
        if (card.isFaceCard())
            cardPoint = card.getCardValue().getNumber() + 1;
        return cardPoint;
    }

    protected static int getGoldenFlowerCardPoint(Card card) {
        int cardPoint = card.getCardIntValue();
        if (card.isFaceCard() || card.getCardValue().equals(Constant.CardNumber.Ace))
            cardPoint = card.getCardValue().getNumber() + 1;
        return cardPoint;
    }

    protected static int getLandlordsCardPoint(Card card) {
        int number = card.getCardValue().getNumber();
        if(card.getCardValue().equals(Constant.CardNumber.Two))
            return Constant.CardNumber.Ace_VALUE + 3;
        return number + 1;
    }

    protected static int getTexasCardPoint(Card card) {
        return card.getCardNumber();
    }
    public static int getCardPointByGameType(Card card,Constant.GameType gameType) {
        switch (gameType){
            case NIUNIU:
                return getCardRealPoint(card);
            case GOLDENFLOWER:
                return getGoldenFlowerCardPoint(card);
            case LANDLORD_GAME:
                return getLandlordsCardPoint(card);
            case TEXAS_GAME:
                return getTexasCardPoint(card);
        }
        return card.getCardIntValue();
    }
    //common cardType
    static public boolean isBomb(List<Card> cards) {
        Map<Constant.CardNumber, List<Card>> cardGroup = cards.stream().collect(Collectors.groupingBy(Card::getCardValue, Collectors.toList()));
        return cardGroup.values().stream().anyMatch(cardsList -> cardsList.size() >= BOOM_CARDSIZE);
    }

    static public Card getMaxCardFromList(List<Card> cards, Comparator<Card> cardComparator) {
        if (cards==null || cards.isEmpty()) {
            throw new CardParameterException("Cards is null!");
        }
        List<Card> cardList = new ArrayList<>(cards);
        cardList.sort(cardComparator);
        return cardList.get(0);
    }

    static public List<Card> getMinCardsFromCardList(List<Card> playerCards,Comparator<Card> cardComparator){
        if (playerCards==null || playerCards.isEmpty()) {
            throw new CardParameterException("Cards is null!");
        }
        playerCards.sort(cardComparator);
        return Lists.newArrayList(playerCards.get(playerCards.size() - 1));
    }

    static public boolean isSameCardType(List<Card> cards) {
        Constant.CardType firstType = cards.get(0).getCardType();
        return cards.stream().filter(card -> !card.getCardType().equals(firstType)).findFirst().isEmpty();
    }
    static public boolean isSameCardNumber(List<Card> cards) {
        Constant.CardNumber firstCardValue = cards.get(0).getCardValue();
        return cards.stream().filter(card -> !card.getCardValue().equals(firstCardValue)).findFirst().isEmpty();
    }

    static public boolean isPairCards(List<Card> cards) {
        Map<Constant.CardNumber, List<Card>> cardsTotal = cards.stream().collect(Collectors.groupingBy(Card::getCardValue, Collectors.toList()));
        return cardsTotal.values().stream().anyMatch(list -> list.size() >= 2);
    }

    static public List<Card> getMaxCardsByGroupSize(List<Card> cards, Constant.GameType gameType,long groupSize){
        Map<Integer, List<Card>> groupCards = groupCardsByPoints(cards, gameType);
        Integer maxPairPoint = groupCards.entrySet().stream().filter(group -> group.getValue().size() == groupSize).map(Map.Entry::getKey).max(Integer::compareTo).orElse(0);
        return groupCards.getOrDefault(maxPairPoint, new ArrayList<>(0));
    }

    //================================BullType Ruler==================================
    static public boolean isSmallBull(List<Card> cards) {
        int sum = cards.stream().mapToInt(Card::getCardIntValue).sum();
        boolean overCard = cards.stream().anyMatch(card -> card.getCardIntValue() >=SMALLBULL_CARD_MAX_POINT);
        return sum < SMALLBULL_POINT && !overCard;
    }

    static public Constant.BullType getFigureBullType(List<Card> cards){
        Map<Boolean, List<Card>> cardGroup = cards.stream().collect(Collectors.groupingBy(Card::isFaceCard, Collectors.toList()));
        List<Card> faceCards = cardGroup.getOrDefault(true,new ArrayList<>());
        List<Card> noFaceCards = cardGroup.getOrDefault(false,new ArrayList<>());
        if(faceCards.size() >= FIGUREBULL_5_FACECARD_SIZE && noFaceCards.isEmpty())
            return Constant.BullType.FigureBull_5;
        Card maxNoFaceCard = getMaxCardFromList(noFaceCards, ComparatorUtil.getCardComparatorForGameType(Constant.GameType.NIUNIU));
        if(faceCards.size() == FIGUREBULL_4_FACECARD_SIZE && maxNoFaceCard.getCardValue().equals(Constant.CardNumber.Ten))
            return Constant.BullType.FigureBull_4;
        return Constant.BullType.UNRECOGNIZED;
    }

    //get bullType(No_Bull ~ BullBull)
    static public Constant.BullType getBullType(List<Card> cards) {
        int sumPoint = cards.stream().mapToInt(Card::getCardIntValue).sum();
        int bull = -1;
        List<List<Card>> combinationsCards = ListsUtil.combinations(cards, 3);
        for (List<Card> cardsPair : combinationsCards) {
            int pairSum = cardsPair.stream().mapToInt(Card::getCardIntValue).sum();
            if (pairSum % 10 == 0) {
                int niu = (sumPoint - pairSum) % 10;
                if(niu==0)
                    return Constant.BullType.BullBull;
                bull = Math.max(bull,niu);
            }
        }
        return bull<0?Constant.BullType.No_Bull : Constant.BullType.forNumber(bull + 1);
    }

    //================================GoldenFlower Ruler==================================
    static public boolean isGoldenFlowerStraight(List<Card> cards) {
        List<Card> cardList = new ArrayList<>(cards);
        AtomicInteger count = new AtomicInteger();
        cardList.sort(ComparatorUtil.getCardComparatorForGameType(Constant.GameType.GOLDENFLOWER));
        Constant.CardNumber now;
        Constant.CardNumber next;
        for (int i = 0; i < cardList.size() - 1; i++) {
            now = cardList.get(i).getCardValue();
            next = cardList.get(i + 1).getCardValue();
            if (now.getNumber() - next.getNumber() == 1)
                count.incrementAndGet();
            else if (now.equals(Constant.CardNumber.Ace) && next.equals(Constant.CardNumber.Three)) {
                count.incrementAndGet();
            }
        }
        return count.get() == cardList.size() - 1;
    }

    static public boolean compareCardsForStraight(List<Card> currentCards, List<Card> prevCards, Constant.GameType gameType){
        int sum1 = sumStraightCards(currentCards,gameType);
        int sum2 = sumStraightCards(prevCards,gameType);
        if(sum1 == sum2){
            Card maxCard1 = CardRulerUtil.getMaxCardFromList(currentCards, ComparatorUtil.getCardComparatorForGameType(gameType));
            Card maxCard2 = CardRulerUtil.getMaxCardFromList(prevCards, ComparatorUtil.getCardComparatorForGameType(gameType));
            return maxCard1.greaterThanOtherCard(maxCard2,gameType);
        }
        return sum1 > sum2;
    }
    static private int sumStraightCards(List<Card> cards,Constant.GameType gameType){
        Collection<Constant.CardNumber> cardNumbers = cards.stream().map(Card::getCardValue).collect(Collectors.toList());
        int sum = cards.stream().mapToInt(card->CardRulerUtil.getCardPointByGameType(card,gameType)).sum();
        switch (gameType){
            case GOLDENFLOWER:
            case TEXAS_GAME:
                if (Min_Straight_Texas.containsAll(cardNumbers) || Min_Straight_GoldenFlower.containsAll(cardNumbers)) {
                    sum = cards.stream().mapToInt(Card::getCardIntValue).sum();
                }
                break;
        }
        return sum;
    }


    //common compare==========
    static public boolean compareMaxCard(List<Card> currentCards, List<Card> prevCards, Constant.GameType gameType){
        Comparator<Card> comparator = ComparatorUtil.getCardComparatorForGameType(gameType);
        Card maxCard1 = CardRulerUtil.getMaxCardFromList(currentCards, comparator);
        Card maxCard2 = CardRulerUtil.getMaxCardFromList(prevCards, comparator);
        return maxCard1.greaterThanOtherCard(maxCard2,gameType);
    }

    public static boolean compareCardsForOneToOne(List<Card> currentCards, List<Card> prevCards, Constant.GameType gameType,Boolean... autoSort){
        List<Card> cards1 = new ArrayList<>(currentCards);
        List<Card> cards2 = new ArrayList<>(prevCards);
        if(autoSort == null || autoSort.length ==0 ||autoSort[0]) {
            cards1.sort(ComparatorUtil.getCardComparatorForGameType(gameType));
            cards2.sort(ComparatorUtil.getCardComparatorForGameType(gameType));
        }
        for (int i = 0; i <cards1.size() ; i++) {
            if(!Objects.equals(cards1.get(i).getCardValue(),cards2.get(i).getCardValue())){
                return cards1.get(i).greaterThanOtherCard(cards2.get(i),gameType);
            }
        }
        return CardRulerUtil.compareMaxCard(cards1,cards2,gameType);
    }

    public static List<Integer> sameCardPointByCardNum(List<Card> cards, long sameCardNum, Constant.GameType gameType){
        return groupCardsByPoints(cards,gameType).entrySet().stream().filter(entry -> entry.getValue().size() == sameCardNum).map(Map.Entry::getKey)
                .sorted(Comparator.comparing(Integer::intValue).reversed()).collect(Collectors.toList());
    }

    public static boolean compareCardsForPair(List<Card> currentCards, List<Card> prevCards, Constant.GameType gameType, long sameCardNum){
        List<Integer> cardsPoint1 = sameCardPointByCardNum(currentCards,sameCardNum,gameType);
        List<Integer> cardsPoint2 = sameCardPointByCardNum(prevCards,sameCardNum,gameType);
        if(cardsPoint1.size()!=cardsPoint2.size()) return false;
        int pairSize = cardsPoint1.size();
        for (int i = 0; i < pairSize; i++) {
            if(!cardsPoint1.get(i).equals(cardsPoint2.get(i)))
                return cardsPoint1.get(i) > cardsPoint2.get(i);
        }
        List<Card> singleCards1 = currentCards.stream().filter(card -> !cardsPoint1.contains(getCardPointByGameType(card,gameType))).collect(Collectors.toList());
        List<Card> singleCards2 = prevCards.stream().filter(card -> !cardsPoint2.contains(getCardPointByGameType(card,gameType))).collect(Collectors.toList());
        return compareCardsForOneToOne(singleCards1, singleCards2, gameType);
    }

    public static boolean isSameCardPoints(List<Card> currentCards, List<Card> prevCards, Constant.GameType gameType){
        List<Integer> cards1 = currentCards.stream().map(card -> CardRulerUtil.getCardPointByGameType(card,gameType)).collect(Collectors.toList());
        List<Integer> cards2 = prevCards.stream().map(card -> CardRulerUtil.getCardPointByGameType(card,gameType)).collect(Collectors.toList());
        return cards1.equals(cards2);
    }
}
