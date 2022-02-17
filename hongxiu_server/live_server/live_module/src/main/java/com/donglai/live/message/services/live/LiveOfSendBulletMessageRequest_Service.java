package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.live.process.RoomProcess;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.util.WordFilterUtil;
import com.donglai.model.db.entity.live.BulletMessage;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.donglai.live.constant.Constant.MAX_BULLET_MSG_CACHE_NUM;
import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("LiveOfSendBulletMessageRequest")
@Slf4j
public class LiveOfSendBulletMessageRequest_Service implements TopicMessageServiceI<String> {
    @Value("${bullet.max.chat.length}")
    private int BULLET_MAX_CHAT_LENGTH;
    @Value("${bullet.chat.time.interval}")
    private long BULLET_CHAT_TIME_INTERVAL;
    private final WordFilterUtil wordFilterUtil;
    private final RoomService roomService;
    final
    RoomProcess roomProcess;

    public LiveOfSendBulletMessageRequest_Service(WordFilterUtil wordFilterUtil, RoomService roomService, RoomProcess roomProcess) {
        this.wordFilterUtil = wordFilterUtil;
        this.roomService = roomService;
        this.roomProcess = roomProcess;
    }

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfSendBulletMessageRequest();
        var replyBuilder = Live.LiveOfSendBulletMessageReply.newBuilder();
        String content = request.getContent();
        String roomId = LiveRedisUtil.getUserEnterRoomRecord(userId);
        long messageInterval = System.currentTimeMillis() - LiveRedisUtil.getUserLastSendBulletTime(userId);
        Constant.ResultCode resultCode;
        if (StringUtils.isNullOrBlank(content) || content.length() > BULLET_MAX_CHAT_LENGTH || request.getContent().isBlank()) {
            resultCode = BULLET_MESSAGE_FORMAT_ERROR;
        } else if (StringUtils.isNullOrBlank(roomId)) {
            resultCode = ROOM_NOT_EXIT;
        } else if (!roomService.roomIsLive(roomId)) {
            resultCode = ROOM_NOT_LIVE;
        } else if (messageInterval < BULLET_CHAT_TIME_INTERVAL) {
            resultCode = SEND_MESSAGE_TOO_SOON;
        } else {
            resultCode = SUCCESS;
            Room room = roomService.findById(roomId);
            content = wordFilterUtil.replaceSensitiveWordAndNumber(content, "*");
            BulletMessage bulletMessage = new BulletMessage(content, userId, roomId);
            addBulletMessageToRoom(room, bulletMessage);
            LiveRedisUtil.saveUserSendBulletTime(userId, BULLET_CHAT_TIME_INTERVAL);
            broadcastSendBulletMessageBroadcastMessage(bulletMessage, room);
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    public void addBulletMessageToRoom(Room room, BulletMessage bulletMessage) {
        if (room.getBulletMessages().size() >= MAX_BULLET_MSG_CACHE_NUM) room.removeFirstBulletMessage();
        room.addBulletMessage(bulletMessage);
        roomService.save(room);
    }

    private void broadcastSendBulletMessageBroadcastMessage(BulletMessage bulletMessage, Room room) {
        var message = Live.LiveOfBulletMessageBroadcastMessage.newBuilder()
                .setBulletMessage(roomProcess.buildBulletMessage(bulletMessage)).build();
        RoomProcess.broadCastMessage(room, message);
    }

    @Override
    public void Close(String s) {

    }
}
