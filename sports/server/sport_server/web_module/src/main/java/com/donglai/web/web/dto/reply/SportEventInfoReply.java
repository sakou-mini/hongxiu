package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.sport.SportEvent;
import lombok.Data;

@Data
public class SportEventInfoReply {
    private SportEvent event;
    private EventLiveInfo liveInfo;

    public SportEventInfoReply(SportEvent event) {
        this.event = event;
    }

    public SportEventInfoReply(SportEvent event, EventLiveInfo liveInfo) {
        this.event = event;
        this.liveInfo = liveInfo;
    }

    @Data
    public static class EventLiveInfo{
        private String avatarUrl;
        private String nickname;
        private String userId;
        private String liveUserId;
        private boolean isLive;
        private long liveBeginTime;
        private String roomId;

        public EventLiveInfo(User user, boolean isLive,String roomId) {
            this.avatarUrl = user.getAvatarUrl();
            this.nickname = user.getNickname();
            this.userId = user.getId();
            this.liveUserId = user.getLiveUserId();
            this.isLive = isLive;
            this.roomId = roomId;
        }
    }
}
