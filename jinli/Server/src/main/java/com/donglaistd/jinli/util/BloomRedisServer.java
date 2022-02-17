package com.donglaistd.jinli.util;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BloomRedisServer {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private BloomFilterHelper bloomFilterHelper;

    private String getBloomCountKey(String key){
        return key + "_count";
    }

    public <T> void addByDefaultBloomFilter(String key, T value) {
        addByBloomFilter(bloomFilterHelper, key, value);
    }

    public <T> void addByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelpernot null");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        if(!includeByBloomFilter(bloomFilterHelper,key,value)){
            redisTemplate.execute(new SessionCallback<>() {
                @Override
                public Object execute(RedisOperations operations) throws DataAccessException {
                    for (int i : offset) {
                        operations.opsForValue().setBit(key, i, true);
                    }
                    operations.opsForValue().increment(getBloomCountKey(key));
                    return true;
                }
            });
        }
    }

    public <T> boolean includeByDefaultBloomFilter(String key, T value) {
       return includeByBloomFilter(bloomFilterHelper, key, value);
    }

    public <T> boolean includeByBloomFilter(BloomFilterHelper<T> bloomFilterHelper, String key, T value) {
        Preconditions.checkArgument(bloomFilterHelper != null, "bloomFilterHelper not null");
        int[] offset = bloomFilterHelper.murmurHashOffset(value);
        for (int i : offset) {
            if (!redisTemplate.opsForValue().getBit(key, i)) {
                return false;
            }
        }
        return true;
    }

    public long getBloomCount(String key){
        Object result = redisTemplate.opsForValue().get(getBloomCountKey(key));
        if(Objects.isNull(result)) return 0;
        return Integer.parseInt(result.toString());
    }

    public void removeBloomFilter(String key){
        redisTemplate.delete(key);
        redisTemplate.delete(getBloomCountKey(key));
    }
}
