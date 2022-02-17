package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.User;
import io.netty.channel.Channel;

public class UserDisconnectEvent implements BaseEvent {
    private final User user;
    private final Channel channel;

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public UserDisconnectEvent(User user, Channel channel) {
        this.user = user;
        this.channel = channel;
    }
}
