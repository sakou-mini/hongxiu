package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.game.texas.Texas;
import com.donglaistd.jinli.database.entity.game.texas.RacePokerPlayer;

import java.util.List;

public class TexasEndEvent implements BaseEvent {
    private final List<RacePokerPlayer> players;
    private final Texas texas;
    public TexasEndEvent(List<RacePokerPlayer> players, Texas texas) {
        this.players = players;
        this.texas = texas;
    }

    public Texas getTexas() {
        return texas;
    }

    public List<RacePokerPlayer> getPlayers() {
        return players;
    }
}
