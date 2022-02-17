package com.donglai.live.process;

import com.donglai.common.constant.QueueType;
import com.donglai.common.util.StringUtils;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class LoginProcess {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomProcess roomProcess;

    public void cancelEndLiveQueueIfPresent(User user) {
        if (!StringUtils.isNullOrBlank(user.getLiveUserId())) {
            LiveUser liveUser = liveUserService.findById(user.getLiveUserId());
            QueueExecute queueExecute = queueExecuteService.findByQueueTypeAndRefId(QueueType.TIMEOUT_END_LIVE.getValue(), liveUser.getRoomId());
            if (Objects.nonNull(queueExecute)) {
                log.info("cancel execute EndLiveQueue");
                queueExecuteService.del(queueExecute);
            }
        }
    }

    public Room recoverEnterRoomIfPresent(User user) {
        Room room = null;
        String enterRoomId = LiveRedisUtil.getUserEnterRoomRecord(user.getId());
        if (StringUtils.isNullOrBlank(enterRoomId)) return null;
        if (!roomService.roomIsLive(enterRoomId)) {
            LiveRedisUtil.cleanEnterRoomRecord(user.getId());
        } else {
            room = roomService.findById(enterRoomId);
        }
        return room;
    }
}
