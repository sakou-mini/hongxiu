package com.donglaistd.jinli.domain;

import com.donglaistd.jinli.Constant;

public class LiveWatchRecordResult {
    public long connectedLiveCount = 0;
    public long bulletMessageCount = 0;
    public long watchTime;
    public long watchNum;
    public long watchCount;
    public String userId;
    public long giftCount;
    public long giftFlow;
    public Constant.PlatformType platform;

    public LiveWatchRecordResult(long connectedLiveCount, long bulletMessageCount, long watchTime, long watchNum, long watchCount, String userId, long giftCount, long giftFlow, Constant.PlatformType platform) {
        this.connectedLiveCount = connectedLiveCount;
        this.bulletMessageCount = bulletMessageCount;
        this.watchTime = watchTime;
        this.watchNum = watchNum;
        this.watchCount = watchCount;
        this.userId = userId;
        this.giftCount = giftCount;
        this.giftFlow = giftFlow;
        this.platform = platform;
    }

    public LiveWatchRecordResult() {
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

    public long getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(long watchTime) {
        this.watchTime = watchTime;
    }

    public long getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(long watchNum) {
        this.watchNum = watchNum;
    }

    public long getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(long watchCount) {
        this.watchCount = watchCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getAvgWatchLiveTime(){
        if(watchCount>0) return watchTime / watchCount;
        return 0;
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

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }
}
