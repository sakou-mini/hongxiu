package com.donglaistd.jinli.http.dto.reply;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveRecord;
import com.donglaistd.jinli.database.entity.User;

import java.io.Serializable;
import java.util.Optional;

public class LiveRecordReply implements Serializable {
    public String displayName;
    public String avatar;
    public String liveUserId;
    public long liveStartTime;
    public long liveEndTime;
    public long liveTime;
    public long audienceNum;
    public long liveVisitorCount;
    public long giftCount;
    public long giftFlow;
    public long newFansNum;
    public long bulletMessageCount;
    public long connectedLiveCount;
    public Constant.MobilePhoneBrand loginDevice;
    public String liveIp;
    public long totalLiveTime;
    public long recordTime;

    public LiveRecordReply(User user, LiveRecord liveRecord) {
        this.liveUserId = user.getLiveUserId();
        this.displayName = user.getDisplayName();
        this.avatar = user.getAvatarUrl();
        this.liveStartTime = liveRecord.getLiveStartTime();
        this.liveEndTime = liveRecord.getRecordTime();
        this.liveTime = liveRecord.getLiveTime();
        this.audienceNum = liveRecord.getAudienceNum();
        this.liveVisitorCount = liveRecord.getLiveVisitorCount();
        this.giftCount = liveRecord.getGiftCount();
        this.newFansNum = liveRecord.getNewFansNum();
        this.bulletMessageCount = liveRecord.getBulletMessageCount();
        this.connectedLiveCount = liveRecord.getConnectedLiveCount();
        this.loginDevice = Constant.MobilePhoneBrand.forNumber(Integer.parseInt(Optional.ofNullable(liveRecord.getLoginDevice()).orElse("0")));
        this.liveIp = liveRecord.getLiveIp();
        this.totalLiveTime = liveRecord.getTotalLiveTime();
        this.recordTime = liveRecord.getRecordTime();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public long getLiveStartTime() {
        return liveStartTime;
    }

    public void setLiveStartTime(long liveStartTime) {
        this.liveStartTime = liveStartTime;
    }

    public long getLiveEndTime() {
        return liveEndTime;
    }

    public void setLiveEndTime(long liveEndTime) {
        this.liveEndTime = liveEndTime;
    }

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }

    public long getAudienceNum() {
        return audienceNum;
    }

    public void setAudienceNum(long audienceNum) {
        this.audienceNum = audienceNum;
    }

    public long getLiveVisitorCount() {
        return liveVisitorCount;
    }

    public void setLiveVisitorCount(long liveVisitorCount) {
        this.liveVisitorCount = liveVisitorCount;
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

    public long getNewFansNum() {
        return newFansNum;
    }

    public void setNewFansNum(long newFansNum) {
        this.newFansNum = newFansNum;
    }

    public long getBulletMessageCount() {
        return bulletMessageCount;
    }

    public void setBulletMessageCount(long bulletMessageCount) {
        this.bulletMessageCount = bulletMessageCount;
    }

    public long getConnectedLiveCount() {
        return connectedLiveCount;
    }

    public void setConnectedLiveCount(long connectedLiveCount) {
        this.connectedLiveCount = connectedLiveCount;
    }

    public Constant.MobilePhoneBrand getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(Constant.MobilePhoneBrand loginDevice) {
        this.loginDevice = loginDevice;
    }

    public String getLiveIp() {
        return liveIp;
    }

    public void setLiveIp(String liveIp) {
        this.liveIp = liveIp;
    }

    public long getTotalLiveTime() {
        return totalLiveTime;
    }

    public void setTotalLiveTime(long totalLiveTime) {
        this.totalLiveTime = totalLiveTime;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }
}
