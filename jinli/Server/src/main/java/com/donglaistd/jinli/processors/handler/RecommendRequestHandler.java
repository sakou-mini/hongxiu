package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class RecommendRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(RecommendRequestHandler.class.getName());
    @Autowired
    FollowListDaoService followListDaoService;
    @Value("${recommend.max}")
    int maxRecommend;
    @Autowired
    RoomProcess roomProcess;
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var followeeLiveUserIds = followListDaoService.findAllByFollower(user).stream()
                .map(followList -> followList.getFollowee().getId()).collect(Collectors.toList());
        var heatRoom = DataManager.roomMap.values().stream().sorted(Comparator.comparing(Room::getHeat).reversed())
                .filter(room->room.isLive() && room.isSharedToPlatform(user.getPlatformType())).collect(Collectors.toList());
        List<Room> followerRoom =  heatRoom.stream().filter(room -> followeeLiveUserIds.contains(room.getLiveUserId())).
                sorted(Comparator.comparing(Room::getHeat).reversed()).limit(maxRecommend).collect(Collectors.toList());
        //排序，将未关注的直播间放最后
        heatRoom.removeAll(followerRoom);
        followerRoom.addAll(heatRoom);
        if(!followerRoom.isEmpty()) followerRoom = followerRoom.subList(0, Math.min(maxRecommend,followerRoom.size()));
        var reply = Jinli.RecommendReply.newBuilder();
        Jinli.RoomInfo roomInfo;
        for (Room room : followerRoom) {
            roomInfo = roomProcess.buildLivingRoomInfo(room);
            if(Objects.nonNull(roomInfo)) reply.addRoomList(roomInfo);
        }
        return buildReply(reply, SUCCESS);
    }
}
