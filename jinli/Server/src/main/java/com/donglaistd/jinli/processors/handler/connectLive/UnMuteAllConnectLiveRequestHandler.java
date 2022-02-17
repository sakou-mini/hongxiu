package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UnMuteAllConnectLiveRequestHandler extends RoomManagementHandler {
    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.UnMuteAllConnectLiveReply.Builder builder = RoomManagement.UnMuteAllConnectLiveReply.newBuilder();
        if (!room.validateIsLiveUser(user.getLiveUserId()) && !room.getAdministrators().contains(user.getId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }
        if (!room.getMuteAll()) {
            return generateReply(builder, ILLEGAL_OPERATION);
        }
        room.setMuteAll(false);
        room.broadCastToAllPlatform(buildReply(Jinli.MuteAllBroadcastMessage.newBuilder().setMuteAll(room.getMuteAll()).build()));
        return generateReply(builder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.UnMuteAllConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setUnMuteAllConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
