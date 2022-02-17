package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

public class LiveLimitRecordRequest {
    public int page;
    public int size;
    public String liveUserId;
    public int platform;
    public Integer startHour;
    public Long limitDate;
    public String displayName;

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

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }
    public Long getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(Long limitDate) {
        this.limitDate = limitDate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Constant.PlatformType getPlatformType() {
        return Constant.PlatformType.forNumber(platform);
    }
}
