package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_IN_THE_MUTE_LIST;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.service.RoomProcess.getMuteIdentify;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UnMuteConnectLiveRequestHandler extends RoomManagementHandler {
    @Autowired
    private BlacklistDaoService blacklistDaoService;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.UnMuteConnectLiveReply.Builder builder = RoomManagement.UnMuteConnectLiveReply.newBuilder();
        RoomManagement.UnMuteConnectLiveRequest request = messageRequest.getUnMuteConnectLiveRequest();
        String userId = request.getUserId();
        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        if (Objects.nonNull(blacklist)) {
            Set<String> mutes = blacklist.getMutes();
            if (mutes.contains(userId)) {
                blacklist.removeMutes(userId);
                blacklistDaoService.save(blacklist);
                Constant.MuteIdentity muteIdentify = getMuteIdentify(user, room);
                room.broadCastToAllPlatform(buildReply(Jinli.MuteConnectLiveBroadcastMessage.newBuilder().setIsMuteLive(false).setUserId(userId).setIdentity(muteIdentify).build()));
                return generateReply(builder, SUCCESS);
            } else {
                return generateReply(builder, NOT_IN_THE_MUTE_LIST);
            }
        } else {
            return generateReply(builder, NOT_IN_THE_MUTE_LIST);
        }
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.UnMuteConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setUnMuteConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
