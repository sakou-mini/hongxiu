package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.ROOM_NOT_LIVE;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Slf4j
@Service("LiveOfQuitRoomRequest")
public class LiveOfQuitRoomRequest_Service implements TopicMessageServiceI<String> {
    @Value("${room.rank.coefficient}")
    private int COEFFICIENT;

    private final RoomService roomService;
    private final UserService userService;
    private final LiveProcess liveProcess;

    public LiveOfQuitRoomRequest_Service(RoomService roomService, UserService userService, LiveProcess liveProcess) {
        this.roomService = roomService;
        this.userService = userService;
        this.liveProcess = liveProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var roomId = message.getLiveOfQuitRoomRequest().getRoomId();
        var reply = Live.LiveOfQuitRoomReply.newBuilder().build();
        var room = roomService.findById(roomId);
        if (!roomService.roomIsLive(roomId)) {
            //producer.sendReply(userId, reply, ROOM_NOT_LIVE);
            return buildReply(userId, reply, ROOM_NOT_LIVE);
        } else {
            User user = userService.findById(userId);
            liveProcess.quitRoom(room, user);
            //producer.sendReply(userId, reply, SUCCESS);
            return buildReply(userId, reply, SUCCESS);
        }
    }

    public Live.LiveOfQuitRoomBroadcastMessage buildQuitRoomMessage(User user, Room room) {
        return Live.LiveOfQuitRoomBroadcastMessage.newBuilder().setUserInfo(user.toSummaryProto().toBuilder().clearCoin().build())
                .setRoomId(room.getId()).setHotValue(room.getAudiences().size() * COEFFICIENT).build();
    }

    @Override
    public void Close(String s) {

    }
}
