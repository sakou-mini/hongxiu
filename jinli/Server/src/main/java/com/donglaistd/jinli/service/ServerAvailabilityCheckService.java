package com.donglaistd.jinli.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.CacheNameConstant.LOCK_SERVER;

@Component
public class ServerAvailabilityCheckService {

    private final Logger logger = Logger.getLogger(ServerAvailabilityCheckService.class.getName());

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    private static final String SERVER_KEY = UUID.randomUUID().toString();

    private static final  long expire = 4000;

    private static final  long interval = 2000;

    public void startCheckServer(){
        checkServerActive();
    }


    private void checkServerActive(){
        boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_SERVER, SERVER_KEY, Duration.ofMillis(expire));
        if (!flag && Objects.equals(SERVER_KEY, stringRedisTemplate.opsForValue().get(LOCK_SERVER))) {
            stringRedisTemplate.opsForValue().set(LOCK_SERVER, SERVER_KEY, Duration.ofMillis(expire));
        }
        if(!isActive()){
            logger.info("server is not active! waiting get Lock");
        }
    }

    public boolean isActive(){
        String keyValue = stringRedisTemplate.opsForValue().get(LOCK_SERVER);
        if(keyValue == null) {
            return true;
        }
        return Objects.equals(SERVER_KEY, keyValue);
    }
}
