package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.sport.SportEvent;
import com.donglai.protocol.Constant;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
public class LiveDetailReply {
    private String nickname;
    private String avatarUrl;
    private String roomImage;
    private String roomId;
    private String liveUserId;
    private Constant.LivePattern pattern;
    private long liveStartTime;
    private int audienceNum;
    private Map<String, Object> liveUrl = new HashMap<>();
    private SportEvent event;

    public LiveDetailReply(Room room, User user) {
        if(Objects.nonNull(user)) {
            this.nickname = user.getNickname();
            this.avatarUrl = user.getAvatarUrl();
        }
        this.roomImage = room.getRoomImage();
        this.liveUserId = room.getLiveUserId();
        this.roomId = room.getId();
        this.pattern = room.getPattern();
        this.liveStartTime = room.getLiveStartTime();
        this.audienceNum = room.getAudiences().size();
    }
}
