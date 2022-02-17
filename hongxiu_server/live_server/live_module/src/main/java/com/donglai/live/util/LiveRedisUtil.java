package com.donglai.live.util;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.util.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

import static com.donglai.common.constant.LiveRedisConstant.getEnterRoomRecordKey;
import static com.donglai.common.constant.LiveRedisConstant.getUserLastSendBulletKey;

public class LiveRedisUtil {

    private static final StringRedisTemplate redisTemplate = SpringApplicationContext.getBean(StringRedisTemplate.class);

    public static void saveUserSendBulletTime(String userId, long overTime) {
        String key = getUserLastSendBulletKey(userId);
        redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()), overTime, TimeUnit.MILLISECONDS);
    }

    public static long getUserLastSendBulletTime(String userId) {
        String key = getUserLastSendBulletKey(userId);
        String recordTime = redisTemplate.opsForValue().get(key);
        return StringUtils.isNullOrBlank(recordTime) ? 0 : Long.parseLong(recordTime);
    }

    public static void saveUserEnterRoomRecord(String userId, String roomId) {
        String key = getEnterRoomRecordKey(userId);
        redisTemplate.opsForValue().set(key, roomId);
    }

    public static String getUserEnterRoomRecord(String userId) {
        String key = getEnterRoomRecordKey(userId);
        return redisTemplate.opsForValue().get(key);
    }

    public static void cleanEnterRoomRecord(String userId) {
        String key = getEnterRoomRecordKey(userId);
        redisTemplate.delete(key);
    }
}
