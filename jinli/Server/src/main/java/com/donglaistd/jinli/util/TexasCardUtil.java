package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.texas.CardsGroup;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.CardNumber.*;
import static com.donglaistd.jinli.Constant.GameType.TEXAS_GAME;
import static com.donglaistd.jinli.Constant.TexasType.*;
import static com.donglaistd.jinli.constant.GameConstant.*;

public class TexasCardUtil {

    public static List<Integer> maxCards = List.of(Ten_VALUE, Jack_VALUE, Queen_VALUE, King_VALUE, Ace_VALUE);
    public static List<Integer> minStraight = List.of(Ace_VALUE, Two_VALUE, Three_VALUE, Four_VALUE, Five_VALUE);

    // 同花顺
    public static boolean isStraightFlush(List<Card> list) {
        List<Card> listDiamond = new ArrayList<>();
        List<Card> listClub = new ArrayList<>();
        List<Card> listHeart = new ArrayList<>();
        List<Card> listSpade = new ArrayList<>();
        separateByCardtype(list, listDiamond, listClub, listHeart, listSpade);
        return TexasCardUtil.isIncludeStraight(listDiamond) || TexasCardUtil.isIncludeStraight(listClub) || TexasCardUtil.isIncludeStraight(listHeart)
                || TexasCardUtil.isIncludeStraight(listSpade);
    }

    private static void separateByCardtype(List<Card> list, List<Card> listDiamond, List<Card> listClub, List<Card> listHeart, List<Card> listSpade) {
        for (Card card : list) {
            switch (card.getCardType()) {
                case Diamond:
                    listDiamond.add(card);
                    break;
                case Club:
                    listClub.add(card);
                    break;
                case Heart:
                    listHeart.add(card);
                    break;
                case Spade:
                    listSpade.add(card);
                    break;
            }
        }
    }

    // 四条
    public static boolean isFourOfAKind(List<Card> list) {
        List<Integer> valueList = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        for (int i = 0; i < valueList.size(); i++) {
            if ((valueList.lastIndexOf(valueList.get(i)) - i) == 3) {
                return true;
            }
        }
        return false;
    }

