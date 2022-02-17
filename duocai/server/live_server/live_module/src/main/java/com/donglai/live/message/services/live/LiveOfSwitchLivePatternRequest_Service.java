package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.message.producer.Producer;
import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;
import static com.donglai.protocol.Constant.UserType.TYPE_LIVEUSER;

@Service("LiveOfSwitchLivePatternRequest")
public class LiveOfSwitchLivePatternRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    Producer producer;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfSwitchLivePatternRequest();
        var reply = Live.LiveOfSwitchLivePatternReply.newBuilder();
        User user = userService.findById(userId);
        Room room = liveProcess.getOnlineRoomByLiveUserId(user.getLiveUserId());
        Constant.ResultCode resultCode = SUCCESS;
        if (!Objects.equals(TYPE_LIVEUSER, user.getUserType())) {
            resultCode = NOT_LIVEUSER;
        } else if (Objects.isNull(room)) {
            resultCode = ROOM_NOT_EXIT;
        } else {
            room.setPattern(request.getPattern());
            roomService.save(room);
            broadCastSwitchLivePattern(room);
        }
        return buildReply(userId, reply, resultCode);
    }

    public void broadCastSwitchLivePattern(Room room) {
        String livePushStreamUrl = liveProcess.getLiveStreamUrl(room, true);
        String livePullStreamUrl = liveProcess.getLiveStreamUrl(room, false);
        liveProcess.broadCastLiveOfSwitchLivePattern(room, livePushStreamUrl, livePullStreamUrl);
    }

    @Override
    public void Close(String s) {

    }
}
