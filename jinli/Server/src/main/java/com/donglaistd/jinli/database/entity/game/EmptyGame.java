package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document
public class EmptyGame extends BaseGame {
    @Field
    private final ObjectId id = ObjectId.get();
    @Field
    private Constant.GameType gameType;

    public EmptyGame() {
    }

    public EmptyGame(Constant.GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public String getGameId() {
        return id.toString();
    }

    @Override
    protected boolean checkGameBetLimit(User user, long betAmount, Game.BetType betType) {
        return false;
    }

    @Override
    protected int getMinimalDeckCardRequest() {
        return 0;
    }

    public void setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public Constant.GameType getGameType() {
        return gameType;
    }

    @Override
    protected List<Game.BetType> getGameResult() {
        return null;
    }

    @Override
    protected void dealCards() {

    }

    @Override
    protected void processGameResult(List<Game.BetType> gameResults) {

    }

    @Override
    public List<? extends Jinli.GameResult> getCardHistory() {
        return null;
    }
}
