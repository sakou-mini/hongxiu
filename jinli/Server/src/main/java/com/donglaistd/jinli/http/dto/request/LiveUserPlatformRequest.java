package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

import java.util.HashSet;
import java.util.Set;

public class LiveUserPlatformRequest {
    public String liveUserId;
    public Set<Constant.PlatformType> sharedPlatform = new HashSet<>();

    public String getLiveUserId() {
        return liveUserId;
    }

    public void setLiveUserId(String liveUserId) {
        this.liveUserId = liveUserId;
    }

    public Set<Constant.PlatformType> getSharedPlatform() {
        return sharedPlatform;
    }

    public void setSharedPlatform(Set<Constant.PlatformType> sharedPlatform) {
        this.sharedPlatform = sharedPlatform;
    }
}
