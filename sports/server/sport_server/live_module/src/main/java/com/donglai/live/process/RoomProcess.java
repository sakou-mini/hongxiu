package com.donglai.live.process;

import com.donglai.common.util.StringUtils;
import com.donglai.live.util.MessageUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.BulletMessage;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.model.db.service.sport.SportEventService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import com.google.protobuf.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglai.live.constant.Constant.GIFT_RANK_SHOW_SIZE;

@Component
public class RoomProcess {
    @Value("${room.rank.coefficient}")
    private int coefficient;

    @Autowired
    RoomService roomService;
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    UserProcess userProcess;
    @Autowired
    SportEventService sportEventService;

    public Live.RoomInfo buildRoomInfo(Room room) {
        User user = userService.findById(room.getUserId());
        Live.RoomInfo.Builder builder = Live.RoomInfo.newBuilder().setRoomId(room.getId())
                .setLiveUserId(room.getLiveUserId())
                .setUserId(room.getUserId())
                .setRoomTitle(room.getRoomTitle()).setRoomImage(room.getRoomImage())
                .setHotValue(room.getAudiences().size() * coefficient).setLive(false);
        if (roomService.roomIsLive(room.getId())) {
            builder.setLiveTag(room.getLiveTag()).setLive(true)
                    .addAllGiftRank(room.getAudienceLiveRankIdByLimit(GIFT_RANK_SHOW_SIZE))
                    .setLiveLineCode(room.getLiveLineCode())
                    .setLiveStartTime(room.getLiveStartTime()).setLivePattern(room.getPattern());
            if (!StringUtils.isNullOrBlank(room.getLiveMusic())) {
                builder.setMusic(room.getLiveMusic());
            }
            if (!StringUtils.isNullOrBlank(room.getLiveCode())) {
                builder.setLivePushStreamUrl(liveProcess.getLiveStreamUrl(room, true))
                        .setLivePullStreamUrl(liveProcess.getLiveStreamUrl(room, false));
            }
            if (!StringUtils.isNullOrBlank(room.getEventId())){
                SportEvent sportEvent = sportEventService.findByEventId(room.getEventId());
                if(Objects.nonNull(sportEvent)){
                    builder.setSportEventInfo(sportEvent.toProto());
                }
            }
        }
        return builder.build();
    }


    public Live.BulletMessage buildBulletMessage(BulletMessage bulletMessage) {
        return Live.BulletMessage.newBuilder().setContent(bulletMessage.getContent())
                .setUserInfo(userProcess.buildUerDetailInfo(bulletMessage.getSendUserId())).build();
    }

    public static void broadCastMessage(Room room, Message message) {
        MessageUtil.broadCastMessage(message, Constant.ResultCode.SUCCESS, room.getAudiences());
    }

}
