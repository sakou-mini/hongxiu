package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;

import java.util.List;

public class RoomInfo {
    public String avatarUrl;
    public String displayName;
    public String roomImage;
    public int audienceNum;
    public Constant.GameType gameType;
    public boolean hot;
    public String roomId;
    public String liveUserId;
    public String roomDisplayId;
    public String roomTitle;
    public List<Constant.PlatformType> sharedPlatform;
    public Constant.Pattern livePattern;

    public RoomInfo(Room room, User user) {
        this.avatarUrl = user.getAvatarUrl();
        this.displayName = user.getDisplayName();
        this.roomImage = room.getRoomImage();
        this.audienceNum = room.getAllPlatformAudienceList().size();
        this.hot = room.isHot();
        this.roomId = room.getId();
        this.roomDisplayId = room.getDisplayId();
        this.liveUserId = room.getLiveUserId();
        this.roomTitle = room.getRoomTitle();
        this.sharedPlatform = room.getAllSharedPlatform();
        this.livePattern = room.getPattern();
    }
}
