package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.game.landlord.Landlords;

public class LandlordsEndEvent implements BaseEvent {
    public Landlords landlords;

    public LandlordsEndEvent(Landlords landlords) {
        this.landlords = landlords;
    }
}
