package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.game.BaseGame;

public class GameEndEvent implements BaseEvent {
    private final BaseGame game;

    public BaseGame getGame() {
        return game;
    }

    public GameEndEvent(BaseGame game) {
        this.game = game;
    }
}
