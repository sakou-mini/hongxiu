package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;


@Component
public class PauseGameRequestHandler extends GameHandler {

    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var user = dataManager.getUserFromChannel(ctx);
        var reply = Game.GameReply.newBuilder();
        var pauseGameReply = Game.PauseGameReply.newBuilder();
        if (!game.getGameId().equals(dataManager.findLiveUser(user.getLiveUserId()).getPlayingGameId())) {
            return generateReply(reply, pauseGameReply, NOT_GAME_OWNER);
        }
        if (!game.getNextGameStatue().equals(Constant.GameStatus.ENDED)) {
            game.pauseGame();
        } else {
            return generateReply(reply, pauseGameReply, GAME_SWITCHING);
        }
        return generateReply(reply, pauseGameReply, SUCCESS);
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.PauseGameReply.Builder pauseGameReply, Constant.ResultCode resultCode) {
        reply.setPauseGameReply(pauseGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
