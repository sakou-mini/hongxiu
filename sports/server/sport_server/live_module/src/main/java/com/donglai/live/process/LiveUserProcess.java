package com.donglai.live.process;

import com.donglai.common.util.VerifyUtil;
import com.donglai.live.entityBuilder.LiveUserBuilder;
import com.donglai.live.entityBuilder.RoomBuilder;
import com.donglai.live.message.producer.Producer;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LiveUserProcess {
    @Autowired
    Producer producer;
    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    RoomBuilder roomBuilder;

    public Live.LiveUserInfo buildLiveUserInfo(LiveUser liveUser) {
        User user = userService.findById(liveUser.getUserId());
        return Live.LiveUserInfo.newBuilder().setLiveUserId(liveUser.getId())
                .setRoomId(liveUser.getRoomId())
                .setStatus(liveUser.getStatus())
                .setUserId(liveUser.getUserId())
                .setPlatform(liveUser.getPlatform()).addAllSlavePlatforms(liveUser.getSlavePlatforms()).build();
    }

    public void broadCastUserApplyLiveUserResult(String userId, boolean isPass, Map<KafkaMessage.ExtraParam, String> examParam) {
        Live.LiveOfApplyLiveUserResultBroadcastMessage build = Live.LiveOfApplyLiveUserResultBroadcastMessage.newBuilder().setIsPass(isPass).build();
        producer.sendReply(userId, build, Constant.ResultCode.SUCCESS, examParam);
    }

    public void becomeSimpleLiveUser(User user, Constant.PlatformType platform) {
        LiveUser liveUser = liveUserBuilder.createSimpleLiveUser(user.getId(), Constant.LiveUserStatus.LIVE_OFFLINE, platform);
        Room room = roomBuilder.createRoom(liveUser.getId(), user.getId());
        liveUser.setRoomId(room.getId());
        user.becomeLiveUser(liveUser.getId());
        liveUserService.save(liveUser);
        userService.save(user);
    }

    public LiveUser applyLiveUser(Live.LiveOfApplyLiveUserRequest request, String userId, Constant.LiveUserStatus status) {
        LiveUser liveUser = liveUserBuilder.createLiveUser(request, userId, status);
        Room room = roomBuilder.createRoom(liveUser.getId(), userId);
        liveUser.setRoomId(room.getId());
        if (VerifyUtil.isPassLiveUserStatus(status)) {
            User user = userService.findById(userId);
            user.becomeLiveUser(liveUser.getId());
            userService.save(user);
        }
        return liveUserService.save(liveUser);
    }
}
