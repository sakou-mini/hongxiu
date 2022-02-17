package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Document
public class UserAttribute {
    @Id
    private String userId;
    @Field
    private Constant.AccountStatue statue;
    @Field
    private long time;
    @Field
    private boolean platformMute = false;
    @Field
    private long watchLiveCount;
    @Field
    private long watchLiveTime;
    @Field
    private Set<String> ipHistory = new HashSet<>();

    private UserAttribute(String userId, Constant.AccountStatue statue) {
        this.userId = userId;
        this.statue = statue;
    }

    public static UserAttribute newInstance(String userId, Constant.AccountStatue statue){
        return new UserAttribute(userId, statue);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Constant.AccountStatue getStatue() {
        return statue;
    }

    public void setStatue(Constant.AccountStatue statue) {
        this.statue = statue;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isPlatformMute() {
        return platformMute;
    }

    public void setPlatformMute(boolean platformMute) {
        this.platformMute = platformMute;
    }

    public long getWatchLiveCount() {
        return watchLiveCount;
    }

    public void setWatchLiveCount(long watchLiveCount) {
        this.watchLiveCount = watchLiveCount;
    }

    public long getWatchLiveTime() {
        return watchLiveTime;
    }

    public void setWatchLiveTime(long watchLiveTime) {
        this.watchLiveTime = watchLiveTime;
    }

    public void setIpHistory(Set<String> ipHistory) {
        this.ipHistory = ipHistory;
    }

    public boolean addIpHistory(String ip){
        if(!StringUtils.isNullOrBlank(ip))
            return ipHistory.add(ip);
        return false;
    }

    public Set<String> getIpHistory() {
        return ipHistory;
    }

    @Override
    public String toString() {
        return "UserAttribute{" +
                "userId='" + userId + '\'' +
                ", statue=" + statue +
                '}';
    }
}
