package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.service.BackOfficeSocketMessageService.broadCastMuteChatMessageToHttpSocket;
import static com.donglaistd.jinli.service.RoomProcess.buildMuteChatBroadcastMessage;
import static com.donglaistd.jinli.service.RoomProcess.getMuteIdentify;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class MuteChatRequestHandler extends RoomManagementHandler {
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private BlacklistDaoService blacklistDaoService;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.MuteChatRequest request = messageRequest.getMuteChatRequest();
        RoomManagement.MuteChatReply.Builder builder = RoomManagement.MuteChatReply.newBuilder();
        if (!room.validateIsLiveUser(user.getLiveUserId()) && !room.containsAdministrator(user.getId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }else if(Objects.equals(request.getUserId(),user.getId())){
            return generateReply(builder, CAN_NOT_MUTE_SELF);
        }else {
            String userId = request.getUserId();
            User muteUser = userDaoService.findById(userId);
            if (Objects.isNull(muteUser)) return generateReply(builder, USER_NOT_FOUND);
            Blacklist blacklist = Optional.ofNullable(blacklistDaoService.findByRoomId(room.getId())).orElse(Blacklist.getInstance(room.getId(), ""));
            Constant.MuteIdentity muteIdentify = getMuteIdentify(user, room);
            Constant.MuteTimeType muteTime = request.getMuteTime();
            Constant.MuteReason muteReason = request.getMuteReason();
            blacklist.addMuteChat(userId,muteTime,muteReason,"",muteIdentify,user.getId(), Constant.MuteArea.LOCAL_ROOM);
            blacklistDaoService.save(blacklist);
            Jinli.MuteChatBroadcastMessage message = buildMuteChatBroadcastMessage(userId, blacklist.getMuteChatProperty(userId));
            room.broadCastToAllPlatform(buildReply(message));
            broadCastMuteChatMessageToHttpSocket(room.getId());
            return generateReply(builder, SUCCESS);
        }
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.MuteChatReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setMuteChatReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }

}
