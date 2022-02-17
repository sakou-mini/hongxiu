package com.donglaistd.jinli.util.landlords;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.game.landlord.PokerRecord;
import com.donglaistd.jinli.util.CardRulerUtil;
import com.donglaistd.jinli.util.ComparatorUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.GameType.LANDLORD_GAME;
import static com.donglaistd.jinli.Constant.LandlordsType.*;
import static com.donglaistd.jinli.constant.GameConstant.*;
import static com.donglaistd.jinli.util.landlords.LandlordsPokerUtil.*;

public class LandlordsPokerPlayCardsUtil {

    public static final Card BLACK_JOKER = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
    public static final Card RED_JOKER = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);

    public static List<Card> autoPlayCards(PokerRecord prevPokerRecord, List<Card> playerCards) {
        List<Card> playCards = new ArrayList<>(0);
        Constant.LandlordsType prevCardsType = prevPokerRecord.getPokerType();
        List<Card> prevCards = prevPokerRecord.getCards();
        if (prevCardsType.equals(Poker_jokerBomb) || prevCardsType.equals(Poker_null))
            return playCards;
        if (prevCardsType.equals(Poker_single)) {
            playCards = playSingleCard(prevCards, playerCards);
        } else if (prevCardsType.equals(Poker_pair)) {
            playCards = playPair(prevCards, playerCards);
        } else if (prevCardsType.equals(Poker_serialPair)) {
            playCards = playSerialPairCard(prevCards, playerCards);
        } else if (prevCardsType.equals(Poker_fourAndTwo)) {
            playCards = playFourAndTwo(prevCards, playerCards);
        } else if (prevCardsType.equals(Poker_straight)) {
            playCards = playStraight(prevCards, playerCards);
        } else if (prevCardsType.getNumber() >= Poker_three_VALUE && prevCardsType.getNumber() <= Poker_threeAndTwo_VALUE) {
            playCards = playPokerThree(prevCards, playerCards);
        } else if (prevCardsType.getNumber() >= Poker_plane_VALUE && prevCardsType.getNumber() <= Poker_planeAndPair_VALUE) {
            playCards = playPlane(prevCards, playerCards);
        } else if (prevCardsType.equals(Poker_bomb)) {
            playCards = playBomb(prevCards, playerCards);
        }
        if (playCards.isEmpty()) {
            if (!prevCardsType.equals(Poker_bomb))
                playCards = playMinBomb(playerCards);
            else
                playCards = playJokerBombIfPresence(playerCards);
        }
        return playCards;
    }


    public static void removeJokerBomb(List<Card> cards) {
        if (cards.contains(BLACK_JOKER) && cards.contains(RED_JOKER)) {
            cards.remove(BLACK_JOKER);
            cards.remove(RED_JOKER);
        }
    }

    public static void removeBomb(List<Card> cards) {
        List<Integer> bombCount = getBombPoints(cards);
        cards.removeIf(card -> bombCount.contains(CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME)));
    }

    private static List<Card> getSingleCards(List<Card> playerCards) {
        Map<Integer, List<Card>> groupCards = CardRulerUtil.groupCardsByPoints(playerCards,LANDLORD_GAME);
        return groupCards.values().stream().filter(cards -> cards.size() == 1).map(cards -> cards.get(0))
                .sorted(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME)).collect(Collectors.toList());
    }

    public static Set<Integer> getGreaterThanOtherStraight(List<Integer> otherStraight, List<Integer> currentStraight) {
        otherStraight.sort(Integer::compareTo);
        Integer minCardPoint = otherStraight.get(0);
        return getAfterMinPointStraightFromCardPoints(currentStraight, otherStraight.size(), minCardPoint);
    }

    public static List<Card> getCardsFromPlayerCardsByPoints(List<Card> playerCards, Set<Integer> cardPoint, int cardSize) {
        List<Card> cards = playerCards.stream().filter(card -> cardPoint.contains(CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME))).collect(Collectors.toList());
        Map<Integer, List<Card>> typeCards = CardRulerUtil.groupCardsByPoints(cards,LANDLORD_GAME);
        cards.clear();
        for (List<Card> groupCards : typeCards.values()) {
            if (groupCards.size() < cardSize) return new ArrayList<>();
            for (int i = 0; i < cardSize; i++) {
                cards.add(groupCards.get(i));
            }
        }
        return cards;
    }


    public static List<Card> getMinCards(List<Card> cards, int cardSize) {
        List<Card> resultCards = new ArrayList<>();
        List<Card> singleCards = getSingleCards(cards);
        for (int i = singleCards.size() - 1; i >= 0; i--) {
            resultCards.add(singleCards.get(i));
            if (resultCards.size() == cardSize) return resultCards;
        }
        List<Integer> pair = getPairPoints(cards);
        for (Integer pairPoints : pair) {
            for (Card card : getCardsFromPlayerCardsByPoints(cards, Set.of(pairPoints), (int) POKER_PAIR_CARD_SIZE)) {
                resultCards.add(card);
                if (resultCards.size() == cardSize) return resultCards;
            }
        }
        List<Integer> planes = getPlaneCardPoint(cards);
        for (Integer plane : planes) {
            for (Card card : getCardsFromPlayerCardsByPoints(cards, Set.of(plane), POKER_THREE_SIZE)) {
                resultCards.add(card);
                if (resultCards.size() == cardSize) return resultCards;
            }
        }
        return new ArrayList<>();
    }

    public static List<Card> getPairFromPairOrPlane(List<Card> cards) {
        List<Integer> pair = getPairPoints(cards);
        if (!pair.isEmpty()) return getCardsFromPlayerCardsByPoints(cards, Set.of(pair.get(0)), 2);
        List<Integer> plane = getPlaneCardPoint(cards);
        if (!plane.isEmpty()) return getCardsFromPlayerCardsByPoints(cards, Set.of(plane.get(0)), 2);
        return new ArrayList<>();
    }

    public static List<Card> getPairCards(List<Card> cards, int pairSize) {
        List<Card> resultCards = new ArrayList<>();
        List<Integer> pairs = getPairPoints(cards);
        int addPairSize = 0;
        for (Integer pair : pairs) {
            addPairSize++;
            resultCards.addAll(getCardsFromPlayerCardsByPoints(cards, Set.of(pair), 2));
            if (addPairSize == pairSize) return resultCards;
        }
        List<Integer> planes = getPlaneCardPoint(cards);
        for (Integer plane : planes) {
            addPairSize++;
            resultCards.addAll(getCardsFromPlayerCardsByPoints(cards, Set.of(plane), 2));
            if (addPairSize == pairSize) return resultCards;
        }
        return new ArrayList<>();
    }

    public static List<Card> playSingleCard(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        removeJokerBomb(tempCards);
        int targetSingleCardPoint = CardRulerUtil.getCardPointByGameType(prevCards.get(0),LANDLORD_GAME);
        List<Card> singleCards = getSingleCards(tempCards);
        Card singleCard = singleCards.stream().filter(card -> CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME) > targetSingleCardPoint)
                .min(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME)).orElse(null);
        if(singleCard == null){
            singleCard = tempCards.stream().filter(card -> CardRulerUtil.getCardPointByGameType(card, LANDLORD_GAME) > targetSingleCardPoint)
                    .min(ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME).reversed()).orElse(null);
        }
        return singleCard == null ? new ArrayList<>(0) : Lists.newArrayList(singleCard);
    }

    public static List<Card> playSerialPairCard(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        removeJokerBomb(tempCards);
        removeBomb(tempCards);
        List<Integer> prevPair = getPairPoints(prevCards);
        List<Integer> playerPair = getPairPoints(tempCards);
        Set<Integer> straight = getGreaterThanOtherStraight(prevPair, playerPair);
        if (straight.isEmpty()) {
            List<Integer> planeCards = getPlaneCardPoint(tempCards);
            playerPair.addAll(planeCards);
            straight = getGreaterThanOtherStraight(prevPair, playerPair);
        }
        return getCardsFromPlayerCardsByPoints(playerCards, straight, 2);
    }

    public static List<Card> playPair(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        removeBomb(tempCards);
        Integer prevPairPoint = getPairPoints(prevCards).get(0);
        List<Integer> pairs = getPairPoints(playerCards);
        Integer chooseCardPoint = pairs.stream().filter(pair -> pair > prevPairPoint).findFirst().orElse(0);
        if (chooseCardPoint > 0) {
            return getCardsFromPlayerCardsByPoints(tempCards, Sets.newHashSet(chooseCardPoint), 2);
        }
        List<Integer> planeCardValues = getPlaneCardPoint(playerCards);
        chooseCardPoint = planeCardValues.stream().filter(threePair -> threePair > prevPairPoint).findFirst().orElse(0);
        if (chooseCardPoint > 0) {
            return getCardsFromPlayerCardsByPoints(tempCards, Sets.newHashSet(chooseCardPoint), 2);
        }
        return new ArrayList<>();
    }

    public static List<Card> playPokerThree(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        removeBomb(tempCards);
        if (tempCards.size() < prevCards.size()) return new ArrayList<>();
        Integer compareCardValue = getPlaneCardPoint(prevCards).get(0);
        Integer pokerThreePoint = getPlaneCardPoint(tempCards).stream().filter(cardValue -> (cardValue > compareCardValue)).findFirst().orElse(0);
        if (pokerThreePoint <= 0) return new ArrayList<>();
        int attachCardsSize = prevCards.size() - POKER_THREE_SIZE;
        List<Card> planeCards = getCardsFromPlayerCardsByPoints(tempCards, Set.of(pokerThreePoint), POKER_THREE_SIZE);
        tempCards.removeAll(planeCards);
        switch (attachCardsSize) {
            case 0:
                return planeCards;
            case 1:
                List<Card> minCards = getMinCards(tempCards, 1);
                if (minCards.isEmpty())
                    return new ArrayList<>();
                planeCards.addAll(minCards);
                break;
            case 2:
                List<Card> pair = getPairFromPairOrPlane(tempCards);
                if (pair.isEmpty())
                    return new ArrayList<>();
                planeCards.addAll(pair);
                break;
        }
        return planeCards;
    }

    public static List<Card> playPlane(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        removeBomb(tempCards);
        List<Integer> targetPlane = getPlaneCardPoint(prevCards);
        List<Integer> playerPlane = getPlaneCardPoint(tempCards);
        Set<Integer> straight = getGreaterThanOtherStraight(targetPlane, playerPlane);
        if (straight.isEmpty()) return new ArrayList<>();
        List<Card> playerPlaneCards = getCardsFromPlayerCardsByPoints(tempCards, straight, 3);
        tempCards.removeAll(playerPlaneCards);

        long attachCardsSize = prevCards.size() - targetPlane.size() * POKER_THREE_SIZE;
        List<Card> attachCards;
        if (attachCardsSize == targetPlane.size()) {
            attachCards = getMinCards(tempCards, (int) attachCardsSize);
        } else {
            attachCards = getPairCards(tempCards, (int) attachCardsSize);
        }
        if (attachCards.isEmpty()) {
            return new ArrayList<>();
        }
        playerPlaneCards.addAll(attachCards);
        return playerPlaneCards;
    }

    public static List<Card> playStraight(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        removeBomb(tempCards);
        List<Integer> targetCardPoints = prevCards.stream().map(card -> CardRulerUtil.getCardPointByGameType(card,LANDLORD_GAME)).collect(Collectors.toList());
        List<Integer> userCards = Lists.newArrayList(groupCardCount(playerCards).keySet());
        userCards.sort(Integer::compareTo);
        Set<Integer> straight = getGreaterThanOtherStraight(targetCardPoints, userCards);
        if (straight.isEmpty()) return new ArrayList<>();
        return getCardsFromPlayerCardsByPoints(tempCards, straight, 1);
    }

    public static List<Card> playFourAndTwo(List<Card> prevCards, List<Card> playerCards) {
        List<Card> tempCards = new ArrayList<>(playerCards);
        List<Card> resultCards = new ArrayList<>(prevCards.size());
        removeJokerBomb(tempCards);
        int targetMaxBomb = getBombPoints(prevCards).stream().max(Integer::compareTo).orElse(0);
        Integer userBomb = getBombPoints(playerCards).stream().filter(bombPoint -> bombPoint > targetMaxBomb).min(Integer::compareTo).orElse(0);
        if (userBomb <= 0) return new ArrayList<>();
        resultCards.addAll( getCardsFromPlayerCardsByPoints(tempCards, Set.of(userBomb), 4));
        tempCards.removeAll(resultCards);
        int attachCardsSize = prevCards.size() - BOOM_CARDSIZE;
        List<Card> attachCards = new ArrayList<>();
        switch (attachCardsSize) {
            case 2:
                attachCards = getMinCards(tempCards, attachCardsSize);
                break;
            case 4:
                attachCards = getPairCards(tempCards, 2);
                break;
        }
        if(attachCards.isEmpty()) return new ArrayList<>();
        resultCards.addAll(attachCards);
        return resultCards;
    }

    public static List<Card> playBomb(List<Card> prevCards, List<Card> playerCards) {
        Integer prevBomb = getBombPoints(prevCards).get(0);
        Integer userBomb = getBombPoints(playerCards).stream().sorted(Integer::compareTo).filter(bombPoint -> bombPoint > prevBomb).findFirst().orElse(0);
        if (userBomb == 0) return new ArrayList<>();
        return getCardsFromPlayerCardsByPoints(playerCards, Set.of(userBomb), BOOM_CARDSIZE);
    }

    public static List<Card> playMinBomb(List<Card> playerCards) {
        Integer userBomb = getBombPoints(playerCards).stream().min(Integer::compareTo).orElse(0);
        return getCardsFromPlayerCardsByPoints(playerCards, Set.of(userBomb), BOOM_CARDSIZE);
    }

    public static List<Card> playJokerBombIfPresence(List<Card> playerCards) {
        Card blackJoker = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card redJoker = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        if (playerCards.contains(blackJoker) && playerCards.contains(redJoker)) {
            return Lists.newArrayList(blackJoker, redJoker);
        }
        return new ArrayList<>(0);
    }

    public static List<Card> playMinCardCombination(List<Card> cards) {
        ArrayList<Card> tempCards = Lists.newArrayList(cards);
        removeJokerBomb(tempCards);
        List<Card> singleCards = getSingleCards(tempCards);
        if (!singleCards.isEmpty()) return Lists.newArrayList(singleCards.get(singleCards.size() - 1));
        List<Integer> pairPoints = getPairPoints(cards);
        if (!pairPoints.isEmpty()) return getCardsFromPlayerCardsByPoints(cards, Set.of(pairPoints.get(0)), (int) POKER_PAIR_CARD_SIZE);
        List<Integer> planeCardPoint = getPlaneCardPoint(cards);
        if (!planeCardPoint.isEmpty()) return getCardsFromPlayerCardsByPoints(cards, Set.of(planeCardPoint.get(0)), POKER_THREE_SIZE);
        List<Card> bomb = playMinBomb(cards);
        if (!bomb.isEmpty()) return bomb;
        Card blackJoker = new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker);
        Card redJoker = new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker);
        List<Card> jokerBomb = Lists.newArrayList(redJoker, blackJoker);
        if (cards.containsAll(jokerBomb)) return jokerBomb;
        return CardRulerUtil.getMinCardsFromCardList(cards, ComparatorUtil.getCardComparatorForGameType(LANDLORD_GAME));
    }
}
