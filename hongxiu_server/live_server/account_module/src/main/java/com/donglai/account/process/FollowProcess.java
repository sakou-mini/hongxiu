package com.donglai.account.process;

import com.donglai.account.message.producer.Producer;
import com.donglai.account.util.MessageUtil;
import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.google.common.collect.Sets;
import com.google.protobuf.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FollowProcess {
    @Autowired
    RoomService roomService;

    public static final UserService userService = SpringApplicationContext.getBean(UserService.class) ;


    public void broadcastFollowOrUnFollowByUser(User leader, User follower, long time, Constant.FollowType followType) {
        String roomId = LiveProcess.getUserEnterRoomRecord(leader.getId());
        if (StringUtils.isNullOrBlank(roomId)) roomId = "";
        broadcastFollowUserInRoom(roomService.findById(roomId), leader, follower, time, followType);
    }

    public static void broadcastFollowUserInRoom(Room room, User leader, User follower, long time, Constant.FollowType followType) {
        Account.FollowInfo leaderInfo = buildFollowInfo(leader,follower,time,"");
        var builder = Account.AccountOfFollowUserBroadcastMessage.newBuilder().setFollowInfo(leaderInfo).setFollowType(followType);
        if (Objects.isNull(room) || room.isClose()) {
            SpringApplicationContext.getBean(Producer.class).broadCastReplyMessage(Sets.newHashSet(leader.getId(), follower.getId()), builder.build(), Constant.ResultCode.SUCCESS, null);
        } else {
            broadCastMessage(room, builder.build());
        }
    }

    public static void broadCastMessage(Room room, Message message) {
        MessageUtil.broadCastMessage(message, Constant.ResultCode.SUCCESS, room.getAudiences(), null);
    }









    //==============build Account.FollowInfo========================
    public static Account.FollowInfo buildFollowInfo(User leaderUser, User followUser, long time, String alias){
        Account.FollowInfo.Builder builder = Account.FollowInfo.newBuilder().setFollowTime(String.valueOf(time));
        if(Objects.nonNull(leaderUser))
            builder.setLeaderInfo(leaderUser.toDetailProto());
        if(Objects.nonNull(followUser))
            builder.setFollowerInfo(followUser.toDetailProto());
        if(!StringUtils.isNullOrBlank(alias)){
            builder.setAlias(alias);
        }
        return builder.build();
    }

    public static Account.FollowInfo buildFollowInfo(FollowList followList){
        Account.FollowInfo.Builder builder = Account.FollowInfo.newBuilder().setFollowTime(String.valueOf(followList.getFollowTime())).setIsNew(followList.isNew());
        User leaderUser = userService.findById(followList.getLeaderId());
        User followUser = userService.findById(followList.getFollowerId());
        if(Objects.nonNull(leaderUser))
            builder.setLeaderInfo(leaderUser.toDetailProto());
        if(Objects.nonNull(followUser))
            builder.setFollowerInfo(followUser.toDetailProto());
        if(!StringUtils.isNullOrBlank(followList.getAlias())){
            builder.setAlias(followList.getAlias());
        }
        return builder.build();
    }
}
