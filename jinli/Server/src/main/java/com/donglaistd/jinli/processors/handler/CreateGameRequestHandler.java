package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.exception.JinliException;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreShutDown
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class CreateGameRequestHandler extends MessageHandler {
    @Autowired
    GameBuilder gameBuilder;

    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getCreateGameRequest();
        var reply = Jinli.CreateGameReply.newBuilder();
        var liveUser = dataManager.findLiveUser(user.getLiveUserId());
        if (liveUser == null || liveUser.getRoomId() == null) {
            return buildReply(reply, NOT_LIVE_USER);
        }
        if (liveUser.getPlayingGameId() != null && DataManager.findGame(liveUser.getPlayingGameId())!=null) {
            reply.setGameId(liveUser.getPlayingGameId());
            return buildReply(reply, ALREADY_HAVE_GAME);
        }
        try {
            var game = gameBuilder.createGame(request.getGameType(), liveUser,request.getIsBankerGame());
            DataManager.addGame(game);
            reply.setGameId(game.getGameId());
        } catch (JinliException e) {
            return buildReply(reply, e.getResultCode());
        }
        return buildReply(reply, SUCCESS);
    }

}
