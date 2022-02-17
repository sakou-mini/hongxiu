package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;

public class OfficialLiveUserReturn {
    public Constant.ResultCode resultCode;

    public LiveUser liveUser;

    public Room room;

    public OfficialLiveUserReturn(Constant.ResultCode resultCode) {
        this.resultCode = resultCode;
    }

    public OfficialLiveUserReturn() {
    }

    public OfficialLiveUserReturn(Constant.ResultCode resultCode, LiveUser liveUser, Room room) {
        this.resultCode = resultCode;
        this.liveUser = liveUser;
        this.room = room;
    }
}
