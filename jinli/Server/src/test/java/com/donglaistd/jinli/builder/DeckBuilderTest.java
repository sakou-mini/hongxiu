package com.donglaistd.jinli.builder;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;

public class DeckBuilderTest {
    @Test
    public void OneDeckTest() {
        var deck = DeckBuilder.getOneNoJokerDeck();
        Assert.assertEquals(52, deck.getLeftCardCount());
        Assert.assertEquals(0, deck.getDealtCardCount());
        var cardSet = new HashSet<>();
        for (int i = 0; i < 52; i++) {
            cardSet.add(deck.deal());
        }
        Assert.assertEquals(0, deck.getLeftCardCount());
        Assert.assertEquals(52, deck.getDealtCardCount());
        Assert.assertEquals(52, cardSet.size());
        deck.reset();
        Assert.assertEquals(52, deck.getLeftCardCount());
        Assert.assertEquals(0, deck.getDealtCardCount());
    }

    @Test
    public void MultiDeckTest() {
        var deck = DeckBuilder.getNoJokerDeck(8);
        Assert.assertEquals(416, deck.getLeftCardCount());
        Assert.assertEquals(0, deck.getDealtCardCount());
        var cardSet = new HashSet<>();
        for (int i = 0; i < 416; i++) {
            cardSet.add(deck.deal());
        }
        Assert.assertEquals(0, deck.getLeftCardCount());
        Assert.assertEquals(416, deck.getDealtCardCount());
        Assert.assertEquals(52, cardSet.size());
    }
}
