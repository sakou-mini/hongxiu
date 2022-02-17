package com.donglai.live.process;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.live.util.MetaUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.proto.metadata.Metadata;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class UserProcess {
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    UserService userService;
    @Autowired
    FollowListService followListService;
    @Autowired
    RoomService roomService;

    //===================buildUserDetailInfo========================
    public Common.UserInfo buildUerDetailInfo(User user) {
        long followerNum = followListService.countFollowerNumByUserId(user.getId());
        long leaderNum = followListService.countLeaderNumByUserId(user.getId());
        return user.toDetailProto().toBuilder().build();
    }

    public Common.UserInfo buildUerDetailInfo(String userId) {
        User user = userService.findById(userId);
        if (user == null) {
            log.warn("not found user by id {}", userId);
            return null;
        }
        return buildUerDetailInfo(user);
    }

    // ===================update user exp========================
    private boolean addExp(User user, long addExp) {
        var nowExp = user.getExperience() + addExp;
        int level = user.getLevel();
        int oldLevel = user.getLevel();
        int needExp;
        do {
            Metadata.PlayerDefine define = MetaUtil.getPlayerDefineByCurrentLevel(level);
            needExp = define.getExp();
            if (nowExp >= needExp) {
                nowExp = nowExp - needExp;
                level++;
            }
        } while (nowExp >= needExp);
        user.setExperience(nowExp);
        return oldLevel != level;
    }

    private boolean updateVipByLevel(User user) {
        Metadata.VipDefine levelVip = MetaUtil.getVipDefineByPlayerLevel(user.getLevel());
        if (user.getVipLevel().getNumber() != levelVip.getVipIdValue()) {
            user.setVipLevel(Constant.VipType.forNumber(levelVip.getVipIdValue()));
            return true;
        }
        return false;
    }

    public void addUserExp(long exp, User user) {
        boolean upgrade = addExp(user, exp);
        if (upgrade) {
            if (updateVipByLevel(user)) {
                //broadCast if in liveRoom;
            }
        }
    }

    public static void sendModifyUserCoinListenerRequest(String userId, int coin) {
        Live.LiveOfModifyUserCoinListenerRequest.Builder request = Live.LiveOfModifyUserCoinListenerRequest.newBuilder();
        request.addModifyUser(Live.LiveOfModifyUserCoinListenerRequest.ModifyUser.newBuilder()
                .setUserId(userId).setCoin(coin).build());
        SpringApplicationContext.getBean(Producer.class).sendMessageRequest(userId, request.build());
    }

    /*check user is live*/
    public boolean userIsOpenLive(String userId) {
        String roomId = LiveRedisUtil.getUserEnterRoomRecord(userId);
        Room room = roomService.findById(roomId);
        if (room == null) return false;
        return Objects.equals(room.getUserId(), userId);
    }
}
