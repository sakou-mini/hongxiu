package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;

import java.util.*;
import java.util.stream.Collectors;

public class RedBlackResult {
    private final Constant.RedBlackType redBlackType;

    private final List<Card> compareCard;


    public Constant.RedBlackType getRedBlackType() {
        return this.redBlackType;
    }

    public List<Card> getCards() {
        return this.compareCard;
    }

    public RedBlackResult(Constant.RedBlackType redBlackType, List<Card> cards) {
        this.redBlackType = redBlackType;
        this.compareCard = cards;
    }

    public static RedBlackResult getRedBlackResult(List<Card> cards) {
        if (cards == null || cards.size() != 3) {
            throw new RuntimeException("The RedBlack Required 3 size");
        }
        if (isLeopard(cards)) {
            return new RedBlackResult(Constant.RedBlackType.Leopard, getOrderList(cards, Constant.RedBlackType.Leopard));
        } else if (isFlushFlowers(cards)) {
            return new RedBlackResult(Constant.RedBlackType.Flush_Flowers, getOrderList(cards, Constant.RedBlackType.Flush_Flowers));
        } else if (isSameFlowers(cards)) {
            return new RedBlackResult(Constant.RedBlackType.SAME_Flowers, getOrderList(cards, Constant.RedBlackType.SAME_Flowers));
        } else if (isFlush(cards)) {
            return new RedBlackResult(Constant.RedBlackType.Flush, getOrderList(cards, Constant.RedBlackType.Flush));
        } else if (isPari(cards)) {
            return new RedBlackResult(Constant.RedBlackType.Pair, getOrderList(cards, Constant.RedBlackType.Pair));
        } else {
            return new RedBlackResult(Constant.RedBlackType.Single, getOrderList(cards, Constant.RedBlackType.Single));
        }

    }

    public static boolean isLeopard(List<Card> cards) {
        if (cards == null || cards.size() == 0) {
            return false;
        }
        return cards.get(0).getCardValue().getNumber() == cards.get(1).getCardValue().getNumber() &&
                cards.get(1).getCardValue().getNumber() == cards.get(2).getCardValue().getNumber();
    }

    public static boolean isFlushFlowers(List<Card> cards) {
        if (cards == null || cards.size() == 0) {
            return false;
        }
        var nCards = cards.stream().sorted(Comparator.comparing(Card::getCardValue)).collect(Collectors.toList());
        var flush = nCards.get(0).getCardValue().getNumber() + 1 == nCards.get(1).getCardValue().getNumber() &&
                nCards.get(1).getCardValue().getNumber() + 1 == nCards.get(2).getCardValue().getNumber();

        if (!flush)
            return false;

        return nCards.get(0).getCardType() == nCards.get(1).getCardType() &&
                nCards.get(1).getCardType() == nCards.get(2).getCardType();

    }

    public static boolean isSameFlowers(List<Card> cards) {
        if (cards == null || cards.size() == 0) {
            return false;
        }
        return cards.get(0).getCardType() == cards.get(1).getCardType() &&
                cards.get(1).getCardType() == cards.get(2).getCardType();
    }

    public static boolean isFlush(List<Card> cards) {
        if (cards == null || cards.size() == 0) {
            return false;
        }
        var nCards = cards.stream().sorted(Comparator.comparing(Card::getCardValue)).collect(Collectors.toList());
        return nCards.get(0).getCardValue().getNumber() + 1 == nCards.get(1).getCardValue().getNumber() &&
                nCards.get(1).getCardValue().getNumber() + 1 == nCards.get(2).getCardValue().getNumber();
    }

    public static boolean isPari(List<Card> cards) {
        if (cards == null || cards.size() == 0) {
            return false;
        }
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Card card : cards) {
            countMap.compute(card.getCardValue().getNumber(), (k, v) -> v = v == null ? 1 : v + 1);
        }
        Integer maxCount = countMap.values().stream().max(Integer::compare).get();
        return maxCount >= 2;
    }

    public static Card getMaxCardFromList(List<Card> cards) {
        if (cards == null || cards.size() == 0) {
            return null;
        }
        return cards.stream().sorted(Comparator.comparing(Card::getCardValue).reversed()).collect(Collectors.toList()).get(0);
    }

    public static List<Card> getOrderList(List<Card> list, Constant.RedBlackType type) {
        if (type == Constant.RedBlackType.Pair) {
            List<Card> tmp = new ArrayList<>(list);

            List<Card> cards = new ArrayList<>();
            if (list.get(0).getCardValue().compareTo(list.get(1).getCardValue()) == 0) {
                //GET 0 1
                cards.add(tmp.remove(0));
                cards.add(tmp.remove(0));
            } else if (list.get(1).getCardValue().compareTo(list.get(2).getCardValue()) == 0) {
                //GET 2 3
                cards.add(tmp.remove(1));
                cards.add(tmp.remove(1));
            } else {
                //GET 1 3
                cards.add(tmp.remove(0));
                cards.add(tmp.remove(1));
            }

            cards.sort(Comparator.comparing(Card::getCardType));
            cards.add(tmp.remove(0));
            return cards;
        } else {
            return list.stream().sorted(Comparator.comparing(Card::getCardValue).reversed()).collect(Collectors.toList());

        }

    }
}