package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.race.TexasRace;

public class AddTexasEvent implements BaseEvent {
    private final TexasRace race;

    public AddTexasEvent(TexasRace race) {
        this.race = race;
    }

    public TexasRace getRace() {
        return race;
    }
}
