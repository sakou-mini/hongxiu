package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.metadata.Metadata;
import com.donglaistd.jinli.service.RoomManagementHandlerService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MetaUtil;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class QueryConnectLiveListRequestHandler extends RoomManagementHandler {
    @Autowired
    DataManager dataManager;
    @Autowired
    private BlacklistDaoService blacklistDaoService;
    @Autowired
    RoomManagementHandlerService roomManagementHandlerService;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        var replyBuilder = RoomManagement.QueryConnectLiveListReply.newBuilder();
        Set<RoomManagement.Audience> connectLiveAudience = room.getConnectLiveCode().stream().map(Pair::getLeft)
                .map(uid -> roomManagementHandlerService.getAudience(room, blacklist, uid)).collect(Collectors.toSet());
        String roomUserId = dataManager.findLiveUser(room.getLiveUserId()).getUserId();
        RoomManagement.Audience liveUserAudience = roomManagementHandlerService.getAudience(room, blacklist, roomUserId);
        List<RoomManagement.Audience> allAdministrators = room.getAdministrators().stream().filter(uid -> !uid.equals(roomUserId)).map(uid -> roomManagementHandlerService.getAudience(room, blacklist, uid)).collect(Collectors.toList());
        allAdministrators.add(0,liveUserAudience);
        List<RoomManagement.Audience> audiences =  room.getAllPlatformAudienceList().stream().filter(uid -> !uid.equals(roomUserId) && !room.containsAdministrator(uid))
                .map(uid -> roomManagementHandlerService.getAudience(room, blacklist, uid))
                .sorted(Comparator.comparing(RoomManagement.Audience::getLevel).reversed()).collect(Collectors.toList());
        List<RoomManagement.Audience> vipChairs = replyBuilder.getAudienceList().stream()
                .filter(b -> canVipChair(b.getVipId()) && !room.containsAdministrator(b.getUserId()))
                .sorted(Comparator.comparing(RoomManagement.Audience::getVipIdValue).reversed())
                .collect(Collectors.toList());
        replyBuilder.setCurrentConnectLiveUserId(room.getCurrentConnectLiveUserId()).setMuteAll(room.getMuteAll()).addAllSummary(connectLiveAudience)
                .addAllAudience(allAdministrators).addAllAudience(audiences).addAllVipChairs(vipChairs)
                .addAllMuteChatRecords(roomManagementHandlerService.getMuteChatRecordIfAdministratorsOrLiveUser(blacklist, user, room));
        return generateReply(replyBuilder);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.QueryConnectLiveListReply.Builder builder) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setQueryConnectLiveListReply(builder.build());
        return new Pair<>(reply.build(), Constant.ResultCode.SUCCESS);
    }

    private boolean canVipChair(Constant.VipType vipId) {
        Metadata.VipDefine define = MetaUtil.getVipDefineByCurrentLevel(vipId.getNumber());
        if (Objects.isNull(define)) return false;
        Metadata.FunctionList function = define.getFunctionList();
        if (Objects.isNull(function)) return false;
        return function.getVipChair();
    }
}
