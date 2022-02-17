package com.donglai.model.db.entity.sport;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.model.db.entity.live.LiveUser;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Data
@CompoundIndex(def = "{'eventId':1,'liveUserId':1,'userId':1}", unique = true)
@Document
@NoArgsConstructor
public class SportLiveSchedule {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String eventId; //赛事id
    @Field
    private String liveUserId;  //安排主播id
    @Field
    private String userId;  //用户id
    @Field
    private long liveBeginTime; //直播开始时间


    public SportLiveSchedule(String eventId, String liveUserId, String userId, long liveBeginTime) {
        this.eventId = eventId;
        this.liveUserId = liveUserId;
        this.userId = userId;
        this.liveBeginTime = liveBeginTime;
    }

    public static SportLiveSchedule newInstance(String eventId, LiveUser liveUser, long liveBeginTime){
        return new SportLiveSchedule(eventId, liveUser.getId(), liveUser.getUserId(), liveBeginTime);
    }
}
