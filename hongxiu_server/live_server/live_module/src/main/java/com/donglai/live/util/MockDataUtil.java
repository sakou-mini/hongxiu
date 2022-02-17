package com.donglai.live.util;

import com.donglai.common.util.StringNumberUtils;
import com.donglai.common.util.StringUtils;
import com.donglai.live.entityBuilder.LiveUserBuilder;
import com.donglai.live.entityBuilder.RoomBuilder;
import com.donglai.live.entityBuilder.UserBuilder;
import com.donglai.live.service.impl.platform.PlatformServiceFactory;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;
import static com.donglai.common.constant.LineSourceConstant.WANGSU_LINE;

@Component
public class MockDataUtil {
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;

    public void createRobotLiveRoom(String accountId, String flagCode, Constant.LiveTag tag, Constant.PlatformType platform) {
        String avatar = PlatformServiceFactory.getPlatformService(platform).getPlatformDefaultAvatar(platform);
        User user = userBuilder.createUser(accountId, "123123", flagCode, avatar, 0, platform);
        user.setTourist(false);
        LiveUser liveUser = liveUserBuilder.createSimpleLiveUser(user.getId(), Constant.LiveUserStatus.LIVE_LIVE, platform);
        Room room = roomBuilder.createRoom(liveUser.getId(), user.getId());
        liveUser.setRoomId(room.getId());
        room.setLiveLineCode(WANGSU_LINE);
        room.setRoomTitle("官方直播间" + flagCode);
        room.setLiveCode(StringUtils.createUUID());
        room.setLiveTag(tag);
        room.setRobotRoom(true);
        room.setLiveStartTime(System.currentTimeMillis());
        roomService.save(room);
        roomService.addLiveRoom(room.getId());
        liveUserService.save(liveUser);
        if (StringUtils.isNullOrBlank(user.getLiveUserId())) {
            user.becomeLiveUser(liveUser.getId());
            userService.save(user);
        }
    }

    public static List<Live.GiftRankInfo> mockGiftRackData(int num) {
        List<Live.GiftRankInfo> rankInfos = new ArrayList<>(num);
        Live.GiftRankInfo.Builder builder;
        for (int i = 0; i < num; i++) {
            builder = Live.GiftRankInfo.newBuilder();
            builder.setAmount(20 + i * 10).setUserInfo(Common.UserInfo.newBuilder().setUserId("70000" + i).setLevel(2 + i)
                    .setAvatarUrl(DEFAULT_AVATAR_PATH)
                    .setNickname("游客_" + StringNumberUtils.generateNumberId(70000 + i, 7))
                    .setVipLevel(Constant.VipType.RANGER)
            ).build();
            rankInfos.add(builder.build());
        }
        return rankInfos;
    }
}
