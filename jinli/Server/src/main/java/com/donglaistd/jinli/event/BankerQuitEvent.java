package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.User;

public class BankerQuitEvent implements BaseEvent {
    private String roomId;
    private final User user;

    public BankerQuitEvent(User banker) {
        this.user = banker;
    }

    public BankerQuitEvent(String roomId, User user) {
        this.roomId = roomId;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getRoomId() {
        return roomId;
    }
}
