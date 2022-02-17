package com.donglai.live.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.RoomProcess;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.live.util.MessageUtil.buildReply;

@Service("LiveOfQueryRoomInfoRequest")
public class LiveOfQueryRoomInfoRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    RoomService roomService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Live.LiveOfQueryRoomInfoRequest request = message.getLiveOfQueryRoomInfoRequest();
        Live.LiveOfQueryRoomInfoReply.Builder replyBuilder = Live.LiveOfQueryRoomInfoReply.newBuilder();
        var roomIds = request.getRoomIdsList();
        Room room;
        for (String roomId : roomIds) {
            room = roomService.findById(roomId);
            if (room != null) {
                replyBuilder.addRoomInfos(roomProcess.buildRoomInfo(room));
            }
        }
        // producer.sendReply(userId,replyBuilder, Constant.ResultCode.SUCCESS);
        return buildReply(userId, replyBuilder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
