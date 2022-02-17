package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;

@Component
public class RemoveConnectLiveRequestHandler extends RoomManagementHandler {
    @Autowired
    DataManager dataManager;
    @Autowired
    UserDaoService userDaoService;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.RemoveConnectLiveRequest request = messageRequest.getRemoveConnectLiveRequest();
        RoomManagement.RemoveConnectLiveReply.Builder builder = RoomManagement.RemoveConnectLiveReply.newBuilder();
        //if is liveUser ,remove Connect currentLive
        String userId = StringUtils.isNullOrBlank(request.getUserId()) ? user.getId() : request.getUserId();
        if(room.validateIsLiveUser(user.getLiveUserId()) && !StringUtils.isNullOrBlank(room.getCurrentConnectLiveUserId())){
            userId = room.getCurrentConnectLiveUserId();
        }
        User removeUser = userDaoService.findById(userId);
        if (Objects.isNull(removeUser)) {
            return generateReply(builder, USER_NOT_FOUND);
        }
        if (!room.hasConnectLive(userId)) {
            return generateReply(builder, NOT_IN_THE_CONNECT_LIST);
        }
        room.removeConnectLiveCodeByUser(removeUser);
        return generateReply(builder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.RemoveConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setRemoveConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
