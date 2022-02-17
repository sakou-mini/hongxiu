package com.donglai.web.process;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.protocol.Constant;
import com.donglai.web.builder.LiveUserBuilder;
import com.donglai.web.builder.RoomBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LiveUserProcess {
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    RoomBuilder roomBuilder;

    public void becomeSimpleLiveUser(User user, Constant.PlatformType platform) {
        LiveUser liveUser = liveUserBuilder.createSimpleLiveUser(user.getId(), Constant.LiveUserStatus.LIVE_OFFLINE, platform);
        Room room = roomBuilder.createRoom(liveUser.getId(), user.getId());
        liveUser.setRoomId(room.getId());
        user.becomeLiveUser(liveUser.getId());
        liveUserService.save(liveUser);
        userService.save(user);
    }
}
