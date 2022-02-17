package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class LiveLimitRecord {
    @Id
    private String id = ObjectId.get().toString();
    @Field
    private long recordTime;
    @Field
    private String liveUserId;
    @Field
    private long limitDate;
    @Field
    private int liveStartHour;
    @Field
    private int liveEndHour;
    @Field
    private boolean addWhiteList;
    @Field
    private String backOfficeUserId;
    @Field
    private Constant.PlatformType platform;
    @Transient
    private String displayName;
    @Transient
    private String account;

    public LiveLimitRecord() {
    }

    public LiveLimitRecord(String liveUserId, long limitDate, int liveStartHour, int liveEndHour,
                           boolean addWhiteList, String backOfficeUserId, Constant.PlatformType platform) {
        this.liveUserId = liveUserId;
        this.limitDate = limitDate;
        this.liveStartHour = liveStartHour;
        this.liveEndHour = liveEndHour;
        this.addWhiteList = addWhiteList;
        this.backOfficeUserId = backOfficeUserId;
        this.platform = platform;
        this.recordTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public long getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(long limitDate) {
        this.limitDate = limitDate;
    }

    public int getLiveStartHour() {
        return liveStartHour;
    }

    public void setLiveStartHour(int liveStartHour) {
        this.liveStartHour = liveStartHour;
    }

    public int getLiveEndHour() {
        return liveEndHour;
    }

    public void setLiveEndHour(int liveEndHour) {
        this.liveEndHour = liveEndHour;
    }

    public boolean isAddWhiteList() {
        return addWhiteList;
    }

    public void setAddWhiteList(boolean addWhiteList) {
        this.addWhiteList = addWhiteList;
    }

    public String getBackOfficeUserId() {
        return backOfficeUserId;
    }

    public void setBackOfficeUserId(String backOfficeUserId) {
        this.backOfficeUserId = backOfficeUserId;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
