package com.donglaistd.jinli.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CachingConfiguration {
    @Bean
//    @Profile("!prod")
    public CacheManager getNoOpCacheManager() {
        return new NoOpCacheManager();
    }

//    @Bean
//    @Profile("prod")
//    public CacheManager getRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(1));
//        CompositeCacheManager compositeCacheManager = new CompositeCacheManager();
//        ArrayList<CacheManager> cacheManagers = new ArrayList<>();
//        cacheManagers.add(RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)).cacheDefaults(redisCacheConfiguration).build());
//        compositeCacheManager.setCacheManagers(cacheManagers);
//        compositeCacheManager.setFallbackToNoOpCache(true);
//        return compositeCacheManager;
//    }
}
