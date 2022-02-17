package com.donglaistd.jinli.database.entity.plant;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Document
public class PlatformRechargeRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String userId;
    @Field
    private String platformAccountUserId;
    @Field
    private String platformAccountDisplayName;
    @Field
    private long platformAccountRechargeAmount;
    @Field
    private long rechargeGameCoin;
    @Field
    private long rechargeTime;
    @Field
    private long leftGameCoin;
    @Field
    private long totalRechargeAmount;

    public PlatformRechargeRecord() {
    }

    public PlatformRechargeRecord(String userId, String platformAccountUserId, String platformAccountDisplayName, long platformAccountRechargeAmount, long rechargeGameCoin,
                                  long leftGameCoin, long totalRechargeAmount) {
        this.userId = userId;
        this.platformAccountUserId = platformAccountUserId;
        this.platformAccountDisplayName = platformAccountDisplayName;
        this.platformAccountRechargeAmount = platformAccountRechargeAmount;
        this.rechargeGameCoin = rechargeGameCoin;
        this.rechargeTime = System.currentTimeMillis();
        this.leftGameCoin = leftGameCoin;
        this.totalRechargeAmount = totalRechargeAmount;
    }

    public static PlatformRechargeRecord newInstance(String userId, String platformAccountUserId, String platformAccountDisPlayName,
                                                     long platformAccountRechargeAmount, long rechargeGameCoin, long leftGameCoin, long totalRechargeAmount) {
        return new PlatformRechargeRecord(userId, platformAccountUserId, platformAccountDisPlayName, platformAccountRechargeAmount, rechargeGameCoin,leftGameCoin,totalRechargeAmount);
    }

    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlatformAccountUserId() {
        return platformAccountUserId;
    }

    public void setPlatformAccountUserId(String platformAccountUserId) {
        this.platformAccountUserId = platformAccountUserId;
    }

    public String getPlatformAccountDisplayName() {
        return platformAccountDisplayName;
    }

    public void setPlatformAccountDisplayName(String platformAccountDisplayName) {
        this.platformAccountDisplayName = platformAccountDisplayName;
    }

    public long getPlatformAccountRechargeAmount() {
        return platformAccountRechargeAmount;
    }

    public void setPlatformAccountRechargeAmount(long platformAccountRechargeAmount) {
        this.platformAccountRechargeAmount = platformAccountRechargeAmount;
    }

    public long getRechargeGameCoin() {
        return rechargeGameCoin;
    }

    public void setRechargeGameCoin(long rechargeGameCoin) {
        this.rechargeGameCoin = rechargeGameCoin;
    }

    public long getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(long rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public long getLeftGameCoin() {
        return leftGameCoin;
    }

    public void setLeftGameCoin(long leftGameCoin) {
        this.leftGameCoin = leftGameCoin;
    }

    public long getTotalRechargeAmount() {
        return totalRechargeAmount;
    }

    public void setTotalRechargeAmount(long totalRechargeAmount) {
        this.totalRechargeAmount = totalRechargeAmount;
    }

    @Override
    public String toString() {
        return "PlatformRechargeRecord{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", platformAccountUserId='" + platformAccountUserId + '\'' +
                ", platformAccountDisplayName='" + platformAccountDisplayName + '\'' +
                ", platformAccountRechargeAmount=" + platformAccountRechargeAmount +
                ", rechargeGameCoin=" + rechargeGameCoin +
                ", rechargeTime=" + rechargeTime +
                ", leftGameCoin=" + leftGameCoin +
                ", totalRechargeAmount=" + totalRechargeAmount +
                '}';
    }
}
