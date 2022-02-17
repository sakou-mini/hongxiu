package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.process.RoomProcess;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.NOT_LIVEUSER;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("LiveOfUpdateRoomImageRequest")
public class LiveOfUpdateRoomImageRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomProcess roomProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfUpdateRoomImageRequest();
        var reply = Live.LiveOfUpdateRoomImageReply.newBuilder();
        User user = userService.findById(userId);
        LiveUser liveUser = StringUtils.isNullOrBlank(user.getLiveUserId()) ? null : liveUserService.findById(user.getLiveUserId());
        Constant.ResultCode resultCode;
        if (liveUser == null || !liveUser.isPassLiveUser()) {
            resultCode = NOT_LIVEUSER;
        } else {
            resultCode = SUCCESS;
            Room room = roomService.findById(liveUser.getRoomId());
            room.setRoomImage(request.getRoomImage());
            reply.setRoomInfo(roomProcess.buildRoomInfo(room));
            roomService.save(room);
        }
        return buildReply(userId, reply, resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
