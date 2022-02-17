package com.donglai.live.message.services.live;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.live.process.LiveProcess;
import com.donglai.live.process.RoomProcess;
import com.donglai.live.util.LiveRedisUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.live.constant.Constant.BROADCAST_TYPE_LIVE_PLAY_MUSIC;
import static com.donglai.live.constant.Constant.BROADCAST_TYPE_LIVE_STOP_MUSIC;
import static com.donglai.live.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("LiveOfSetRoomPlayingMusicInfoRequest")
public class LiveOfSetRoomPlayingMusicInfoRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    RoomProcess roomProcess;
    @Autowired
    UserService userService;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    RoomService roomService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getLiveOfSetRoomPlayingMusicInfoRequest();
        var reply = Live.LiveOfSetRoomPlayingMusicInfoReply.newBuilder();
        User user = userService.findById(userId);
        String roomId = LiveRedisUtil.getUserEnterRoomRecord(userId);
        Constant.ResultCode resultCode;
        if (StringUtil.isNullOrEmpty(user.getLiveUserId())) {
            resultCode = NOT_LIVEUSER;
        } else {
            Room room = liveProcess.getOnlineRoomByLiveUserId(user.getLiveUserId());
            if (Objects.isNull(room)) {
                resultCode = ROOM_NOT_LIVE;
            } else {
                resultCode = SUCCESS;
                processRoomMusicOptByMusicOptType(room, request);
                broadCastPlayingMusicChangeBroadcastMessage(room, request.getMusicOptType(), request.getMusic());
                roomService.save(room);
            }
        }
        return buildReply(userId, reply.build(), resultCode);
    }

    public void broadCastPlayingMusicChangeBroadcastMessage(Room room, String musicOptType, String music) {
        Live.LiveOfRoomPlayingMusicChangeBroadcastMessage.Builder builder = Live.LiveOfRoomPlayingMusicChangeBroadcastMessage.newBuilder().setMusicOptType(musicOptType);
        if (!StringUtil.isNullOrEmpty(music)) {
            builder.setMusic(music);
        }
        RoomProcess.broadCastMessage(room, builder.build());
    }

    public void processRoomMusicOptByMusicOptType(Room room, Live.LiveOfSetRoomPlayingMusicInfoRequest request) {
        String musicOptType = request.getMusicOptType();
        if (BROADCAST_TYPE_LIVE_PLAY_MUSIC.equals(musicOptType)) {
            room.setLiveMusic(request.getMusic());
        } else if (BROADCAST_TYPE_LIVE_STOP_MUSIC.equals(musicOptType)) {
            room.setLiveMusic("");
        }
    }

    @Override
    public void Close(String s) {

    }
}
