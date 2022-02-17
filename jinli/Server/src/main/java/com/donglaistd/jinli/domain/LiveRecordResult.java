package com.donglaistd.jinli.domain;

import com.donglaistd.jinli.Constant;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class LiveRecordResult {
    private String liveUserId;
    private String userId;
    private long liveTime;
    private long giftFlow;
    private long gameFlow;
    private Set<Set<String>> audienceHistory;
    private Constant.PlatformType platform = Constant.PlatformType.PLATFORM_JINLI;
    private String roomId;
    private String roomDisplayId;
    private long bulletMessageCount;
    private long liveVisitorCount;
    private long connectedLiveCount;
    private int newFansNum;
    private int fansNum;
    private long totalLiveTime;
    private long giftCount;

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

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }

    public long getGiftFlow() {
        return giftFlow;
    }

    public void setGiftFlow(long giftFlow) {
        this.giftFlow = giftFlow;
    }

    public long getGameFlow() {
        return gameFlow;
    }

    public void setGameFlow(long gameFlow) {
        this.gameFlow = gameFlow;
    }

    public Set<Set<String>> getAudienceHistory() {
        return audienceHistory;
    }

    public void setAudienceHistory(Set<Set<String>> audienceHistory) {
        this.audienceHistory = audienceHistory;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public void setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
    }

    public long getBulletMessageCount() {
        return bulletMessageCount;
    }

    public void setBulletMessageCount(long bulletMessageCount) {
        this.bulletMessageCount = bulletMessageCount;
    }

    public long getLiveVisitorCount() {
        return liveVisitorCount;
    }

    public void setLiveVisitorCount(long liveVisitorCount) {
        this.liveVisitorCount = liveVisitorCount;
    }

    public long getConnectedLiveCount() {
        return connectedLiveCount;
    }

    public void setConnectedLiveCount(long connectedLiveCount) {
        this.connectedLiveCount = connectedLiveCount;
    }

    public int getNewFansNum() {
        return newFansNum;
    }

    public void setNewFansNum(int newFansNum) {
        this.newFansNum = newFansNum;
    }

    public int getFansNum() {
        return fansNum;
    }

    public void setFansNum(int fansNum) {
        this.fansNum = fansNum;
    }

    public long getTotalLiveTime() {
        return totalLiveTime;
    }

    public void setTotalLiveTime(long totalLiveTime) {
        this.totalLiveTime = totalLiveTime;
    }

    public long getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(long giftCount) {
        this.giftCount = giftCount;
    }

    public int getAudienceNum(){
        if(Objects.isNull(this.audienceHistory)) return 0;
        Set<String> audience = new HashSet<>();
        this.audienceHistory.forEach(audience::addAll);
        return audience.size();
    }
}
