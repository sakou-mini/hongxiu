package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MuteProperty {
    private Constant.MuteIdentity muteOptIdentity;
    private String muteOptUserId;
    private Constant.MuteTimeType muteTimeType;
    private Constant.MuteReason muteReason;
    private String customReason;
    private long muteStartTime;
    private Constant.MuteArea muteArea;

    public long getMuteStartTime() {
        return muteStartTime;
    }

    public void setMuteStartTime(long muteStartTime) {
        this.muteStartTime = muteStartTime;
    }
    public Constant.MuteTimeType getMuteTimeType() {
        return muteTimeType;
    }

    public void setMuteTimeType(Constant.MuteTimeType muteTimeType) {
        this.muteTimeType = muteTimeType;
    }

    public Constant.MuteReason getMuteReason() {
        return muteReason;
    }

    public void setMuteReason(Constant.MuteReason muteReason) {
        this.muteReason = muteReason;
    }

    public String getCustomReason() {
        return customReason;
    }

    public void setCustomReason(String customReason) {
        this.customReason = customReason;
    }

    public MuteProperty(Constant.MuteTimeType muteTimeType, Constant.MuteReason muteReason, String customReason,Constant.MuteIdentity muteOptIdentity, String muteOptUserId,Constant.MuteArea muteArea) {
        this.muteStartTime = System.currentTimeMillis();
        this.muteOptIdentity = muteOptIdentity;
        this.muteOptUserId = muteOptUserId;
        this.muteTimeType = muteTimeType;
        this.muteReason = muteReason;
        this.customReason = customReason;
        this.muteArea = muteArea;
    }

    public static MuteProperty newInstance(Constant.MuteTimeType timeType, Constant.MuteReason muteReason , String customReason,
                                           Constant.MuteIdentity muteOptIdentity, String muteOptUserId, Constant.MuteArea muteArea) {
        return new MuteProperty(timeType,muteReason,customReason,muteOptIdentity,muteOptUserId, muteArea);
    }

    public MuteProperty() {
    }

    public Constant.MuteArea getMuteArea() {
        return muteArea;
    }

    public void setMuteArea(Constant.MuteArea muteArea) {
        this.muteArea = muteArea;
    }

    public long getMuteEndTime(){
        return muteStartTime + getMuteTimeByMuteTimeType();
    }

    public boolean isMute(){
        return (muteStartTime + getMuteTimeByMuteTimeType()) >= System.currentTimeMillis();
    }

    public Constant.MuteIdentity getMuteOptIdentity() {
        return muteOptIdentity;
    }

    public void setMuteOptIdentity(Constant.MuteIdentity muteOptIdentity) {
        this.muteOptIdentity = muteOptIdentity;
    }

    public String getMuteOptUserId() {
        return muteOptUserId;
    }

    public void setMuteOptUserId(String muteOptUserId) {
        this.muteOptUserId = muteOptUserId;
    }

    public long getMuteTimeByMuteTimeType(){
        switch (this.muteTimeType){
            case MUTE_FIVE_MINUTE:
                return TimeUnit.MINUTES.toMillis(5);
            case MUTE_HALF_HOUR:
                return TimeUnit.MINUTES.toMillis(30);
            case MUTE_ONE_DAY:
                return TimeUnit.DAYS.toMillis(1);
            case MUTE_THREE_DAY:
                return TimeUnit.DAYS.toMillis(3);
            case MUTE_ONE_WEEK:
                return TimeUnit.DAYS.toMillis(7);
            case MUTE_ONE_MONTH:
                return TimeUnit.DAYS.toMillis(30);
            case MUTE_ONE_YEAR:
                return TimeUnit.DAYS.toMillis(365);
            case MUTE_FOREVER:
            case MUTE_TIME_DEFAULT:
                return TimeUnit.DAYS.toMillis(365*99);
        }
        return 0;
    }

    public RoomManagement.MuteProperty toProto(){
        RoomManagement.MuteProperty.Builder builder = RoomManagement.MuteProperty.newBuilder()
                .setMuteEndTime(String.valueOf(getMuteEndTime()))
                .setMuteReason(muteReason).setMuteTime(muteTimeType);
        if(Objects.nonNull(muteOptIdentity))
            builder.setMuteOptIdentity(muteOptIdentity);
        if(Objects.nonNull(muteArea))
            builder.setMuteArea(muteArea);
        if(!StringUtils.isNullOrBlank(muteOptUserId)){
            builder.setMuteOptUserId(muteOptUserId);
        }
        return builder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MuteProperty that = (MuteProperty) o;
        return muteStartTime == that.muteStartTime &&
                muteOptIdentity == that.muteOptIdentity &&
                Objects.equals(muteOptUserId, that.muteOptUserId) &&
                muteTimeType == that.muteTimeType &&
                muteReason == that.muteReason &&
                Objects.equals(customReason, that.customReason) &&
                muteArea == that.muteArea;
    }

    @Override
    public int hashCode() {
        return Objects.hash(muteOptIdentity, muteOptUserId, muteTimeType, muteReason, customReason, muteStartTime, muteArea);
    }
}
