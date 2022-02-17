/*
package com.donglai.gate.app;

import com.donglai.gate.net.GateNetConfig;
import com.donglai.gate.net.WebSocketHandler;
import com.donglai.netty.http.NettyHttpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Configuration
public class GataConfiguration{

    @Bean
    @ConditionalOnMissingBean(NettyStarter.class)
    public NettyStarter nettyStarter() {
        return new NettyStarter();
    }

    @Component
    public static class NettyStarter implements InitializingBean, DisposableBean {

        public static final NettyHttpServer nettyHttpServer = new NettyHttpServer();

        @Autowired
        private GateNetConfig gnc;

        @Override
        public void destroy() throws Exception {
            nettyHttpServer.stop();
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            nettyHttpServer.bind(new WebSocketHandler(), gnc.getPort());
        }
    }
}
*/
