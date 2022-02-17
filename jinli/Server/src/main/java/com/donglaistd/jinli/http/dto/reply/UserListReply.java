package com.donglaistd.jinli.http.dto.reply;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;

public class UserListReply {
    public long loginTime;
    public long createTime;
    public String avatarUrl;
    public String displayName;
    public String userId;

    public long watchLiveCount; //直播间次数
    public long watchLiveNum; //直播间数量
    public long exchangeCoinAmount; //兑换金币数量
    public long avgWatchLiveTime;
    public long watchLiveTime;
    public long sendGiftCount;
    public long sendGiftFlow;
    public long connectedLiveCount;
    public long bulletMessageCount;
    public int platformTag;
    public String jinliUserId;
    public Constant.AccountStatue statue;
    public String lastIp;

    public UserListReply(User user) {
        this.createTime = user.getCreateDate().getTime();
        this.loginTime = user.getLastLoginTime();
        this.avatarUrl = user.getAvatarUrl();
        this.displayName = user.getDisplayName();
        this.userId = user.getPlatformShowUserId();
        this.jinliUserId = user.getId();
        this.lastIp = user.getLastIp();
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }


    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getWatchLiveCount() {
        return watchLiveCount;
    }

    public void setWatchLiveCount(long watchLiveCount) {
        this.watchLiveCount = watchLiveCount;
    }

    public long getWatchLiveNum() {
        return watchLiveNum;
    }

    public void setWatchLiveNum(long watchLiveNum) {
        this.watchLiveNum = watchLiveNum;
    }

    public long getExchangeCoinAmount() {
        return exchangeCoinAmount;
    }

    public void setExchangeCoinAmount(long exchangeCoinAmount) {
        this.exchangeCoinAmount = exchangeCoinAmount;
    }

    public long getAvgWatchLiveTime() {
        return avgWatchLiveTime;
    }

    public void setAvgWatchLiveTime(long avgWatchLiveTime) {
        this.avgWatchLiveTime = avgWatchLiveTime;
    }

    public long getWatchLiveTime() {
        return watchLiveTime;
    }

    public void setWatchLiveTime(long watchLiveTime) {
        this.watchLiveTime = watchLiveTime;
    }

    public long getSendGiftCount() {
        return sendGiftCount;
    }

    public void setSendGiftCount(long sendGiftCount) {
        this.sendGiftCount = sendGiftCount;
    }

    public long getSendGiftFlow() {
        return sendGiftFlow;
    }

    public void setSendGiftFlow(long sendGiftFlow) {
        this.sendGiftFlow = sendGiftFlow;
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

    public int getPlatformTag() {
        return platformTag;
    }

    public void setPlatformTag(int platformTag) {
        this.platformTag = platformTag;
    }

    public String getJinliUserId() {
        return jinliUserId;
    }

    public void setJinliUserId(String jinliUserId) {
        this.jinliUserId = jinliUserId;
    }

    public Constant.AccountStatue getStatue() {
        return statue;
    }

    public void setStatue(Constant.AccountStatue statue) {
        this.statue = statue;
    }
}
