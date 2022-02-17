package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveRecord;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class DailyLiveRecordInfo {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String liveUserId;
    @Field
    private long liveTime;
    @Field
    private long income;
    @Field
    private int rank;
    @Field
    private int audienceNum;
    @Field
    private long time;
    @Field
    private long newFansNum;
    @Field
    private Constant.PlatformType platform;

    public DailyLiveRecordInfo() {
    }

    private DailyLiveRecordInfo(String liveUserId, long liveTime, long income, int rank, int audienceNum, long time, long newFansNum ,Constant.PlatformType platform ) {
        this.liveUserId = liveUserId;
        this.liveTime = liveTime;
        this.income = income;
        this.rank = rank;
        this.audienceNum = audienceNum;
        this.time = time;
        this.newFansNum = newFansNum;
        this.platform = platform;
    }

    public static DailyLiveRecordInfo newInstance(String liveUserId, long liveTime, long income, int rank, int audienceNum,long time,long newFansNum ,Constant.PlatformType platform) {
        return new DailyLiveRecordInfo(liveUserId, liveTime, income, rank, audienceNum,time,newFansNum,platform);
    }

    public static DailyLiveRecordInfo newInstance(LiveRecord liveRecord,long time, long newFansNum ,int rank) {
        return new DailyLiveRecordInfo(liveRecord.getLiveUserId(), liveRecord.getLiveTime(), liveRecord.getGiftFlow(), rank, liveRecord.getAudienceNum(),time,newFansNum,liveRecord.getPlatform());
    }

    public String getId() {
        return id.toString();
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

    public long getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(long liveTime) {
        this.liveTime = liveTime;
    }

    public long getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getAudienceNum() {
        return audienceNum;
    }

    public void setAudienceNum(int audienceNum) {
        this.audienceNum = audienceNum;
    }
    /*   public DailyLiveRecordInfo() {
    }*/

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getNewFansNum() {
        return newFansNum;
    }

    public void setNewFansNum(long newFansNum) {
        this.newFansNum = newFansNum;
    }

    public Constant.PlatformType getPlatform() {
        return platform;
    }

    public void setPlatform(Constant.PlatformType platform) {
        this.platform = platform;
    }
}
