package com.donglaistd.jinli.event;

import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.google.protobuf.Message;

import java.util.List;
import java.util.Map;

public class GameFinishEvent implements BaseEvent {
    private final BaseGame game;
    private final Message message;
    private final Map<User, Long> userBetAmount;
    private final List<Game.BetType> gameResult;

    public BaseGame getGame() {
        return game;
    }

    public Message getMessage() {
        return message;
    }

    public Map<User, Long> getUserBetAmount() {
        return userBetAmount;
    }

    public GameFinishEvent(BaseGame game, Message message, Map<User, Long> userBetAmount, List<Game.BetType> gameResult) {
        this.game = game;
        this.message = message;
        this.userBetAmount = userBetAmount;
        this.gameResult = gameResult;
    }

    public List<Game.BetType> getGameResult() {
        return gameResult;
    }

}
