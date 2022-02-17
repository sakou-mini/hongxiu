package com.donglai.web.config.shiro;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ShiroRedisProperties {
    @Value("${spring.redis.shiro.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.database}")
    private int db;
    @Value("${spring.redis.nodes}")
    private String[] redisNodes;
}