    // 葫芦
    public static boolean isFullHouse(List<Card> list) {
        List<Integer> valueList = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        List<Integer> ls = new ArrayList<>();
        boolean flag = false;

        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.lastIndexOf(valueList.get(i)) - i == 2) {
                ls.addAll(valueList.subList(i, i + 3));
                valueList.removeAll(ls);
                flag = true;
                break;
            }
        }
        if (flag) {
            for (int i = 0; i < valueList.size(); i++) {
                if (valueList.lastIndexOf(valueList.get(i)) - i >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    // 同花
    public static boolean isFlush(List<Card> list) {
        List<Integer> valueList = list.stream().map(Card::getCardTypeValue).sorted().collect(Collectors.toList());
        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.lastIndexOf(valueList.get(i)) - i >= 4) {
                return true;
            }
        }
        return false;
    }

    // 顺子
    public static boolean isIncludeStraight(List<Card> list) {
        Collections.sort(list);
        List<Integer> collect = list.stream().map(Card::getCardNumber).collect(Collectors.toList());
        if (collect.containsAll(minStraight)) return true;
        for (var i = 0; i < list.size() - 4; i++) {
            if (isSortedCardsStraight(list.subList(i, i + 5))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isStraight(List<Card> list) {
        List<Integer> value = list.stream().map(Card::getCardNumber).collect(Collectors.toList());
        if (value.containsAll(minStraight)) return true;
        List<Integer> maxList = new ArrayList<>();
        int max = Constant.CardNumber.Ace_VALUE;
        maxList.add(max);
        for (int i = 0; i < 4; i++) {
            maxList.add(max - i);
        }
        if (list.containsAll(maxList)) {
            return true;
        } else {
            maxList.clear();
            for (int a = list.size() - 1; a >= 0; a--) {
                int v = list.get(a).getCardNumber();
                maxList.clear();
                for (int j = 0; j < 5; j++, v--) {
                    maxList.add(v);
                }
                List<Integer> collect = list.stream().map(Card::getCardValue).map(Constant.CardNumber::getNumber).collect(Collectors.toList());
                if (collect.containsAll(maxList)) {
                    return true;
                }
            }
        }
        return false;
    }
    private static boolean isSortedCardsStraight(List<Card> list) {
        if (list.size() < TEXAS_CARD_SIZE) return false;
        for (var i = 0; i < list.size() - 1; i++) {
            if (i == 3 && list.get(i + 1).getCardValue().equals(Ace) && list.get(i).getCardValue().equals(Five)) {
                return true;
            }
            if (list.get(i + 1).getCardValue().getNumber() - list.get(i).getCardValue().getNumber() != 1) {
                return false;
            }
        }
        return true;
    }

    // 三条

    public static boolean isThreeOfAKind(List<Card> list) {
        List<Integer> valueList = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.lastIndexOf(valueList.get(i)) - i == 2) {
                return true;
            }
        }
        return false;
    }

    // 判断两对
    public static boolean isTwoPair(List<Card> list) {
        List<Integer> valueList = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        List<Integer> ls = new ArrayList<>();
        boolean flag = false;
        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.lastIndexOf(valueList.get(i)) - i == 1) {
                ls.addAll(valueList.subList(i, i + 2));
                valueList.removeAll(ls);
                flag = true;
                break;
            }
        }
        if (flag) {
            for (int i = 0; i < valueList.size(); i++) {
                if (valueList.lastIndexOf(valueList.get(i)) - i >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    // 对子
    public static boolean isOnePair(List<Card> list) {
        List<Integer> valueList = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.lastIndexOf(valueList.get(i)) - i == 1) {
                return true;
            }
        }
        return false;
    }

    //  得到最大的顺子_通用
    public static List<Integer> getMaxShunZi(List<Card> list) {
        List<Integer> maxList = new ArrayList<>(maxCards);
        List<Integer> collect = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        if (collect.containsAll(minStraight)&&!collect.contains(Six_VALUE)) return minStraight;
        if (!collect.containsAll(maxList)) {
            maxList.clear();
            for (int a = collect.size() - 1; a >= 0; a--) {
                int v = collect.get(a);
                maxList.clear();
                for (int j = 0; j < 5; j++, v--) {
                    maxList.add(v);
                }
                if (collect.containsAll(maxList)) {
                    break;
                }
            }
        }
        return maxList;
    }

    // 获取最大同花顺
    public static List<Card> getMaxStraightFlush(List<Card> list) {
        List<Card> list1 = new ArrayList<>();
        List<Card> list2 = new ArrayList<>();
        List<Card> list3 = new ArrayList<>();
        List<Card> list4 = new ArrayList<>();
        List<Card> result = new ArrayList<>();

        separateByCardtype(list, list1, list2, list3, list4);

        if (list1.size() >= TEXAS_CARD_SIZE) {
            Collections.sort(list1);
            for (Integer value : getMaxShunZi(list1)) {
                list.stream().filter(card -> card.getCardNumber() == value).findFirst().ifPresent(result::add);
            }
        }
        if (list2.size() >= TEXAS_CARD_SIZE) {
            Collections.sort(list2);
            for (Integer value : getMaxShunZi(list2)) {
                list.stream().filter(card -> card.getCardNumber() == value).findFirst().ifPresent(result::add);
            }
        }
        if (list3.size() >= TEXAS_CARD_SIZE) {
            Collections.sort(list3);
            for (Integer value : getMaxShunZi(list3)) {
                list.stream().filter(card -> card.getCardNumber() == value).findFirst().ifPresent(result::add);

            }
        }
        for (Integer value : getMaxShunZi(list4)) {
            Collections.sort(list4);
            list.stream().filter(card -> card.getCardNumber() == value).findFirst().ifPresent(result::add);
        }
        return result;
    }

    // 获取最大单张
    public static List<Card> getMaxHighCard(List<Card> list) {
        list.sort(Comparator.comparing(Card::getCardNumber).reversed());
        return list.subList(0, TEXAS_CARD_SIZE);
    }

    // 获取最大四条
    public static List<Card> getMaxFourOfAKind(List<Card> cards) {
        List<Card> result = new ArrayList<>();
        Map<Integer, List<Card>> collect = cards.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        collect.entrySet().stream().filter(e->e.getValue().size()==TEXAS_FOUR_OF_KIND_SAME_SIZE).findFirst().map(Map.Entry::getValue).ifPresent(result::addAll);
        cards.removeAll(result);
        cards.stream().max(Comparator.comparing(Card::getCardNumber)).ifPresent(result::add);
        return result;
    }

    // 获取最大葫芦
    public static List<Card> getMaxFullHouse(List<Card> list) {
        list = list.stream().sorted(Comparator.comparing(Card::getCardNumber)).collect(Collectors.toList());
        List<Card> max = new ArrayList<>();
        List<Integer> valueList = list.stream().map(Card::getCardNumber).sorted().collect(Collectors.toList());
        boolean flag = false;

        for (int i = valueList.size() - 1; i >= 0; i--) {
            if (i - valueList.indexOf(valueList.get(i)) == 2) {
                max.addAll(list.subList(i - 2, i + 1));
                list.removeAll(max);
                valueList.removeAll(max.stream().map(Card::getCardNumber).collect(Collectors.toList()));
                flag = true;
                break;
            }
        }
        if (flag) {
            for (int i = valueList.size() - 1; i >= 0; i--) {
                if (i - valueList.indexOf(valueList.get(i)) >= 1) {
                    max.addAll(list.subList(i - 1, i + 1));
                    break;
                }
            }

        }
        return max;
    }

    // 获取最大同花
    public static List<Card> getMaxFlush(List<Card> list) {
        Map<Constant.CardType, List<Card>> collect = list.stream().collect(Collectors.groupingBy(Card::getCardType));
        List<Card> maxFlush = collect.entrySet().stream().filter(e -> e.getValue().size() >= 5).findFirst().map(Map.Entry::getValue).get();
        maxFlush.sort(Comparator.comparing(Card::getCardNumber).reversed());
        return maxFlush.subList(0, TEXAS_CARD_SIZE);
    }

    // 获取最大顺子
    public static List<Card> getStraight_Card(List<Card> list) {
        List<Integer> valueList = getMaxShunZi(list);
        List<Card> ls = new ArrayList<>();
        for (Integer value : valueList) {
            list.stream().filter(card -> card.getCardNumber() == value).findFirst().ifPresent(ls::add);
        }
        return ls;
    }

    // 获取最大三条
    public static List<Card> getMaxThreeOfAKind(List<Card> list) {
        list.sort(Comparator.comparing(Card::getCardNumber).reversed());
        List<Card> result = new ArrayList<>();
        Map<Integer, List<Card>> collect = list.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        collect.entrySet().stream().filter(e->e.getValue().size()==TEXAS_THREE_OF_KIND_SAME_SIZE).max(Map.Entry.comparingByKey()).map(Map.Entry::getValue).ifPresent(result::addAll);
        list.removeAll(result);
        result.addAll(list.subList(0, 2));
        return result;
    }

    // 获取最大两对
    public static List<Card> getMaxTwoPair(List<Card> list) {
        list.sort(Comparator.comparing(Card::getCardNumber).reversed());
        List<Card> result = new ArrayList<>();
        Map<Integer, List<Card>> collect = list.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        collect.entrySet().stream().sorted(Map.Entry.<Integer, List<Card>>comparingByKey().reversed()).filter(e->e.getValue().size()==2).limit(2).map(Map.Entry::getValue).forEach(result::addAll);
        list.removeAll(result);
        list.stream().max(Comparator.comparing(Card::getCardNumber)).ifPresent(result::add);
        return result;
    }

    // 获取最大对子
    public static List<Card> getMaxOnePair(List<Card> list) {
        List<Card> maxOnePair = new ArrayList<>();
        list.sort(Comparator.comparing(Card::getCardNumber).reversed());
        Map<Integer, List<Card>> map = list.stream().collect(Collectors.groupingBy(Card::getCardNumber));
        map.entrySet().stream().filter(e->e.getValue().size()==2).max(Map.Entry.comparingByKey()).map(Map.Entry::getValue).ifPresent(maxOnePair::addAll);
        list.removeAll(maxOnePair);
        maxOnePair.addAll(list.subList(0, 3));
        return maxOnePair;
    }

    // 判断牌型
    public static CardsGroup<Constant.TexasType, List<Card>> getMaxCardsGroup(List<Card> list) {
        Constant.TexasType type;
        List<Card> result;
        if (isStraightFlush(list)) {
            result = getMaxStraightFlush(list);
            if (list.get(0).getCardValue() == Ace) {
                type = RoyalStraightFlush;
            } else {
                result = getMaxStraightFlush(list);
                type = StraightFlush;
            }
        } else if (isFourOfAKind(list)) {
            result = getMaxFourOfAKind(list);
            type = FourOfAKind;
        } else if (isFullHouse(list)) {
            result = getMaxFullHouse(list);
            type = FullHouse;
        } else if (isFlush(list)) {
            result = getMaxFlush(list);
            type = Texas_Flush;
        } else if (isStraight(list)) {
            result = getStraight_Card(list);
            type = Straight;
        } else if (isThreeOfAKind(list)) {
            result = getMaxThreeOfAKind(list);
            type = ThreeOfAKind;
        } else if (isTwoPair(list)) {
            result = getMaxTwoPair(list);
            type = TwoPair;
        } else if (isOnePair(list)) {
            result = getMaxOnePair(list);
            type = OnePair;
        } else {
            result = getMaxHighCard(list);
            type = HighCard;
        }
        return new CardsGroup<>(type, result);
    }

    // 比较牌型 List1,List2比较大小 List1>List2返回1、List1等于List2返回0、List1<List2返回-1
    public static int compareValue(CardsGroup<Constant.TexasType, List<Card>> listNew, CardsGroup<Constant.TexasType, List<Card>> listOld) {
        Constant.TexasType newType = listNew.getTexasType();
        Constant.TexasType oldType = listOld.getTexasType();

        List<Card> list1 = listNew.getCardsList();
        List<Card> list2 = listOld.getCardsList();
        if ((newType.getNumber() - oldType.getNumber()) > 0) {
            return 1;
        } else if ((newType.getNumber() - oldType.getNumber()) < 0) {
            return -1;
        } else {
            switch (newType) {
                case RoyalStraightFlush:
                case StraightFlush:
                case Straight:
                    return compareStraightCards(list1, list2);
                case FourOfAKind:
                    return compareSameCards(list1, list2,TEXAS_FOUR_OF_KIND_SAME_SIZE);
                case FullHouse:
                case ThreeOfAKind:
                    return compareSameCards(list1, list2,TEXAS_THREE_OF_KIND_SAME_SIZE);
                case Texas_Flush:
                case HighCard:
                    return compareOneByOne(list1, list2);
                case TwoPair:
                    return compareTwoPairCards(list1, list2);
                case OnePair:
                    return compareOnePairCards(list1, list2);
            }
            return 0;
        }
    }

    public static boolean isSameCardPoints(List<Card> list1, List<Card> list2) {
        List<Integer> cards1 = list1.stream().map(card -> CardRulerUtil.getCardPointByGameType(card, TEXAS_GAME)).collect(Collectors.toList());
        List<Integer> cards2 = list2.stream().map(card -> CardRulerUtil.getCardPointByGameType(card, TEXAS_GAME)).collect(Collectors.toList());
        return cards1.equals(cards2);
    }

    public static int compareStraightCards(List<Card> list1, List<Card> list2) {
        if (isSameCardPoints(list1, list2)) return 0;
        List<Integer> value1 = list1.stream().map(Card::getCardNumber).collect(Collectors.toList());
        List<Integer> value2 = list2.stream().map(Card::getCardNumber).collect(Collectors.toList());
        list1.sort(Comparator.comparing(Card::getCardNumber).reversed());
        list2.sort(Comparator.comparing(Card::getCardNumber).reversed());
        if (value1.containsAll(minStraight)) {
            Card ace = list1.get(0);
            list1.remove(ace);
            list1.add(ace);
        }
        if (value2.containsAll(minStraight)) {
            Card ace = list2.get(0);
            list2.remove(ace);
            list2.add(ace);
        }
        return CardRulerUtil.compareCardsForOneToOne(list1, list2, TEXAS_GAME, false) ? 1 : -1;
    }

    public static int compareOnePairCards(List<Card> list1, List<Card> list2) {
        if (isSameCardPoints(list1, list2)) return 0;
        return CardRulerUtil.compareCardsForPair(list1, list2, TEXAS_GAME, TEXAS_PAIR_SAME_SIZE) ? 1 : -1;
    }

    public static int compareTwoPairCards(List<Card> list1, List<Card> list2) {
        if (isSameCardPoints(list1, list2)) return 0;
        return CardRulerUtil.compareCardsForPair(list1, list2, TEXAS_GAME, TEXAS_PAIR_SAME_SIZE) ? 1 : -1;
    }

    public static int compareSameCards(List<Card> list1, List<Card> list2, int sameCardsSize) {
        return CardRulerUtil.compareCardsForPair(list1, list2, TEXAS_GAME, sameCardsSize) ? 1 : -1;
    }
    public static int compareOneByOne(List<Card> list1, List<Card> list2) {
        if(isSameCardPoints(list1,list2)) return 0;
        return CardRulerUtil.compareCardsForOneToOne(list1, list2, TEXAS_GAME) ? 1 : -1;
    }
}
