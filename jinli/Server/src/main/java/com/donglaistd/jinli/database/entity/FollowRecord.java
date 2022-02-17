package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class FollowRecord {
    @Id
    private String id = ObjectId.get().toString();
    @Field
    private String leaderId;
    @Field
    private String followerId;
    @Field
    private long recordTime;
    @Field
    private Constant.PlatformType platform;

    public FollowRecord(String leaderId, String followerId,Constant.PlatformType platform) {
        this.leaderId = leaderId;
        this.followerId = followerId;
        this.recordTime = System.currentTimeMillis();
        this.platform = platform;
    }

    public FollowRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(String leaderId) {
        this.leaderId = leaderId;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
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
