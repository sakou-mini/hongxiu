package com.donglai.common.config.redis;

import com.alibaba.fastjson.parser.ParserConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String passowrd;
    @Value("${spring.redis.database}")
    private int database;

    static {
        ParserConfig.getGlobalInstance().addAccept("com.donglai.common.db.entity");
        ParserConfig.getGlobalInstance().addAccept("com.donglai.live.db.entity");
        ParserConfig.getGlobalInstance().addAccept("com.donglai.blogs.db.entity");
        ParserConfig.getGlobalInstance().addAccept("com.donglai.model.db.entity");
        ParserConfig.getGlobalInstance().addAccept("com.donglai.web.db.backoffice.entity");
        ParserConfig.getGlobalInstance().addAccept("com.donglai.web.db.server.entity");
        ParserConfig.getGlobalInstance().addAccept("com.donglai.web.web.dto");
    }

    @Bean
    public KeyGenerator wiselyKeyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getName());
            sb.append(method.getName());
            for (Object obj : params) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    @Primary
    @Bean("masterRedisConnectionFactory")
    public RedisConnectionFactory masterRedisConnectionFactory() {
        return createJedisConnectionFactory(host, port, passowrd, database);
    }

    public LettuceConnectionFactory createJedisConnectionFactory(String host, int port, String password, int dataBase) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setDatabase(dataBase);
        configuration.setPassword(password);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    public CacheManager cacheManager(@Qualifier("masterRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory, RedisTemplate redisTemplate) {
        Map<String, RedisCacheConfiguration> cacheNamesConfigurationMap = new HashMap<>();
        final RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(
                        RedisCacheConfiguration
                                .defaultCacheConfig()
                                .serializeValuesWith(
                                        RedisSerializationContext
                                                .SerializationPair
                                                .fromSerializer(redisTemplate.getValueSerializer())
                                ))
                .withInitialCacheConfigurations(cacheNamesConfigurationMap);
        return builder.build();
    }

    @Primary
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("masterRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        // 使用FASTJSON序列化
        MyFastJsonRedisSerializer myFastJsonRedisSerializer = new MyFastJsonRedisSerializer<>(Object.class);
        // key的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        // value值的序列化采用fastJsonRedisSerializer
        template.setValueSerializer(myFastJsonRedisSerializer);
        template.setHashValueSerializer(myFastJsonRedisSerializer);
        return template;
    }
}
