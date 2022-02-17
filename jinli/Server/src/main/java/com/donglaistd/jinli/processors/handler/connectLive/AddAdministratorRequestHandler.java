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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class AddAdministratorRequestHandler extends RoomManagementHandler {
    @Autowired
    private DataManager dataManager;
    @Value("${data.live-room.administrator.max_number}")
    private int MAX_NUMBER;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user,
                                                                                Room room) {
        RoomManagement.AddAdministratorReply.Builder builder = RoomManagement.AddAdministratorReply.newBuilder();
        if (!room.validateIsLiveUser(user.getLiveUserId())) {
            return generateReply(builder, INSUFFICIENT_PERMISSIONS);
        }
        String userId = messageRequest.getAddAdministratorRequest().getUserId();
        if (!room.getAllPlatformAudienceList().contains(userId)) {
            return generateReply(builder, NOT_IN_THE_ROOM);
        }
        if (room.containsAdministrator(userId)) {
            return generateReply(builder, ALREADY_AN_ADMINISTRATOR);
        }
        if (room.getAdministrators().size() >= MAX_NUMBER) {
            return generateReply(builder, THE_NUMBER_OF_ADMINISTRATORS_EXCEEDS_THE_LIMIT);
        }
        room.addAdministrator(userId);
        dataManager.saveRoom(room);
        room.broadCastToAllPlatform(buildReply(Jinli.AdministratorBroadcastMessage.newBuilder().setUserId(userId).setIsAdd(true).build()));
        return generateReply(builder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.AddAdministratorReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setAddAdministratorReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }

}
