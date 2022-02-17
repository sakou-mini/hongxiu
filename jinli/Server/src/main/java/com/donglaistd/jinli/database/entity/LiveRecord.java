package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Set;

public class LiveRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String liveUserId;
    @Field
    private String userId;
    @Field
    private long liveStartTime;
    @Field
    private long liveTime;
    @Field
    private long giftFlow;
    @Field
    private long gameFlow;
    @Field
    private int audienceNum;
    @Field
    private long recordTime;
    @Field
    private Set<String> audienceHistory;
    @Field
    private Constant.GameType gameType;
    @Field
    private Constant.PlatformType platform = Constant.PlatformType.PLATFORM_JINLI;
    @Field
    private String roomId;
    @Field
    private String roomDisplayId;
    @Field
    private long bulletMessageCount;
    @Field
    private long liveVisitorCount;
    @Field
    private long connectedLiveCount;
    @Field
    private String loginDevice="0";
    @Field
    private String liveIp;
    @Field
    private int newFansNum;
    @Field
    private int fansNum;
    @Field
    private long totalLiveTime;
    @Field
    private long giftCount;


    public LiveRecord() {
    }

    public void setId(ObjectId id) {
        this.id = id;
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

    public long getLiveStartTime() {
        return liveStartTime;
    }

    public void setLiveStartTime(long liveStartTime) {
        this.liveStartTime = liveStartTime;
    }

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }

    public LiveRecord(String liveUserId,String roomId, String userId, long liveStartTime, long liveTime, long giftFlow, long gameFlow, int audienceNum, long recordTime,Constant.GameType gameType, Constant.PlatformType platform) {
        this.liveUserId = liveUserId;
        this.roomId = roomId;
        this.userId = userId;
        this.liveStartTime = liveStartTime;
        this.liveTime = liveTime;
        this.giftFlow = giftFlow;
        this.gameFlow = gameFlow;
        this.audienceNum = audienceNum;
        this.recordTime = recordTime;
        this.gameType = gameType;
        this.platform = platform;
    }

    public static LiveRecord newInstance(String liveUserId, String roomId,String userId, long liveStartTime, long liveTime, long giftFlow,
                                         long gameFlow, int audienceNum, Constant.GameType gameType , Constant.PlatformType platform){
        return new LiveRecord(liveUserId, roomId, userId, liveStartTime, liveTime, giftFlow, gameFlow, audienceNum, System.currentTimeMillis(),gameType,platform);
    }

    public String getId() {
        return id.toString();
    }

    public long getGiftFlow() {
        return giftFlow;
    }

    public int getAudienceNum() {
        return audienceNum;
    }

    public void setAudienceNum(int audienceNum) {
        this.audienceNum = audienceNum;
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
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

    public Set<String> getAudienceHistory() {
        return audienceHistory;
    }

    public void setAudienceHistory(Set<String> audienceHistory) {
        this.audienceHistory = audienceHistory;
    }

    public Constant.GameType getGameType() {
        return gameType;
    }

    public void setGameType(Constant.GameType gameType) {
        this.gameType = gameType;
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

    public String getLoginDevice() {
        return loginDevice;
    }

    public void setLoginDevice(String loginDevice) {
        this.loginDevice = loginDevice;
    }

    public String getLiveIp() {
        return liveIp;
    }

    public void setLiveIp(String liveIp) {
        this.liveIp = liveIp;
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

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public void setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
    }
}
