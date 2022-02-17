package com.donglaistd.jinli.http.entity.live;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveRecord;
import com.donglaistd.jinli.database.entity.User;

public class LiveRecordInfo {
    public String userId;
    public String displayName;
    public String avatar;
    public LiveRecord liveRecord;
    public long totalLiveTime;
    public long totalIncome;
    public long totalGameFlow;
    public Constant.PlatformType platform;
    public long newFans;

    private LiveRecordInfo(User user, LiveRecord liveRecord, long totalLiveTime, long totalIncome,long totalGameFlow) {
        this.userId = user.getId();
        this.displayName = user.getDisplayName();
        this.avatar = user.getAvatarUrl();
        this.liveRecord = liveRecord;
        this.totalLiveTime = totalLiveTime;
        this.totalIncome = totalIncome;
        this.totalGameFlow = totalGameFlow;
    }

    private LiveRecordInfo(User user, LiveRecord liveRecord) {
        this.userId = user.getId();
        this.displayName = user.getDisplayName();
        this.avatar = user.getAvatarUrl();
        this.liveRecord = liveRecord;
    }


    public static LiveRecordInfo newInstance(User user, LiveRecord liveRecord, long totalLiveTime, long totalIncome, long totalGameFlow){
        return new LiveRecordInfo(user, liveRecord, totalLiveTime, totalIncome,totalGameFlow);
    }

    public static LiveRecordInfo newInstance(User user, LiveRecord liveRecord){
        return new LiveRecordInfo(user, liveRecord);
    }
}
