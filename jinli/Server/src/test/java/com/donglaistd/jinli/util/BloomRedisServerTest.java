package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;
import java.util.logging.Logger;

public class BloomRedisServerTest extends BaseTest {
    private final Logger logger = Logger.getLogger(BloomRedisServerTest.class.getName());
    @Autowired
    BloomRedisServer bloomRedisServer;
    @Autowired
    RedisTemplate<String, Object> commonRedisTemplate;

    @Test
    public void addToBloomTest(){
        String key = "bloomKey1";
        for (int i = 0; i < 1000 ; i++) {
            String value = UUID.randomUUID().toString();
            if(!bloomRedisServer.includeByDefaultBloomFilter(key,value)){
                bloomRedisServer.addByDefaultBloomFilter(key,value);
            }else{
                logger.warning("add failed!");
            }
        }
        logger.warning("total num is:"+bloomRedisServer.getBloomCount(key));
        Assert.assertEquals(1000,bloomRedisServer.getBloomCount(key));
    }

    @Test
    public void checkIsContainTest(){
        String key = "bloomKey2";
        for (int i = 0; i < 500 ; i++) {
            String value = String.valueOf(i);
            if(!bloomRedisServer.includeByDefaultBloomFilter(key,value)){
                bloomRedisServer.addByDefaultBloomFilter(key,value);
            }else{
                logger.warning("add failed!");
            }
        }
        for (int i = 0; i <100 ; i++) {
            String value = String.valueOf(i);
            Assert.assertTrue(bloomRedisServer.includeByDefaultBloomFilter(key,value));
        }
        Assert.assertEquals(500,bloomRedisServer.getBloomCount(key));
    }
}
