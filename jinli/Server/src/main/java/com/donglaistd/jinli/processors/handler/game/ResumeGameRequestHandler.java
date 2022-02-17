package com.donglaistd.jinli.processors.handler.game;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Game;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class ResumeGameRequestHandler extends GameHandler {
    private static final Logger logger = Logger.getLogger(ResumeGameRequestHandler.class.getName());

    @Autowired
    DataManager dataManager;

    @Override
    public Pair<Game.GameReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, Game.GameRequest messageRequest, BaseGame game) {
        var user = dataManager.getUserFromChannel(ctx);
        var reply = Game.GameReply.newBuilder();
        var resumeGameReply = Game.ResumeGameReply.newBuilder();
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (!game.getGameId().equals(liveUser.getPlayingGameId())) {
            return generateReply(reply, resumeGameReply, NOT_GAME_OWNER);
        } else if (game.getGameStatus().equals(Constant.GameStatus.PAUSED)) {
            logger.fine("Resume Game");
            game.resumeGame();
            Jinli.ResumeGameBroadcastMessage.Builder builder = Jinli.ResumeGameBroadcastMessage.newBuilder().setRoomId(liveUser.getRoomId());
            DataManager.roomMap.get(liveUser.getRoomId()).broadCastToAllPlatform(buildReply(builder));
            return generateReply(reply, resumeGameReply, SUCCESS);
        } else {
            return generateReply(reply, resumeGameReply, GAME_STATUS_ERROR);
        }
    }

    private Pair<Game.GameReply, Constant.ResultCode> generateReply(Game.GameReply.Builder reply, Game.ResumeGameReply.Builder resumeGameReply, Constant.ResultCode resultCode) {
        reply.setResumeGameReply(resumeGameReply);
        return new Pair<>(reply.build(), resultCode);
    }
}
