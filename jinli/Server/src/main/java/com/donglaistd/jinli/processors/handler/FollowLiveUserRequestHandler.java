package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.FollowRecordDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.FollowRecord;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class FollowLiveUserRequestHandler extends MessageHandler {
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    FollowRecordDaoService followRecordDaoService;

    public static void broadcastFollowLiveUser(Room room, String followerUserId, String liveUserUserId,Constant.FollowType followType){
        if(room == null) return;
        Jinli.FollowLiveUserBroadcastMessage followLiveUserBroadcast = Jinli.FollowLiveUserBroadcastMessage.newBuilder()
                .setFollowerUserId(followerUserId)
                .setLiveUserUserId(liveUserUserId)
                .setFollowType(followType).build();
        room.broadCastToAllPlatform(buildReply(followLiveUserBroadcast));
    }

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getFollowLiveUserRequest();
        var reply = Jinli.FollowLiveUserReply.newBuilder();

        var userId = request.getUserId();
        if (Objects.equals(user.getId(), userId)) {
            buildReply(reply.setUserId(userId), CAN_NOT_FOLLOWING_YOURSELF);
        }

        var followee = userDaoService.findById(userId);
        if (followee == null) {
            return buildReply(reply.setUserId(userId), USER_NOT_FOUND);
        }
        var liveUser = liveUserDaoService.findById(followee.getLiveUserId());
        if (liveUser == null) {
            return buildReply(reply.setUserId(userId), USER_NOT_FOUND);
        }
        boolean contains = followListDaoService.findAllByFollower(user).stream().map(FollowList::getFollowee).collect(Collectors.toList()).contains(liveUser);
        if (contains) {
            return buildReply(reply.setUserId(userId), ALREADY_FOLLOWING);
        }
        var followList = new FollowList(user, liveUser);
        followListDaoService.save(followList);
        broadcastFollowLiveUser(DataManager.findOnlineRoom(liveUser.getRoomId()), user.getId(), liveUser.getUserId(),Constant.FollowType.FOLLOW);
        followRecordDaoService.save(new FollowRecord(liveUser.getUserId(), userId, user.getPlatformType()));
        return buildReply(reply.setUserId(userId), SUCCESS);
    }
}
