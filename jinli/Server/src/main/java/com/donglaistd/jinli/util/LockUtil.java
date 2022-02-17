package com.donglaistd.jinli.util;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class LockUtil
{
    private static Logger logger = Logger.getLogger(LockUtil.class.getName());
    private static final String LOCK_PRE = "lock::";
    private static final int expire = 200;
    private static final long defaultRetryCount = 50;
    private static final long defaultInterval = 5;

    public static<T> T  runInLock(String lockName, RedisTemplate<String,Object> redisTemplate, Supplier<T> supplier)
    {
        String identity = UUID.randomUUID().toString();
        if ( lock(lockName,identity,redisTemplate))
        {
            try
            {
                return supplier.get();
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
            }
            finally
            {
                releaseLock(lockName, identity, redisTemplate);
            }
        }
        return null;
    }

    public static boolean runInLock(String lockName, int retryCount, int interval, RedisTemplate<String,Object> redisTemplate, Supplier<Boolean> supplier)
    {
        String identity = UUID.randomUUID().toString();
        boolean result = lock(lockName, identity,retryCount,interval,redisTemplate);
        if (result)
        {
            try
            {
                result = supplier.get();
            }
            catch (Throwable throwable)
            {
                throwable.printStackTrace();
                result = false;
            }
            finally
            {
                releaseLock(lockName, identity, redisTemplate);
            }
        }
        logger.warning("  get lock failed !");
        return result;
    }



    private static boolean lock(String lockName, String identity, RedisTemplate<String,Object> redisTemplate)
    {
        return lock(lockName, identity, defaultRetryCount, defaultInterval, redisTemplate);
    }

    private static boolean lock(String lockName, String identity, long retryCount, long interval, RedisTemplate<String,Object> redisTemplate)
    {
        retryCount = Math.max(retryCount, 1);
        interval = Math.max(interval, 5);
        boolean flag = false;
        for (; retryCount >= 0; retryCount--)
        {
            flag = redisTemplate.opsForValue().setIfAbsent(LOCK_PRE + lockName, identity, Duration.ofMillis(expire));
            if (flag) break;
            try
            {
                TimeUnit.MILLISECONDS.sleep(interval);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static void releaseLock(String lockName, String identity,  RedisTemplate<String,Object> redisTemplate)
    {
        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String key = LOCK_PRE + lockName;
                operations.watch(key);
                if (identity.equals(operations.opsForValue().get(key))) {
                    operations.multi();
                    operations.delete(key);
                    operations.exec();
                    return true;
                }
                return false;
            }
        });
    }
}
