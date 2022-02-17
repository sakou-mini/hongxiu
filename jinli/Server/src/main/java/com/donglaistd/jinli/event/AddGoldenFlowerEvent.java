package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;

public class AddGoldenFlowerEvent implements BaseEvent {
    private final GoldenFlowerRace goldenFlowerRace;

    public AddGoldenFlowerEvent(GoldenFlowerRace goldenFlowerRace) {
        this.goldenFlowerRace = goldenFlowerRace;
    }

    public GoldenFlowerRace getGoldenFlowerRace() {
        return goldenFlowerRace;
    }
}
