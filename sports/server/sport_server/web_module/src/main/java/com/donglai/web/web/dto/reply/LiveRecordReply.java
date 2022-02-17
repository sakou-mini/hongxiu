package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.entity.sport.SportEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LiveRecordReply {
    private String userId;// id
    private String liveUserId; //主播id
    private String avatar; //头像
    private String nickname; //昵称
    private long liveStartTime; //直播开始时间
    private long liveEndTime; //直播结束时间
    private long liveTime;
    private int offlineCount; //掉线次数
    private SportEvent sportEvent; //赛事相关信息


    public LiveRecordReply(User user, LiveRecord liveRecord) {
        this.userId = user.getId();
        this.liveUserId = user.getLiveUserId();
        this.avatar = user.getAvatarUrl();
        this.nickname = user.getNickname();
        this.liveStartTime = liveRecord.getStartTime();
        this.liveEndTime = liveRecord.getEndTime();
        this.liveTime = liveRecord.getLiveTime();
        this.offlineCount = liveRecord.getOfflineCount();
    }
}
