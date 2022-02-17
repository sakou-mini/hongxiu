package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.GameEndEvent;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@Scope("prototype")
public class SwitchGameListener implements EventListener {
    private static final Logger logger = Logger.getLogger(SwitchGameListener.class.getName());

    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    DataManager dataManager;

    private Constant.GameType newGameType;
    private BaseGame expectingGame;
    private boolean openBanker;

    public void setNewGameType(Constant.GameType newGameType) {
        this.newGameType = newGameType;
    }

    public void setExpectingGame(BaseGame expectingGame) {
        this.expectingGame = expectingGame;
    }

    public void setOpenBanker(boolean openBanker) {
        this.openBanker = openBanker;
    }

    @Override
    public boolean handle(BaseEvent event) {
        var e = (GameEndEvent) event;
        var game = e.getGame();
        if (game != expectingGame) {
            logger.info("not expecting switch game:" + game.getGameStatus() + ", expecting game id:" + expectingGame.getGameId() + ", actual game id:" + game.getGameId());
            logger.info("games left:" + DataManager.gameMap.size());
            return false;
        }
        if (!game.isStop()) {
            logger.warning("switching game status error:" + game.getGameStatus());
            return true;
        }
        logger.info("switching game from:" + expectingGame.getGameType() + " id <" + expectingGame.getGameId() + "> to:" + newGameType);
        DataManager.removeGame(game.getGameId());
        LiveUser liveUser = game.getOwner();
        var newGame = gameBuilder.createGame(newGameType, liveUser, openBanker);
        DataManager.addGame(newGame);
        newGame.beginGameLoop(game.getBettingTime());
        logger.info("putting game: <" + newGame.getGameId() + "> into memory map");
        DataManager.roomMap.get(liveUser.getRoomId()).broadCastToAllPlatform(Jinli.JinliMessageReply.newBuilder()
                .setSwitchGameBroadcast(Jinli.SwitchGameBroadcast.newBuilder().setGameId(newGame.getGameId()).setGameType(newGameType)).build());
        return true;
    }

    @Override
    public boolean isDisposable() {
        return true;
    }
}
