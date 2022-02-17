package com.donglai.common.service.impl;

import com.donglai.common.exeception.UnableToAquireLockException;
import com.donglai.common.service.RedissonLockService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class RedissonLockServiceImpl implements RedissonLockService {
    private final static String LOCKER_PREFIX = "redissonLock::";
    private final static int LOCK_TIME = 100;

    @Autowired
    RedissonClient redissonClient;

    @Override
    public <T> T lock(String resourceName, Supplier<T> supplier) throws UnableToAquireLockException, Exception {
        return lock(resourceName, supplier, LOCK_TIME);
    }

    @Override
    public <T> T lock(String resourceName, Supplier<T> supplier, int lockTime) throws UnableToAquireLockException, Exception {
        RLock lock = redissonClient.getLock(LOCKER_PREFIX + resourceName);
        boolean success = lock.tryLock(LOCK_TIME, lockTime, TimeUnit.SECONDS);
        if (success) {
            try {
                return supplier.get();
            } finally {
                lock.unlock();
            }
        }
        throw new UnableToAquireLockException();
    }
}
