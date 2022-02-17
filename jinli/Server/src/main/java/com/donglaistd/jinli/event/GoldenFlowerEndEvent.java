package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.game.goldenflower.FriedGoldenFlower;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;

import java.util.List;

public class GoldenFlowerEndEvent implements BaseEvent{
    private final List<RacePokerPlayer> players;
    private final FriedGoldenFlower goldenFlower;

    public GoldenFlowerEndEvent(List<RacePokerPlayer> players, FriedGoldenFlower goldenFlower) {
        this.players = players;
        this.goldenFlower = goldenFlower;
    }

    public List<RacePokerPlayer> getPlayers() {
        return players;
    }

    public FriedGoldenFlower getGoldenFlower() {
        return goldenFlower;
    }
}
