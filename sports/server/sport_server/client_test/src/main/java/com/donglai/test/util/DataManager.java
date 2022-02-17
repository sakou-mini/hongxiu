package com.donglai.test.util;

import com.donglai.test.entity.UserCache;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class DataManager {
    private static Set<Channel> channels = new CopyOnWriteArraySet<>();
    private static Map<String, UserCache> userCacheMap = new ConcurrentHashMap<>();

    public static UserCache getUserCache(Channel channel) {
        return userCacheMap.get(channel.id().asLongText());
    }

    public static void saveUserCache(UserCache userCache, Channel channel) {
        userCacheMap.put(channel.id().asLongText(), userCache);
    }
}
