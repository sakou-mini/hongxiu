package com.donglaistd.jinli.util.landlords;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.landlord.PokerRecord;
import com.donglaistd.jinli.util.CardRulerUtil;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.CardNumber.Ace_VALUE;
import static com.donglaistd.jinli.Constant.GameType.LANDLORD_GAME;
import static com.donglaistd.jinli.Constant.LandlordsType.*;
import static com.donglaistd.jinli.constant.GameConstant.*;

public class LandlordsPokerUtil {

    public static Constant.LandlordsType checkPokerType(List<Card> cards){
        int count = cards.size();
        if (count <= 0) return Poker_null;
        switch (count) {
            case 1: return CardTypeService.SingleCardsService.getPokerType(cards);
            case 2: return CardTypeService.TwoCardsService.getPokerType(cards);
            case 3: return CardTypeService.ThreeCardsService.getPokerType(cards);
            case 4: return CardTypeService.FourCardsService.getPokerType(cards);
            default: return CardTypeService.OthersCardsService.getPokerType(cards);
        }
    }

    public static void sortCards(List<Card> cards) {
        if(Objects.nonNull(cards) && !cards.isEmpty())
            cards.sort(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME));
    }

    public static Map<Integer, Long> groupCardCount(List<Card> cards){
        return cards.stream().collect(Collectors.groupingBy(card -> CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME), Collectors.counting()));
    }

    public static boolean hasSameCountCard(List<Card> cards, int count){
        Map<Integer, Long> cardsCount = groupCardCount(cards);
        return cardsCount.values().stream().anyMatch(v -> v >= count);
    }
    public static List<Integer> getBombPoints(List<Card> cards){
        Map<Integer, Long> groupCards = groupCardCount(cards);
        return groupCards.entrySet().stream().filter(groupCard -> groupCard.getValue() == BOOM_CARDSIZE).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public static int getPairNum(List<Card> cards){
        Map<Integer, Long> groupCards = groupCardCount(cards);
        return (int) groupCards.entrySet().stream().filter(groupCard -> groupCard.getValue().intValue() == POKER_PAIR_CARD_SIZE).count();
    }

    public static int getGroupPairNum(List<Card> cards){
        Map<Integer, Long> groupCards = groupCardCount(cards);
        List<Long>  cardsNum =  groupCards.values().stream().filter(cardSize -> cardSize.intValue() >= POKER_PAIR_CARD_SIZE).collect(Collectors.toList());
        int pairNum = 0;
        for (Long size : cardsNum) {
            pairNum += size.intValue() / 2;
        }
        return pairNum;
    }

    public static List<Integer> getPairPoints(List<Card> cards){
        Map<Integer, Long> groupCards = groupCardCount(cards);
        return groupCards.entrySet().stream().filter(groupCard -> groupCard.getValue().intValue() == POKER_PAIR_CARD_SIZE).map(Map.Entry::getKey).sorted(Integer::compareTo).collect(Collectors.toList());
    }

    public static Set<Integer> getAfterMinPointStraightFromCardPoints(List<Integer> cardPoints, int reqLength, int minPoint){
        Set<Integer> straight = new HashSet<>(reqLength);
        if(cardPoints.size() < reqLength) return straight;
        for (int i = 0; i < cardPoints.size()-1; i++) {
            if(straight.size()==reqLength) break;
            Integer lastPoint = cardPoints.get(i);
            Integer nextPoint = cardPoints.get(i + 1);
            if(nextPoint - lastPoint == 1 && lastPoint > minPoint) {
                straight.add(lastPoint);
                straight.add(nextPoint);
            }
            else straight.clear();
        }
        if(straight.size() < reqLength) straight.clear();
        return straight;
    }

    public static List<Integer> getPlaneCardPoint(List<Card> cards){
        Map<Integer, Long> groupCards = groupCardCount(cards);
       return groupCards.entrySet().stream().filter(groupCard -> groupCard.getValue().intValue() == POKER_THREE_SIZE).map(Map.Entry::getKey).sorted(Integer::compareTo).collect(Collectors.toList());
    }

    public static List<Integer> checkAndGetPlaneCards(List<Card> cards){
        List<Integer> planesPoints = getPlaneCardPoint(cards);
        int planeSize = planesPoints.size();
        if(planeSize < POKER_PLANE_MIN_SIZE) return new ArrayList<>();
        Set<Integer> plane = new HashSet<>(planesPoints.size());
        for(int i = 0 ; i<planeSize-1 ; i++){
            Integer lastPoint = planesPoints.get(i + 1);
            Integer prePoint = planesPoints.get(i);
            if(lastPoint - prePoint == 1) plane.addAll(List.of(prePoint,lastPoint));
        }
        if(plane.size() < POKER_PLANE_MIN_SIZE) plane.clear();
        return Lists.newArrayList(plane);
    }

    //check cardsType
    public static boolean isPokerJokerBomb(List<Card> cards){
        if(cards.size() != 2) return false;
        Card blackJoker = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card redJoker = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        return cards.containsAll(List.of(blackJoker, redJoker));
    }

    public static boolean isPokerThree(List<Card> cards){
        if(cards.size() != POKER_THREE_SIZE) return false;
        return hasSameCountCard(cards, cards.size());
    }

    public static boolean isPokerThreeAttachOne(List<Card> cards){
        if(cards.size() != POKER_THREE_ATTACH_ONE_SIZE) return false;
        List<Card> temp = new ArrayList<>(cards);
        return hasSameCountCard(temp, POKER_THREE_SIZE);
    }

    public static boolean isPokerThreeAttachTwo(List<Card> cards){
        if(cards.size()!= POKER_THREE_ATTACH_TWO_SIZE) return false;
        Map<Integer, Long> cardPointGroup = groupCardCount(cards);
        Collection<Long> cardPointSize = cardPointGroup.values();
        return cardPointSize.size()==2 && cardPointSize.containsAll(List.of(POKER_PAIR_CARD_SIZE, (long) POKER_THREE_SIZE));
    }

    public static boolean isPokerStraight(List<Card> cards){
        sortCards(cards);
        if(cards.size() < LANDLORD_STRAIGHT_MIN_SIZE || cards.get(0).getCardValue().getNumber()>Ace_VALUE) return false;
        for(int i=0 ; i<cards.size()-1 ; i++){
            int lastPoint = CardRulerUtil.getCardPointByGameType(cards.get(i),LANDLORD_GAME);
            int nextPoint = CardRulerUtil.getCardPointByGameType(cards.get(i+1),LANDLORD_GAME);
            if(lastPoint - nextPoint != 1) return false;
        }
        return true;
    }

    public static boolean isPokerSerialPair(List<Card> cards) {
        int size=cards.size();
        if(size < POKER_SERIAL_PAIR_MIN_SIZE || size%2!=0) return false;
        Map<Integer, Long> cardsCount = groupCardCount(cards);
        if(cardsCount.values().stream().anyMatch(cardCount->cardCount.intValue() != POKER_PAIR_CARD_SIZE)) return false;
        ArrayList<Integer> cardsPoints = Lists.newArrayList(cardsCount.keySet());
        cardsPoints.sort(Integer::compareTo);
        for (int i = 0; i < cardsPoints.size()-1; i++) {
            if(cardsPoints.get(i+1) - cardsPoints.get(i)!=1) return false;
        }
        return true;
    }

    public static boolean isPlane(List<Card> cards) {
        if( cards.size()% POKER_THREE_SIZE !=0) return false;
        List<Integer> planes = checkAndGetPlaneCards(cards);
        if(planes.isEmpty()) return false;
        return planes.size() * POKER_THREE_SIZE == cards.size();
    }

    public static boolean isPlaneAttachPair(List<Card> cards){
        List<Integer> planes = checkAndGetPlaneCards(cards);
        if(planes.isEmpty()) return false;
        List<Card> tempCards = new ArrayList<>(cards);
        tempCards.removeIf(card -> planes.contains(CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME)));
        int pairNum = getGroupPairNum(tempCards);
        return pairNum == planes.size() && ((pairNum * POKER_PAIR_CARD_SIZE) + (planes.size() * POKER_THREE_SIZE)) == cards.size();
    }

    public static boolean isPlaneAttachSingle(List<Card> cards){
        List<Card> tempCards = new ArrayList<>(cards);
        sortCards(tempCards);
        List<Integer> planes = checkAndGetPlaneCards(tempCards);
        if(planes.isEmpty()) return false;
        int single = (int) tempCards.stream().filter(card -> !planes.contains(CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME))).count();
        return single == planes.size() && single+planes.size() * POKER_THREE_SIZE == tempCards.size() ;
    }

    public static boolean isFourAttachTwo(List<Card> cards){
        sortCards(cards);
        if(!hasSameCountCard(cards, BOOM_CARDSIZE)) return false;
        if(cards.size() == POKER_FOUR_ATTACH_TWO_MIN_SIZE) return true;
        else if(cards.size() == 8){
            int pairNum = getPairNum(cards);
            int bomb = getBombPoints(cards).size();
            return pairNum == 2 || bomb == 2;
        }
        return false;
    }

    public static boolean comparePoker(PokerRecord prevPoker, PokerRecord currentPoker){
        Constant.LandlordsType prevPokerType = prevPoker.getPokerType();
        Constant.LandlordsType currentPokerType = currentPoker.getPokerType();
        if(prevPokerType.equals(Poker_jokerBomb)) return false;
        if(currentPokerType.equals(Poker_jokerBomb)) return true;
        if(currentPokerType.equals(prevPokerType) && prevPoker.getCardsSize() == currentPoker.getCardsSize()) {
            if(CardRulerUtil.isSameCardPoints(currentPoker.getCards(),prevPoker.getCards(),LANDLORD_GAME))
                return false;
            return CardRulerUtil.compareMaxCard(currentPoker.getBaseCard(), prevPoker.getBaseCard(), LANDLORD_GAME);
        }
        else return currentPoker.getPokerType().equals(Poker_bomb);
    }

    public static List<Card> splitAttachCards(List<Card> cards){
        Constant.LandlordsType landlordsType = checkPokerType(cards);
        List<Integer> filterPoint = new ArrayList<>();
        switch (landlordsType){
            case Poker_threeAndOne:
            case Poker_threeAndTwo:
                filterPoint = getPlaneCardPoint(cards);
                break;
            case Poker_plane:
            case Poker_planeAndPair:
            case Poker_planeAndSingle:
                filterPoint = checkAndGetPlaneCards(cards);
                break;
            case Poker_fourAndTwo:
                filterPoint.add(getBombPoints(cards).stream().max(Integer::compareTo).orElse(0));
                break;
        }
        if(filterPoint.isEmpty()) return new ArrayList<>(0);
        List<Integer> finalFilterPoint = filterPoint;
        return cards.stream().filter(card -> !finalFilterPoint.contains(CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME))).collect(Collectors.toList());
    }
}
