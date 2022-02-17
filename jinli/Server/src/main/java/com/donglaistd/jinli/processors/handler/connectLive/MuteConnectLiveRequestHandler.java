package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.service.RoomProcess.getMuteIdentify;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class MuteConnectLiveRequestHandler extends RoomManagementHandler {
    @Autowired
    private BlacklistDaoService blacklistDaoService;
    @Autowired
    DataManager dataManager;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        var request = messageRequest.getMuteConnectLiveRequest();
        var replyBuilder = RoomManagement.MuteConnectLiveReply.newBuilder();
        String userId = request.getUserId();
        User optUser = dataManager.findUser(userId);
        if(Objects.equals(request.getUserId(),user.getId())) {
            return generateReply(replyBuilder, CAN_NOT_MUTE_SELF);
        }
        else if (Objects.nonNull(room.getConnectLiveCodeByUserId(userId))) {
            room.removeConnectLiveCodeByUser(optUser);
        }
        Blacklist blacklist = Optional.ofNullable(blacklistDaoService.findByRoomId(room.getId())).orElse(Blacklist.getInstance(room.getId(), ""));
        if (blacklist.containsMute(userId)) {
            return generateReply(replyBuilder, ALREADY_IN_THE_MUTE_LIST);
        }
        blacklist.addMutes(userId);
        blacklistDaoService.save(blacklist);
        Constant.MuteIdentity muteIdentify = getMuteIdentify(user, room);
        room.broadCastToAllPlatform(buildReply(Jinli.MuteConnectLiveBroadcastMessage.newBuilder().setIsMuteLive(true).setIdentity(muteIdentify).setUserId(userId).build()));
        return generateReply(replyBuilder, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.MuteConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setMuteConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }

}
