package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_GAME_OWNER;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;


@Component
public class EndGameRequestHandler extends GameHandler {
    private static final Logger logger = Logger.getLogger(EndGameRequestHandler.class.getName());

    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var user = dataManager.getUserFromChannel(ctx);
        var reply = Game.GameReply.newBuilder();
        var endGameReply = Game.EndGameReply.newBuilder();

        if (!game.getGameId().equals(dataManager.findLiveUser(user.getLiveUserId()).getPlayingGameId())) {
            logger.info("you are not owner");
            return generateReply(reply, endGameReply, NOT_GAME_OWNER);
        }
        game.endGame();
        return generateReply(reply, endGameReply, SUCCESS);
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.EndGameReply.Builder endGameReply,
                                                                  Constant.ResultCode resultCode) {
        reply.setEndGameReply(endGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
