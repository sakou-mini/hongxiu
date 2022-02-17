package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.annotation.AutoIncKey;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;

@Document
public class DailyUserActiveRecord {
    @AutoIncKey
    @Id
    private long id;
    @Indexed
    private String userId;
    @Indexed
    private long dailyTime;
    private long liveVisitorCount;
    private long liveWatchTime;
    private long avgLiveWatchTime;
    private long giftCount;
    private long giftFlow;
    private long connectedLiveCount;
    private long bulletMessageCount;
    private Constant.PlatformType platform;
    @Transient
    private String displayName;

    public DailyUserActiveRecord() {
    }

    public DailyUserActiveRecord(LiveWatchRecordResult liveWatchRecord, long dailyTime) {
        this.userId = liveWatchRecord.getUserId();
        this.dailyTime = dailyTime;
        this.liveVisitorCount = liveWatchRecord.getWatchCount();
        this.liveWatchTime = liveWatchRecord.getWatchTime();
        this.avgLiveWatchTime = liveWatchRecord.getAvgWatchLiveTime();
        this.giftCount = liveWatchRecord.getGiftCount();
        this.giftFlow = liveWatchRecord.getGiftFlow();
        this.connectedLiveCount = liveWatchRecord.getConnectedLiveCount();
        this.bulletMessageCount = liveWatchRecord.getBulletMessageCount();
        this.platform = liveWatchRecord.getPlatform();
    }

    public static DailyUserActiveRecord newInstance(LiveWatchRecordResult liveWatchRecord, long dailyTime){
        return new DailyUserActiveRecord(liveWatchRecord, dailyTime);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getDailyTime() {
        return dailyTime;
    }

    public void setDailyTime(long dailyTime) {
        this.dailyTime = dailyTime;
    }

    public long getLiveVisitorCount() {
        return liveVisitorCount;
    }

    public void setLiveVisitorCount(long liveVisitorCount) {
        this.liveVisitorCount = liveVisitorCount;
    }

    public long getLiveWatchTime() {
        return liveWatchTime;
    }

    public void setLiveWatchTime(long liveWatchTime) {
        this.liveWatchTime = liveWatchTime;
    }

    public long getAvgLiveWatchTime() {
        return avgLiveWatchTime;
    }

    public void setAvgLiveWatchTime(long avgLiveWatchTime) {
        this.avgLiveWatchTime = avgLiveWatchTime;
    }

    public long getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(long giftCount) {
        this.giftCount = giftCount;
    }

    public long getGiftFlow() {
        return giftFlow;
    }

    public void setGiftFlow(long giftFlow) {
        this.giftFlow = giftFlow;
    }

    public long getConnectedLiveCount() {
        return connectedLiveCount;
    }

    public void setConnectedLiveCount(long connectedLiveCount) {
        this.connectedLiveCount = connectedLiveCount;
    }

    public long getBulletMessageCount() {
        return bulletMessageCount;
    }

    public void setBulletMessageCount(long bulletMessageCount) {
        this.bulletMessageCount = bulletMessageCount;
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
}
