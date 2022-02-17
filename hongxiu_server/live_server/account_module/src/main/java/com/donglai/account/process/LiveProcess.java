package com.donglai.account.process;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.LiveRedisConstant.getEnterRoomRecordKey;

@Component
public class LiveProcess {
    @Autowired
    public static final RedisService redisService = SpringApplicationContext.getBean(RedisService.class);

    public static String getUserEnterRoomRecord(String userId) {
        String roomRecordKey = getEnterRoomRecordKey(userId);
        return (String) redisService.get(roomRecordKey);
    }

}
