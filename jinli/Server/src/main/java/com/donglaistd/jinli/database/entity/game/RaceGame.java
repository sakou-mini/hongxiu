package com.donglaistd.jinli.database.entity.game;

import org.springframework.data.annotation.Transient;

import java.io.Serializable;

public abstract class RaceGame extends CardGame implements Serializable {
    @Transient
    protected String raceId;

    public String getRaceId() {
        return raceId;
    }

    public void setRaceId(String raceId) {
        this.raceId = raceId;
    }
}
