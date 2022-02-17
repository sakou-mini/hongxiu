package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.game.BaseGame;

public class LiveRecordEvent implements BaseEvent {
    Room room;
    BaseGame baseGame;

    public LiveRecordEvent(Room room) {
        this.room = room;
    }

    public LiveRecordEvent(Room room, BaseGame baseGame) {
        this.room = room;
        this.baseGame = baseGame;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public BaseGame getBaseGame() {
        return baseGame;
    }

    public void setBaseGame(BaseGame baseGame) {
        this.baseGame = baseGame;
    }
}
