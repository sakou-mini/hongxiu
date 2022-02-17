package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.GameStatus.IDLE;
import static com.donglaistd.jinli.Constant.GameStatus.PAUSED;
import static com.donglaistd.jinli.Constant.ResultCode.*;


@Component
public class CancelPauseGameRequestHandler extends GameHandler{
    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var user = dataManager.getUserFromChannel(ctx);
        var reply = Game.GameReply.newBuilder();
        var cancelPauseGameReply = Game.CancelPauseGameReply.newBuilder();
        Constant.GameStatus gameStatus = game.getGameStatus();
        if (!game.getGameId().equals(dataManager.findLiveUser(user.getLiveUserId()).getPlayingGameId())) {
            return generateReply(reply, cancelPauseGameReply, NOT_GAME_OWNER);
        }else if(gameStatus.equals(PAUSED) ){
            return generateReply(reply, cancelPauseGameReply, GAME_ALREADY_PAUSE);
        }else if(game.getNextGameStatue().equals(PAUSED)){
            game.setNextGameState(IDLE);
            game.setAboutToEnd(false);
            return generateReply(reply, cancelPauseGameReply, SUCCESS);
        }else{
            return generateReply(reply, cancelPauseGameReply, GAME_STATUS_ERROR);
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.CancelPauseGameReply.Builder cancelGameReply, Constant.ResultCode resultCode) {
        reply.setCancelPauseGameReply(cancelGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
