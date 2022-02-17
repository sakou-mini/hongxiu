package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.LiveProcess;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("LiveOfAutoQuitRoomRequest")
@Slf4j
public class LiveOfAutoQuitRoomRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {

        Live.LiveOfAutoQuitRoomRequest request = message.getLiveOfAutoQuitRoomRequest();
        String roomId = request.getRoomId();
        log.info("auto quitRoom:{} by user {}", roomId, userId);
        Room room = roomService.findById(roomId);
        if (!room.isLive()) {
            return null;
        }
        liveProcess.quitRoom(room, userService.findById(userId));
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
