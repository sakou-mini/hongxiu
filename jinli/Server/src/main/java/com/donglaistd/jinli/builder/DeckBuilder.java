package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Card;
import com.donglaistd.jinli.database.entity.Deck;

public class DeckBuilder {
    public static Deck getNoJokerDeck(int deckCount) {
        var deck = new Deck();
        while (deckCount > 0) {
            deck.addDeck(getOriginalDeck());
            deckCount--;
        }
        deck.shuffle();
        return deck;
    }

    public static Deck getOneNoJokerDeck() {
        var deck = getOriginalDeck();
        deck.shuffle();
        return deck;
    }

    private static Deck getOriginalDeck() {
        var deck = new Deck();
        for (var cardNumber : Constant.CardNumber.values()) {
            if (cardNumber.equals(Constant.CardNumber.UNRECOGNIZED)||cardNumber.equals(Constant.CardNumber.CARD_NUMBER_NO_SET) || cardNumber.getNumber()>=Constant.CardNumber.BlackJoker_VALUE) continue;
            for (var cardType : Constant.CardType.values()) {
                if (cardType.equals(Constant.CardType.UNRECOGNIZED)||cardType.equals(Constant.CardType.CARD_TYPE_NO_SET) || cardType.getNumber() >= Constant.CardType.Joker_VALUE) continue;
                deck.addCard(new Card(cardNumber, cardType));
            }
        }
        return deck;
    }

    public static Deck getJokerDeck(){
        var deck = getOriginalDeck();
        deck.addCard(new Card(Constant.CardNumber.BlackJoker, Constant.CardType.Joker));
        deck.addCard(new Card(Constant.CardNumber.RedJoker, Constant.CardType.Joker));
        deck.shuffle();
        return deck;
    }
}
