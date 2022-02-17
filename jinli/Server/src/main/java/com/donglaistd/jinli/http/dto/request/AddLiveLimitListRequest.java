package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

import java.util.Optional;

public class AddLiveLimitListRequest {
    public String liveUserId;
    public Long liveLimitDate;
    public Integer liveStartHour;
    public Integer liveEndHour;
    public boolean addWhiteList;
    public int platform;

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public long getLiveLimitDate() {
        return Optional.ofNullable(liveLimitDate).orElse(0L);
    }

    public void setLiveLimitDate(long liveLimitDate) {
        this.liveLimitDate = liveLimitDate;
    }

    public int getLiveStartHour() {
        return Optional.ofNullable(liveStartHour).orElse(-1);
    }

    public void setLiveStartHour(int liveStartHour) {
        this.liveStartHour = liveStartHour;
    }

    public int getLiveEndHour() {
        return Optional.ofNullable(liveEndHour).orElse(-1);
    }

    public void setLiveEndHour(int liveEndHour) {
        this.liveEndHour = liveEndHour;
    }

    public boolean isAddWhiteList() {
        return addWhiteList;
    }

    public void setAddWhiteList(boolean addWhiteList) {
        this.addWhiteList = addWhiteList;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public Constant.PlatformType getPlatformType() {
        return Constant.PlatformType.forNumber(platform);
    }
}
