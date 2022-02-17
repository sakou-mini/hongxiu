package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.entity.sport.SportEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportEventRecordReply {
    private SportEvent event;
    private EventLiveRecord liveInfo;


    @Data
    public static class EventLiveRecord{
        private String avatarUrl;
        private String nickname;
        private String userId;
        private String liveUserId;


/*        public EventLiveRecord(User user, LiveRecord liveRecord) {
            this.avatarUrl = user.getAvatarUrl();
            this.nickname = user.getNickname();
            this.userId = user.getId();
            this.liveUserId = user.getLiveUserId();
        }*/

        public EventLiveRecord(User user) {
            this.avatarUrl = user.getAvatarUrl();
            this.nickname = user.getNickname();
            this.userId = user.getId();
            this.liveUserId = user.getLiveUserId();
        }
    }
}
