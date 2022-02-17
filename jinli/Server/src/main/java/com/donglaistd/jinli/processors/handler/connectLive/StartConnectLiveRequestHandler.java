package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

@Component
public class StartConnectLiveRequestHandler extends RoomManagementHandler {
    @Autowired
    DataManager dataManager;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user,
                                                                                Room room) {
        RoomManagement.StartConnectLiveReply.Builder builder = RoomManagement.StartConnectLiveReply.newBuilder();
        RoomManagement.StartConnectLiveRequest request = messageRequest.getStartConnectLiveRequest();
        String userId = request.getUserId();
        if (Objects.equals(userId, user.getId())) {
            return generateReply(builder, ILLEGAL_OPERATION);
        }
        if (!room.validateIsLiveUser(user.getLiveUserId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }
        User connectUser = dataManager.findOnlineUser(userId);
        if (Objects.isNull(connectUser)) {
            return generateReply(builder, USER_NOT_FOUND);
        }
        if (!room.getAllPlatformAudienceList().contains(userId)) {
            return generateReply(builder, HAS_LEFT_THE_ROOM);
        }
        if (!room.hasConnectLive(userId)) {
            return generateReply(builder, NOT_IN_THE_CONNECT_LIST);
        }
        room.setCurrentConnectLiveUserId(userId);
        String codeByUserId = room.getConnectLiveCodeByUserId(userId).getRight();
        sendMessage(userId, buildReply(Jinli.ConnectLiveBroadcastMessage.newBuilder().setAudioCode(codeByUserId).build()));
        room.broadCastToAllPlatform(buildReply(Jinli.StartConnectBroadcastMessage.newBuilder().setUserId(userId)));
        EventPublisher.publish(TaskEvent.newInstance(userId, ConditionType.coopLive,1));
        return generateReply(builder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.StartConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setStartConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
