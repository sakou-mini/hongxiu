package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document
public class DayLiveDataTotal {
    @Id
    private ObjectId id = ObjectId.get();
    public long liveCount;//开播次数
    public long liveUserNum;
    public long banLiveUserCount;//主播禁言人数
    public long banUserCount;//玩家禁言人数
    public long rewardCount; //打赏次数
    public long incomeUserNum;//收礼人数
    public long incomeCoin; //打赏总金额（收入）
    public long visitorNum; //累计访问用户去重
    public long liveVisitorCount;//直播间访问次数
    public long bulletChatNum; //弹幕互动数
    public long connectedLiveCount;//连麦次数
    public long attentionNum;//关注数量
    public long liveTime; //开播时长
    @Indexed
    private Constant.PlatformType platform;
    @Indexed
    private long recordTime;

    //user data total
    private Set<String> visitorIds = new HashSet<>();

    public DayLiveDataTotal() {

    }

    public long getLiveCount() {
        return liveCount;
    }

    public void setLiveCount(long liveCount) {
        this.liveCount = liveCount;
    }

    public long getBanLiveUserCount() {
        return banLiveUserCount;
    }

    public void setBanLiveUserCount(long banLiveUserCount) {
        this.banLiveUserCount = banLiveUserCount;
    }

    public long getRewardCount() {
        return rewardCount;
    }

    public void setRewardCount(long rewardCount) {
        this.rewardCount = rewardCount;
    }

    public long getIncomeCoin() {
        return incomeCoin;
    }

    public void setIncomeCoin(long incomeCoin) {
        this.incomeCoin = incomeCoin;
    }

    public long getVisitorNum() {
        return visitorNum;
    }

    public void setVisitorNum(long visitorNum) {
        this.visitorNum = visitorNum;
    }

    public long getLiveVisitorCount() {
        return liveVisitorCount;
    }

    public void setLiveVisitorCount(long liveVisitorCount) {
        this.liveVisitorCount = liveVisitorCount;
    }

    public long getBulletChatNum() {
        return bulletChatNum;
    }

    public void setBulletChatNum(long bulletChatNum) {
        this.bulletChatNum = bulletChatNum;
    }

    public long getConnectedLiveCount() {
        return connectedLiveCount;
    }

    public void setConnectedLiveCount(long connectedLiveCount) {
        this.connectedLiveCount = connectedLiveCount;
    }

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }

    public long getAttentionNum() {
        return attentionNum;
    }

    public void setAttentionNum(long attentionNum) {
        this.attentionNum = attentionNum;
    }

    public long getBanUserCount() {
        return banUserCount;
    }

    public void setBanUserCount(long banUserCount) {
        this.banUserCount = banUserCount;
    }

    public long getIncomeUserNum() {
        return incomeUserNum;
    }

    public void setIncomeUserNum(long incomeUserNum) {
        this.incomeUserNum = incomeUserNum;
    }

    public long getLiveUserNum() {
        return liveUserNum;
    }

    public void setLiveUserNum(long liveUserNum) {
        this.liveUserNum = liveUserNum;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }

    public Set<String> getVisitorIds() {
        return visitorIds;
    }

    public void setVisitorIds(Set<String> visitorIds) {
        this.visitorIds = visitorIds;
    }
}
