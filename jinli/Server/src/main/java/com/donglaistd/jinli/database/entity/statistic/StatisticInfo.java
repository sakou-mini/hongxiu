package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
@Document(collection = "statisticInfo")
public class StatisticInfo implements Serializable {
    @Id
    private String id;
    private String liveUserId;
    private long time;
    private int value;
    private Constant.StatisticType type;

    public StatisticInfo(String liveUserId, long time, int value, Constant.StatisticType type) {
        this.liveUserId = liveUserId;
        this.time = time;
        this.value = value;
        this.type = type;
    }

    public StatisticInfo() {
    }

    @Override
    public String toString() {
        return "StatisticInfo{" +
                "id='" + id + '\'' +
                ", userId=" + liveUserId +
                ", time=" + time +
                ", value=" + value +
                ", type=" + type +
                '}';
    }

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Constant.StatisticType getType() {
        return type;
    }

    public void setType(Constant.StatisticType type) {
        this.type = type;
    }

    public Jinli.StatisticCurve toProto(){
        return Jinli.StatisticCurve.newBuilder().setTime(getTime()).setValue(getValue()).build();
    }
}
