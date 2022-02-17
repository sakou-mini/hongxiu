package com.donglai.live.message.services.modultNotice;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.process.LiveProcess;
import com.donglai.live.process.QueueProcess;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("LiveOfUserDisconnectionRequest")
@Slf4j
public class LiveOfUserDisconnectionRequest_Service implements TopicMessageServiceI<String> {
    @Value("${live.user.disconnect.close.live.time}")
    private long disconnectOverTime;
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    Producer producer;

    @Override
    public KafkaMessage.TopicMessage Process(String s, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfUserDisconnectionRequest();
        String userId = request.getUserId();
        User user = userService.findById(userId);
        if (Objects.isNull(user)) return null;
        quitRoomIfHasEnterRoom(user);
        return null;
    }

    public void quitRoomIfHasEnterRoom(User user) {
        String enterRoomId = LiveRedisUtil.getUserEnterRoomRecord(user.getId());
        if (StringUtils.isNullOrBlank(enterRoomId)) return;
        Room room = roomService.findById(enterRoomId);
        if (!room.isClose() && roomService.roomIsLive(room.getId())) {
            if (Objects.equals(room.getUserId(), user.getId())) {
                //close live 广播主播断线了
                long endTime = System.currentTimeMillis() + disconnectOverTime;
                log.info("主播断线了，将会自动关闭直播间:{}", endTime);
                queueProcess.createAndSendAutoEndLiveQueue(room, endTime);
                //TODO 通知 房间内的玩家主播断线了
            } else {
                liveProcess.quitRoom(room, user);
            }
        }
    }

    @Override
    public void Close(String s) {

    }
}

