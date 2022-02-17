package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.meta.LineSourceMetaBuilder;
import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.meta.LineSource;
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
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("LiveOfStartLiveRequest")
@Slf4j
public class LiveOfStartLiveRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    LineSourceMetaBuilder lineSourceMetaBuilder;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Live.LiveOfStartLiveRequest request = message.getLiveOfStartLiveRequest();
        Live.LiveOfStartLiveReply.Builder replyBuilder = Live.LiveOfStartLiveReply.newBuilder();
        User user = userService.findById(userId);
        Constant.ResultCode resultCode;
        LiveUser liveUser = StringUtils.isNullOrBlank(user.getLiveUserId()) ? null : liveUserService.findById(user.getLiveUserId());
        LineSource lineSource = lineSourceMetaBuilder.getById(request.getLiveLineCode());
        if (!LiveProcess.VerifySwitchLiveSourceOrStartLiveParam(request.getLiveDomain(), request.getLiveLineCode())) {
            resultCode = LIVE_PARAM_ERROR;
        } else if (liveUser == null || !liveUser.isPassLiveUser()) {
            resultCode = NOT_LIVEUSER;
        } else if (Objects.isNull(lineSource)) {
            resultCode = LIVE_LINE_NOT_EXIT;
        } else {
            resultCode = SUCCESS;
            startLive(request, liveUser);
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    public void startLive(Live.LiveOfStartLiveRequest request, LiveUser liveUser) {
        Room room = roomService.findById(liveUser.getRoomId());
        room.setRoomTitle(request.getRoomTitle());
        room.setLiveStartTime(System.currentTimeMillis());
        room.setPattern(request.getPattern());
        room.setLiveCode(StringUtils.createUUID());
        room.setLiveLineCode(request.getLiveLineCode());
        room.setLiveDomain(request.getLiveDomain());
        liveUser.setStatus(Constant.LiveUserStatus.LIVE_LIVE);
        liveUserService.save(liveUser);
        roomService.save(room);
        roomService.addLiveRoom(room.getId());
    }

    @Override
    public void Close(String s) {

    }
}
