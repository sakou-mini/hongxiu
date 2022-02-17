package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class RevokeAdministratorRequestHandler extends RoomManagementHandler {
    @Autowired
    private DataManager dataManager;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.RevokeAdministratorReply.Builder builder = RoomManagement.RevokeAdministratorReply.newBuilder();
        if (!room.validateIsLiveUser(user.getLiveUserId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }
        RoomManagement.RevokeAdministratorRequest request = messageRequest.getRevokeAdministratorRequest();
        String userId = request.getUserId();
        if (!room.containsAdministrator(userId)) {
            return generateReply(builder, NOT_AN_ADMINISTRATOR);
        }
        room.revokeAdministrator(userId);
        dataManager.saveRoom(room);
        room.broadCastToAllPlatform(buildReply(Jinli.AdministratorBroadcastMessage.newBuilder().setUserId(userId).setIsAdd(false)));
        return generateReply(builder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.RevokeAdministratorReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setRevokeAdministratorReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
