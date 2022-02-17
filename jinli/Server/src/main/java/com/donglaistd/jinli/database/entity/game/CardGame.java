package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Deck;
import com.donglaistd.jinli.util.FutureTaskWeakSet;
import org.springframework.data.annotation.Transient;

import java.util.logging.Logger;

public abstract class CardGame {
    @Transient
    public static final Logger logger = Logger.getLogger(RaceGame.class.getName());

    @Transient
    protected Deck deck;

    @Transient
    protected FutureTaskWeakSet futureTaskWeakSet = new FutureTaskWeakSet();

    @Transient
    protected long gameStartTime;

    public Deck getDeck() {
        return deck;
    }

    public void setDeck(Deck deck) {
        this.deck = deck;
    }

    public long getGameStartTime() {
        return gameStartTime;
    }

    public abstract  String getGameId();

    public abstract Constant.GameType getGameType();

    public abstract void startGame();

    public abstract void endGame();

}
