package com.donglai.common.config.redis;

import io.netty.util.internal.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RedissionConnector {
    @Value("${spring.redis.nodes}")
    private List<String> redisClusterNodes;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.password}")
    private String pwd;

    //单点模式
    @Profile("!prod")
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = String.format("redis://%s", redisClusterNodes.get(0));
        config.useSingleServer().setAddress(address).setDatabase(database)
                .setConnectionMinimumIdleSize(1);
        if (!StringUtil.isNullOrEmpty(pwd)) {
            config.useSingleServer().setPassword(pwd);
        }
        return Redisson.create(config);
    }

    //哨兵模式
    @Profile("prod")
    @Bean
    public RedissonClient prodRedissonClient() {
        Config config = new Config();
        List<String> addressList = new ArrayList<>();
        for (String redisClusterNode : redisClusterNodes) {
            addressList.add(String.format("redis://%s", redisClusterNode));
        }
        config.useSentinelServers().addSentinelAddress(addressList.toArray(new String[0])).setMasterName("myMaster")
                .setPassword(pwd).setDatabase(database);
        return Redisson.create(config);
    }
}
