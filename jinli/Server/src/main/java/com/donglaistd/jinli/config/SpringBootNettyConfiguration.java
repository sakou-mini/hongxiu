package com.donglaistd.jinli.config;

import com.donglaistd.jinli.netty.server.NettyServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.Resource;
import java.util.logging.Logger;

@Configuration
@PropertySources({
        @PropertySource("classpath:game_config.properties"),
        @PropertySource("classpath:misc_config.properties"),
        @PropertySource("classpath:backOffice_config.properties"),
        @PropertySource("classpath:live.properties"),
        @PropertySource("classpath:security-config.properties")
})
@EnableConfigurationProperties(SpringBootNettyProperties.class)
@EnableWebMvc
public class SpringBootNettyConfiguration {
    private static final Logger logger = Logger.getLogger(SpringBootNettyConfiguration.class.getName());

    @Autowired
    private SpringBootNettyProperties springBootNettyProperties;

    @Bean
    @ConditionalOnMissingBean(NettyStarter.class)
    public NettyStarter nettyStarter() {
        return new NettyStarter();
    }

    @Bean
    @ConditionalOnMissingBean(SpringContext.class)
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Component
    public static class NettyStarter implements InitializingBean, DisposableBean {

        @Resource
        private SpringBootNettyProperties springBootNettyProperties;

        @Autowired
        private NettyServer nettyServer;

        @Override
        public void afterPropertiesSet() {
            logger.info("Starting The Netty Server");
            nettyServer.start(springBootNettyProperties);
            logger.info("Started The Netty Server");
        }

        @Override
        public void destroy() {
            logger.info("Stopping The Netty Server");
            nettyServer.stop();
            logger.info("Stopped The Netty Server");
        }
    }
}
