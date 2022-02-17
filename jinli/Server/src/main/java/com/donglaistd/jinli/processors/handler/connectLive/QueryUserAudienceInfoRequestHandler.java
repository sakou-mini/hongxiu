package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.entity.Blacklist;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.RoomManagementHandlerService;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public class QueryUserAudienceInfoRequestHandler extends RoomManagementHandler{
    private final RoomManagementHandlerService roomManagementHandlerService;
    private final BlacklistDaoService blacklistDaoService;

    public QueryUserAudienceInfoRequestHandler(RoomManagementHandlerService roomManagementHandlerService, BlacklistDaoService blacklistDaoService) {
        this.roomManagementHandlerService = roomManagementHandlerService;
        this.blacklistDaoService = blacklistDaoService;
    }

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.QueryUserAudienceInfoRequest request = messageRequest.getQueryUserAudienceInfoRequest();
        String userId = request.getUserId();
        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        RoomManagement.Audience audience = roomManagementHandlerService.getAudience(room, blacklist, userId);
        RoomManagement.QueryUserAudienceInfoReply.Builder replyBuilder = RoomManagement.QueryUserAudienceInfoReply.newBuilder().setUserAudienceInfo(audience);
        return generateReply(replyBuilder);
    }


    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply (RoomManagement.QueryUserAudienceInfoReply.Builder builder) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setQueryUserAudienceInfoReply(builder.build());
        return new Pair<>(reply.build(), Constant.ResultCode.SUCCESS);
    }
}
