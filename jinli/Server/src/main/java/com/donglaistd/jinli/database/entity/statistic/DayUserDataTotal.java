package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
public class DayUserDataTotal {
    @Id
    private ObjectId id = ObjectId.get();
    private long liveVisitorCount;
    private Set<String> liveVisitorIds;
    private long exchangeCoinAmount;
    private long liveWatchTime;
    private long avgLiveWatchTime;
    private long giftCount;
    private long giftFlow;
    private long connectedLiveCount;
    private long bulletMessageCount;
    private long banUserNum;
    @Indexed
    private long recordTime;
    private Constant.PlatformType platform;

    public DayUserDataTotal() {
    }

    public DayUserDataTotal(long time, Constant.PlatformType platform) {
        this.recordTime = time;
        this.platform = platform;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getLiveVisitorCount() {
        return liveVisitorCount;
    }

    public void setLiveVisitorCount(long liveVisitorCount) {
        this.liveVisitorCount = liveVisitorCount;
    }

    @JsonIgnore
    public Set<String> getLiveVisitorIds() {
        return liveVisitorIds;
    }


    public void setLiveVisitorIds(Set<String> liveVisitorIds) {
        this.liveVisitorIds = liveVisitorIds;
    }
    public long getLiveVisitorNum(){
        return liveVisitorIds.size();
    }

    public long getExchangeCoinAmount() {
        return exchangeCoinAmount;
    }

    public void setExchangeCoinAmount(long exchangeCoinAmount) {
        this.exchangeCoinAmount = exchangeCoinAmount;
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

    public long getBanUserNum() {
        return banUserNum;
    }

    public void setBanUserNum(long banUserNum) {
        this.banUserNum = banUserNum;
    }
}
