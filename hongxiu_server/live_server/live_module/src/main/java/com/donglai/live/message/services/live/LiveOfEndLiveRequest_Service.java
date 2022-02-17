package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.process.LiveProcess;
import com.donglai.live.process.QueueProcess;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.ROOM_NOT_LIVE;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("LiveOfEndLiveRequest")
@Slf4j
public class LiveOfEndLiveRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    RoomService roomService;
    @Autowired
    Producer producer;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    QueueProcess queueProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        User user = userService.findById(userId);
        var request = message.getLiveOfEndLiveRequest();
        var replyBuilder = Live.LiveOfEndLiveReply.newBuilder();
        LiveUser liveUser = StringUtils.isNullOrBlank(user.getLiveUserId()) ? null : liveUserService.findById(user.getLiveUserId());
        Constant.ResultCode resultCode;
        if (Objects.isNull(liveUser) || !Objects.equals(liveUser.getStatus(), Constant.LiveUserStatus.LIVE_LIVE) || !roomService.roomIsLive(liveUser.getRoomId())) {
            resultCode = ROOM_NOT_LIVE;
        } else {
            resultCode = SUCCESS;
            Room room = roomService.findById(liveUser.getRoomId());
            delayEndLive(room, request.getEndDelayTime());
        }
        return buildReply(userId, replyBuilder, resultCode);
    }

    public void delayEndLive(Room room, long delayTime) {
        if (room.isClose()) {
            log.info("房间已经关闭了关闭了直播间");
            return;
        }
        long endTime = System.currentTimeMillis() + delayTime;
        log.info("直播间 将在{} ms 后关闭,结束时间戳为:{}", delayTime, endTime);
        queueProcess.createAndSendEndLiveQueue(room, delayTime);
        ;
        room.setClose(true);
        roomService.save(room);
        liveProcess.broadcastLiveOfReadyEndLiveBroadcastMessage(room, endTime);
    }

    @Override
    public void Close(String s) {

    }
}
