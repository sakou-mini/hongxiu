package com.donglaistd.jinli.http.dto.reply;

import com.donglaistd.jinli.database.entity.LiveWatchRecord;

import java.io.Serializable;

public class WatchLiveRecordReply implements Serializable {
    public LiveWatchRecord liveWatchRecord;
    public String userDisplayName;
    public String liveUserDisplayName;

    public WatchLiveRecordReply(LiveWatchRecord liveWatchRecord, String userDisplayName, String liveUserDisplayName) {
        this.liveWatchRecord = liveWatchRecord;
        this.userDisplayName = userDisplayName;
        this.liveUserDisplayName = liveUserDisplayName;
    }

    public LiveWatchRecord getLiveWatchRecord() {
        return liveWatchRecord;
    }

    public void setLiveWatchRecord(LiveWatchRecord liveWatchRecord) {
        this.liveWatchRecord = liveWatchRecord;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getLiveUserDisplayName() {
        return liveUserDisplayName;
    }

    public void setLiveUserDisplayName(String liveUserDisplayName) {
        this.liveUserDisplayName = liveUserDisplayName;
    }
}
