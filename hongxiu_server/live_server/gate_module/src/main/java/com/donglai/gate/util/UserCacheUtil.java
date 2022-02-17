package com.donglai.gate.util;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.protocol.Constant;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static com.donglai.common.constant.RedisConstant.ONLINE_USER;
import static com.donglai.common.constant.RedisConstant.getPlatformOnlineUserKey;

public class UserCacheUtil {

    private static final StringRedisTemplate redisTemplate = SpringApplicationContext.getBean(StringRedisTemplate.class);

    public static void addOnlineUser(String userId, Constant.PlatformType platformType) {
        if (platformType == null) return;
        String key = getPlatformOnlineUserKey(platformType);
        redisTemplate.opsForSet().add(key, userId);
    }

    public static void removeOnlineUser(String userId, Constant.PlatformType platformType) {
        if (platformType == null) return;
        String key = getPlatformOnlineUserKey(platformType);
        redisTemplate.opsForSet().remove(key, userId);
    }

    public static Set<String> getAllOnlineUser(Constant.PlatformType platformType) {
        return redisTemplate.opsForSet().members(ONLINE_USER);
    }

    public static void cleanAllOnlineUserInfo() {
        Set<String> keys = redisTemplate.keys(ONLINE_USER + "::*");
        if (keys != null) redisTemplate.delete(keys);

    }

}
