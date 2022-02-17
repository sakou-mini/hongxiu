package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.dao.statistic.record.ConnectedLiveRecordDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.record.ConnectedLiveRecord;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class ApplyConnectLiveRequestHandler extends RoomManagementHandler {
    @Autowired
    DataManager dataManager;
    @Value("${data.liveroom.wait.max_count}")
    private int MAX_WAIT_COUNT;
    @Autowired
    private BlacklistDaoService blacklistDaoService;
    @Autowired
    ConnectedLiveRecordDaoService connectedLiveRecordDaoService;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.ApplyConnectLiveReply.Builder reply = RoomManagement.ApplyConnectLiveReply.newBuilder();
        if (room.getMuteAll()) {
            return generateReply(reply, CURRENTLY_ALL_MUTED);
        }
        if (!room.getAdministrators().contains(user.getId()) && !user.canApplyConnect()) {
            return generateReply(reply, NOT_UNLOCKED_YET);
        }
        if (room.getConnectLiveCodeSize() >= MAX_WAIT_COUNT) {
            return generateReply(reply, THE_NUMBER_OF_PEOPLE_EXCEEDS_THE_LIMIT);
        }
        if (!room.getAllPlatformAudienceList().contains(user.getId())) {
            return generateReply(reply, NOT_IN_THE_ROOM);
        }
        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        if (Objects.nonNull(blacklist) && blacklist.containsMute(user.getId())) {
            return generateReply(reply, YOU_HAVE_BEEN_MUTED);
        }
        if (room.hasConnectLive(user.getId())) {
            return generateReply(reply, ALREADY_IN_THE_CONNECT_LIST);
        }
        room.addConnectLiveCode(user.getId(),user.getPlatformType());
        Jinli.AddConnectLiveBroadcastMessage.Builder builder = Jinli.AddConnectLiveBroadcastMessage.newBuilder()
                .setUserId(user.getId()).setDisplayName(user.getDisplayName()).setAvatarUrl(user.getAvatarUrl()).setLevel(user.getLevel());
        room.broadCastToAllPlatform(buildReply(builder.build()));
        connectedLiveRecordDaoService.save(new ConnectedLiveRecord(room.getId(), room.getLiveUserId(), user.getId(), user.getPlatformType()));
        DataManager.getUserRoomRecord(user.getId()).addConnectedLiveCount();
        return generateReply(reply, SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.ApplyConnectLiveReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setApplyConnectLiveReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
