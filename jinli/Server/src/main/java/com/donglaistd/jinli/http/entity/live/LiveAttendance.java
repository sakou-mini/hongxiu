package com.donglaistd.jinli.http.entity.live;

import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.DailyLiveRecordInfo;
import com.donglaistd.jinli.http.entity.UserSummary;

public class LiveAttendance {
    public UserSummary userSummary;
    public DailyLiveRecordInfo liveInfo;

    public LiveAttendance(UserSummary userSummary, DailyLiveRecordInfo liveInfo) {
        this.userSummary = userSummary;
        this.liveInfo = liveInfo;
    }

    public  static LiveAttendance newInstance (UserSummary userSummary, DailyLiveRecordInfo liveInfo) {
        return new LiveAttendance(userSummary, liveInfo);
    }

    public  static LiveAttendance newInstance (User user, DailyLiveRecordInfo liveInfo) {
        return new LiveAttendance(UserSummary.newInstance(user), liveInfo);
    }

    public UserSummary getUserSummary() {
        return userSummary;
    }

    public void setUserSummary(UserSummary userSummary) {
        this.userSummary = userSummary;
    }

    public DailyLiveRecordInfo getLiveInfo() {
        return liveInfo;
    }

    public void setLiveInfo(DailyLiveRecordInfo liveInfo) {
        this.liveInfo = liveInfo;
    }
}
