package com.donglaistd.jinli.database.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck implements Serializable {
    public final List<Card> cards = new ArrayList<>();
    private final List<Card> dealtCards = new ArrayList<>();

    public void addCard(Card card) {
        cards.add(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card deal() {
        Card card = cards.remove(0);
        dealtCards.add(card);
        return card;
    }

    public List<Card> dealMultipleCards(int cardSize) {
        List<Card> cardList = new ArrayList<>();
        if (cardSize <= cards.size()) {
            for (int i = 0; i < cardSize; i++) {
                cardList.add(deal());
            }
        }
        return cardList;
    }

    public void addDeck(Deck deck) {
        cards.addAll(deck.cards);
    }

    public int getLeftCardCount() {
        return cards.size();
    }

    public int getDealtCardCount() {
        return dealtCards.size();
    }


    public List<Card> getDealtCards() {
        return dealtCards;
    }

    public void reset() {
        cards.addAll(dealtCards);
        dealtCards.clear();
    }
}