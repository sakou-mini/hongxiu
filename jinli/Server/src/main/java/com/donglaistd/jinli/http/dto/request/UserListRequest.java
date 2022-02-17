package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.data.domain.PageRequest;

import java.util.Objects;

public class UserListRequest {
    public int platform;
    public Long startTime;
    public Long endTime;
    public String displayName;
    public String userId;
    public Integer page;
    public Integer size;
    public Long startOfLiveWatchTime;
    public Long endOfLiveWatchTime;
    public int statue;
    public String ip;

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public Long getStartTime() {
        if(Objects.nonNull(startTime)) startTime = TimeUtil.getTimeDayStartTime(startTime);
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        if(Objects.nonNull(endTime)) endTime = TimeUtil.getDayEndTime(endTime);
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
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

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Constant.PlatformType getPlatformType(){
        return Constant.PlatformType.forNumber(platform);
    }

    public Long getStartOfLiveWatchTime() {
        return startOfLiveWatchTime;
    }

    public void setStartOfLiveWatchTime(Long startOfLiveWatchTime) {
        this.startOfLiveWatchTime = startOfLiveWatchTime;
    }

    public Long getEndOfLiveWatchTime() {
        return endOfLiveWatchTime;
    }

    public void setEndOfLiveWatchTime(Long endOfLiveWatchTime) {
        this.endOfLiveWatchTime = endOfLiveWatchTime;
    }

    public int getStatue() {
        return statue;
    }

    public void setStatue(int statue) {
        this.statue = statue;
    }

    public Constant.AccountStatue getStatueType() {
        return Constant.AccountStatue.forNumber(statue);
    }

    public PageRequest getPageRequest(){
        if(Objects.isNull(page) || Objects.isNull(size)) return null;
        return PageRequest.of(page, size);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
