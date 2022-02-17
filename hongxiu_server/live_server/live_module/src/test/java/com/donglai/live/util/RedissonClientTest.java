/*
package com.donglai.live.util;

import com.donglai.common.service.RedisService;
import com.donglai.common.service.RedissonLockService;
import com.donglai.live.BaseTest;
import com.donglai.live.app.MyThreadPool;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;

public class RedissonClientTest extends BaseTest {
    @Autowired
    RedisService redisService;
    @Autowired
    RedissonLockService redissonLockService;

    public RedissonClient getClient() {
        Config config = new Config();
        //config.useClusterServers().addNodeAddress("redis://127.0.0.1:6379").
        config.useSingleServer().setAddress("redis://127.0.0.1:6379").setDatabase(3);
        return Redisson.create(config);
    }

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            MyThreadPool.getbgThread().execute(() -> {
                try {
                    redissonLockService.lock("number", () -> {
                        Integer number = (Integer) redisService.get("number");
                        if (number == null) {
                            number = 1;
                        } else {
                            number += 1;
                        }
                        redisService.set("number", number);
                        System.out.println(Thread.currentThread().getName() + "---->result:" + number);
                        return null;
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        Thread.sleep(10000);
        Assert.assertEquals(1000, redisService.get("number"));
    }


    @Test
    public void test2() {
        //配置redis数据
        RedissonClient redissonClient = getClient();
        //通过redisson获取redlock
        RLock lock = redissonClient.getLock("redlock");

        for (int i = 0; i < 100; i++) {
            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    //加锁
                    lock.lock();
                    try {
                        Integer number = (Integer) redisService.get("number");
                        if (number == null) {
                            number = 1;
                        } else {
                            number += 1;
                        }
                        redisService.set("number", number);
                        System.out.println(Thread.currentThread().getName() + "---->result:" + number);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                }
            });
            thread1.start();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(100, redisService.get("number"));

    }
}
*/
