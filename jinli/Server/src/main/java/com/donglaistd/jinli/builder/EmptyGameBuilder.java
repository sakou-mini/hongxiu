package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import org.springframework.stereotype.Component;

@Component
public class EmptyGameBuilder {

    public EmptyGame create(Constant.GameType gameType) {
        return new EmptyGame(gameType);
    }
}
