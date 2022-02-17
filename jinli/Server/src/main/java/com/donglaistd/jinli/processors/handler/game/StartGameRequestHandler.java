package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;


@Component
public class StartGameRequestHandler extends GameHandler {
    private static final Logger logger = Logger.getLogger(StartGameRequestHandler.class.getName());

    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var user = dataManager.getUserFromChannel(ctx);
        var reply = Game.GameReply.newBuilder();
        var startGameReply = Game.StartGameReply.newBuilder();

        if (!game.getOwner().getId().equals(user.getLiveUserId())) {
            return generateReply(reply, startGameReply, NOT_GAME_OWNER);
        }
        if(game instanceof EmptyGame){
            return generateReply(reply, startGameReply, CANNOT_START_EMPTY_GAME);
        }
        if (game.getGameStatus().equals(Constant.GameStatus.PAUSED)) {
            game.beginGameLoop(game.getBettingTime());
            return generateReply(reply, startGameReply, SUCCESS);
        } else {
            logger.warning("game statue is:"+game.getGameStatus() + "gameType is "+game.getGameType());
            return generateReply(reply, startGameReply, GAME_STATUS_ERROR);
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.StartGameReply.Builder startGameReply, Constant.ResultCode resultCode) {
        reply.setStartGameReply(startGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
