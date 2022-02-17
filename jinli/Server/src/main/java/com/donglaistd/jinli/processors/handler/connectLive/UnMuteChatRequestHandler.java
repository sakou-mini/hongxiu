package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.service.BackOfficeSocketMessageService.broadCastMuteChatMessageToHttpSocket;
import static com.donglaistd.jinli.service.RoomProcess.buildUnMuteChatBroadcastMessage;
import static com.donglaistd.jinli.service.RoomProcess.getMuteIdentify;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UnMuteChatRequestHandler extends RoomManagementHandler {
    @Autowired
    private BlacklistDaoService blacklistDaoService;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.UnMuteChatReply.Builder builder = RoomManagement.UnMuteChatReply.newBuilder();
        if (!room.validateIsLiveUser(user.getLiveUserId()) && !room.getAdministrators().contains(user.getId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }
        RoomManagement.UnMuteChatRequest request = messageRequest.getUnMuteChatRequest();
        String userId = request.getUserId();
        Constant.MuteIdentity muteIdentify = getMuteIdentify(user, room);
        if(removeUserToBlackList(room.getId(),userId,muteIdentify)){
            return generateReply(builder, SUCCESS);
        } else {
            return generateReply(builder, NOT_IN_THE_MUTE_LIST);
        }

    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.UnMuteChatReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setUnMuteChatReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }

    public boolean removeUserToBlackList(String roomId,String userId,Constant.MuteIdentity identity) {
        var blacklist = blacklistDaoService.findByRoomId(roomId);
        if(Objects.isNull(blacklist)) {
            return false;
        }
        blacklist.removeMuteChat(userId);
        blacklistDaoService.save(blacklist);
        Room room = DataManager.findOnlineRoom(roomId);
        if(Objects.nonNull(room)) {
            var message = buildUnMuteChatBroadcastMessage(userId, identity);
            room.broadCastToAllPlatform(buildReply(message));
            broadCastMuteChatMessageToHttpSocket(room.getId());
        }
        return true;
    }
}
