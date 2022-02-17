package com.donglai.common.config.redis;

import com.alibaba.fastjson.parser.ParserConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {
    @Value("${spring.redis.password}")
    private String passowrd;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.nodes}")
    private String[] redisClusterNodes;

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

    @Bean
    @Primary
    @Profile("!prod")
    public RedisConnectionFactory masterRedisConnectionFactory() {
        String host = redisClusterNodes[0].split(":")[0];
        int port = Integer.parseInt(redisClusterNodes[0].split(":")[1]);
        return createJedisConnectionFactory(host, port, passowrd, database);
    }

    private LettuceConnectionFactory createJedisConnectionFactory(String host, int port, String password, int dataBase) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host,port);
        configuration.setDatabase(dataBase);
        configuration.setPassword(password);
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Primary
    @Profile("prod")
    public RedisConnectionFactory redisClusterFactory() {
        return redisClusterConfiguration();
    }

    private LettuceConnectionFactory redisClusterConfiguration() {
        log.info("init redis cluster Configuration");
        var clusterConfiguration = new RedisClusterConfiguration();
        var nodes = new ArrayList<RedisNode>();
        for (var nodeInfo : redisClusterNodes) {
            var ni = nodeInfo.split(":");
            nodes.add(new RedisNode(ni[0], Integer.parseInt(ni[1])));
        }
        clusterConfiguration.setClusterNodes(nodes);
        clusterConfiguration.setPassword(passowrd);
        return  new LettuceConnectionFactory(clusterConfiguration);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, RedisTemplate redisTemplate) {
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
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
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
