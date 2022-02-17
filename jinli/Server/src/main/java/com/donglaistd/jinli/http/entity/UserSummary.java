package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.database.entity.User;

public class UserSummary {
    public String displayName;
    public String userId;
    public String avatar;

    private UserSummary(String displayName, String userId, String avatar) {
        this.displayName = displayName;
        this.userId = userId;
        this.avatar = avatar;
    }

    public static UserSummary newInstance(User user){
        return new UserSummary(user.getDisplayName(), user.getPlatformShowUserId(), user.getAvatarUrl());
    }
}
