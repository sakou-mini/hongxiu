package com.donglaistd.jinli.http.dto.request;

import com.donglaistd.jinli.Constant;

public class LiveLimitListRequest {
    public int platform;
    public long limitDate;

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public long getLimitDate() {
        return limitDate;
    }

    public void setLimitDate(long limitDate) {
        this.limitDate = limitDate;
    }

    public Constant.PlatformType getPlatformType(){
        return Constant.PlatformType.forNumber(platform);
    }
}
