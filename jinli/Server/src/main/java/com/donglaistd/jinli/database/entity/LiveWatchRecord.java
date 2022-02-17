package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class LiveWatchRecord {
    @Id
    private String id = ObjectId.get().toString();
    public String ip;
    public String roomLiveUserId;
    public Constant.MobilePhoneBrand phoneBrand;
    @Field
    private long watchTime;
    @Field
    private long recordTime;
    @Field
    private Constant.PlatformType platform;
    @Field
    private String roomDisplayId;
    private long enterRoomTime;
    private long quitRoomTime;
    private int giftCount;
    private int giftFlow;
    private int connectedLiveCount;
    private int bulletMessageCount;
    @Field
    @Indexed
    private String userId;
    @Field
    @Indexed
    private String roomId;


    public LiveWatchRecord(UserRoomRecord userRoomRecord, long quitRoomTime, Constant.MobilePhoneBrand brand, Constant.PlatformType platform, String roomDisplayId, String ip,String roomLiveUserId) {
        this.userId = userRoomRecord.getUserId();
        this.roomId = userRoomRecord.getRoomId();
        this.enterRoomTime = userRoomRecord.getEnterRoomTime();
        this.quitRoomTime = quitRoomTime;
        this.giftFlow = userRoomRecord.getGiftCoin();
        this.giftCount = userRoomRecord.getGiftCount();
        this.connectedLiveCount = userRoomRecord.getConnectedLiveCount();
        this.bulletMessageCount = userRoomRecord.getBulletMessageCount();
        this.phoneBrand = brand;
        this.watchTime = quitRoomTime - userRoomRecord.getEnterRoomTime();
        this.recordTime = System.currentTimeMillis();
        this.platform = platform;
        this.ip = ip;
        this.roomDisplayId = roomDisplayId;
        this.roomLiveUserId = roomLiveUserId;
    }

    public LiveWatchRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public long getWatchTime() {
        return watchTime;
    }

    public void setWatchTime(long watchTime) {
        this.watchTime = watchTime;
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

    public long getEnterRoomTime() {
        return enterRoomTime;
    }

    public void setEnterRoomTime(long enterRoomTime) {
        this.enterRoomTime = enterRoomTime;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public int getGiftFlow() {
        return giftFlow;
    }

    public void setGiftFlow(int giftFlow) {
        this.giftFlow = giftFlow;
    }

    public int getConnectedLiveCount() {
        return connectedLiveCount;
    }

    public void setConnectedLiveCount(int connectedLiveCount) {
        this.connectedLiveCount = connectedLiveCount;
    }

    public int getBulletMessageCount() {
        return bulletMessageCount;
    }

    public void setBulletMessageCount(int bulletMessageCount) {
        this.bulletMessageCount = bulletMessageCount;
    }

    public Constant.MobilePhoneBrand getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(Constant.MobilePhoneBrand phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getRoomDisplayId() {
        return roomDisplayId;
    }

    public void setRoomDisplayId(String roomDisplayId) {
        this.roomDisplayId = roomDisplayId;
    }

    public long getQuitRoomTime() {
        return quitRoomTime;
    }

    public void setQuitRoomTime(long quitRoomTime) {
        this.quitRoomTime = quitRoomTime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRoomLiveUserId() {
        return roomLiveUserId;
    }

    public void setRoomLiveUserId(String roomLiveUserId) {
        this.roomLiveUserId = roomLiveUserId;
    }
}
