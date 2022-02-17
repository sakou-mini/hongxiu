package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class MuteAllConnectLiveRequestHandler extends RoomManagementHandler {
    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.MuteAllConnectLiveReply.Builder builder = RoomManagement.MuteAllConnectLiveReply.newBuilder();
        if (!room.validateIsLiveUser(user.getLiveUserId()) && !room.getAdministrators().contains(user.getId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }
        if (room.getMuteAll()) {
            return generateReply(builder, ILLEGAL_OPERATION);
        }
        room.setMuteAll(true);
        if (!StringUtils.isNullOrBlank(room.getCurrentConnectLiveUserId())) {
            room.cleanCurrentConnectLiveUserId();
        }
        room.clearConnectLiveCode();
        room.broadCastToAllPlatform(buildReply(Jinli.MuteAllBroadcastMessage.newBuilder().setMuteAll(room.getMuteAll()).build()));
        return generateReply(builder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.MuteAllConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setMuteAllConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
