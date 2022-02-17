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
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;
import static com.donglai.protocol.Constant.UserType.TYPE_LIVEUSER;

@Service("LiveOfSwitchLiveSourceLineRequest")
public class LiveOfSwitchLiveSourceLineRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    Producer producer;
    @Autowired
    LiveProcess liveProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfSwitchLiveSourceLineRequest();
        var reply = Live.LiveOfSwitchLiveSourceLineReply.newBuilder();
        User user = userService.findById(userId);
        Room room = liveProcess.getOnlineRoomByLiveUserId(user.getLiveUserId());
        Constant.ResultCode resultCode;
        if (!LiveProcess.VerifySwitchLiveSourceOrStartLiveParam(request.getLiveDomain(), request.getLiveLineCode())) {
            resultCode = LIVE_PARAM_ERROR;
        } else if (!Objects.equals(TYPE_LIVEUSER, user.getUserType())) {
            resultCode = NOT_LIVEUSER;
        } else if (Objects.isNull(room)) {
            resultCode = ROOM_NOT_EXIT;
        } else {
            resultCode = SUCCESS;
            room.setLiveLineCode(request.getLiveLineCode());
            room.setLiveDomain(request.getLiveDomain());
            roomService.save(room);
            broadCastSwitchLiveSourceLineLiveUrl(room);
        }
        return buildReply(userId, reply, resultCode);
    }

    public void broadCastSwitchLiveSourceLineLiveUrl(Room room) {
        broadCastPushUrlToLiveUser(room);
        var builder = Live.LiveOfSwitchLiveSourceLineBroadcastMessage.newBuilder();
        Set<String> audience = room.getAudiences().stream().filter(userId -> !Objects.equals(userId, room.getUserId())).collect(Collectors.toSet());
        builder.setLiveStreamUrl(liveProcess.getLiveStreamUrl(room, false)).setLiveLineCode(room.getLiveLineCode());
        producer.broadCastReplyMessage(audience, builder.build(), Constant.ResultCode.SUCCESS);
    }

    public void broadCastPushUrlToLiveUser(Room room) {
        var builder = Live.LiveOfSwitchLiveSourceLineBroadcastMessage.newBuilder();
        builder.setLiveLineCode(room.getLiveLineCode()).setLiveStreamUrl(liveProcess.getLiveStreamUrl(room, true));
        producer.sendReply(room.getUserId(), builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
