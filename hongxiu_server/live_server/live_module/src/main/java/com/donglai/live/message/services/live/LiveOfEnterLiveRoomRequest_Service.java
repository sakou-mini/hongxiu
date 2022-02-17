package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.LiveProcess;
import com.donglai.live.process.RoomProcess;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.live.util.MessageUtil.buildReply;

@Service("LiveOfEnterLiveRoomRequest")
@Slf4j
public class LiveOfEnterLiveRoomRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    RoomService roomService;
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    FollowListService followListService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        User user = userService.findById(userId);
        var request = message.getLiveOfEnterLiveRoomRequest();
        var replyBuilder = Live.LiveOfEnterLiveRoomReply.newBuilder();
        String roomId = request.getRoomId();
        if (!roomService.roomIsLive(roomId)) {
            return buildReply(userId, replyBuilder, Constant.ResultCode.ROOM_NOT_LIVE);
        } else {
            Room room = roomService.findById(roomId);
            liveProcess.quitOldRoomIfPresent(userId, roomId);
            room.addAudience(userId);
            roomService.save(room);
            LiveRedisUtil.saveUserEnterRoomRecord(userId, roomId);
            replyBuilder.setRoomInfo(roomProcess.buildRoomInfo(room))
                    .setIsFollow(followListService.isUserFollower(room.getUserId(), userId));
            liveProcess.broadCastEnterLiveRoomMessage(user, room);
            return buildReply(userId, replyBuilder, Constant.ResultCode.SUCCESS);
        }
    }

    @Override
    public void Close(String s) {

    }
}
