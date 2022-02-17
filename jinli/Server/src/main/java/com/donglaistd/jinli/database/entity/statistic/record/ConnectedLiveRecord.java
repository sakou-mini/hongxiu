package com.donglaistd.jinli.database.entity.statistic.record;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class ConnectedLiveRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Indexed
    private String roomId;
    @Indexed
    private String liveUserId;
    @Indexed
    private String userId;
    private long recordTime;
    private Constant.PlatformType platform;

    public ConnectedLiveRecord() {
    }

    public ConnectedLiveRecord(String roomId, String liveUserId, String userId, Constant.PlatformType platform) {
        this.roomId = roomId;
        this.liveUserId = liveUserId;
        this.userId = userId;
        this.platform = platform;
        this.recordTime = System.currentTimeMillis();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }
}
