package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

public class PlatformTimeRequest {
    public int platform;
    public Long startTime;
    public Long endTime;
    public int page;
    public int size;

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Constant.PlatformType getPlatformType(){
        return Constant.PlatformType.valueOf(platform);
    }
}
