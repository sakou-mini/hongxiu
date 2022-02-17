package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class LiveRecord {
    @Id
    @AutoIncKey
    private long id;
    private long startTime;
    private long endTime;
    private String liveUserId;
    private String userId;
    private String roomId;

    private LiveRecord(long startTime, long endTime, String liveUserId, String userId, String roomId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.liveUserId = liveUserId;
        this.userId = userId;
        this.roomId = roomId;
    }

    public static LiveRecord newInstance(long startTime, long endTime, String liveUserId, String userId, String roomId){
        return new LiveRecord(startTime, endTime, liveUserId, userId, roomId);
    }

    public long getLiveTime(){
        return endTime - startTime;
    }
}
