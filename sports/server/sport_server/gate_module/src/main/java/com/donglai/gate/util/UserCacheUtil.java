package com.donglai.gate.util;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.protocol.Constant;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Set;

import static com.donglai.common.constant.RedisConstant.ONLINE_USER;
import static com.donglai.common.constant.RedisConstant.getPlatformOnlineUserKey;

public class UserCacheUtil {

    private static final StringRedisTemplate redisTemplate = SpringApplicationContext.getBean(StringRedisTemplate.class);

    public static void addOnlineUser(String userId) {
        redisTemplate.opsForSet().add(ONLINE_USER, userId);
    }

    public static void removeOnlineUser(String userId){
        redisTemplate.opsForSet().remove(ONLINE_USER, userId);
    }

    public static Set<String> getAllOnlineUser() {
        return redisTemplate.opsForSet().members(ONLINE_USER);
    }

    public static void cleanAllOnlineUserInfo() {
        Set<String> keys = redisTemplate.keys(ONLINE_USER + "*");
        if(keys!=null) redisTemplate.delete(keys);

    }

}
