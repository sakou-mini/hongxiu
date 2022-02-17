package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryRoomChatHistoryRequestHandler extends MessageHandler {
    @Autowired
    UserDaoService userDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryRoomChatHistoryRequest request = messageRequest.getQueryRoomChatHistoryRequest();
        Jinli.QueryRoomChatHistoryReply.Builder replyBuilder = Jinli.QueryRoomChatHistoryReply.newBuilder();
        String roomId = request.getRoomId();
        Room room = DataManager.findOnlineRoom(roomId);
        if (Objects.isNull(room)) return buildReply(replyBuilder, ROOM_DOES_NOT_EXIST);
        if (room.notContainsUser(user)) return buildReply(replyBuilder, NO_ENTERED_ROOM);
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        if (!StringUtils.isNullOrBlank(liveUser.getPlayingGameId())) {
            CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
            if (!Objects.isNull(game) && game instanceof EmptyGame) {
                room.getRoomChatHistoryByUser(user).forEach(record -> replyBuilder.addChatBroadcastMessage(buildtChatBroadcastMessage(record.getFromUid(), record.getMsg())));
            }
        }
        return buildReply(replyBuilder, SUCCESS);
    }

    private Jinli.ChatBroadcastMessage buildtChatBroadcastMessage(String userId, String message) {
        User user = userDaoService.findById(userId);
        return Jinli.ChatBroadcastMessage.newBuilder().setLevel(user.getLevel())
                .setDisplayName(user.getDisplayName()).setVipId(user.getVipType()).setContent(message).setUserId(userId).build();
    }
}
