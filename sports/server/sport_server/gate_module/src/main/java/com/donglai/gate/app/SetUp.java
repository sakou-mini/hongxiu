package com.donglai.gate.app;

import com.donglai.gate.util.UserCacheUtil;
import com.donglai.gate.net.GateNetConfig;
import com.donglai.gate.net.WebSocketHandler;
import com.donglai.netty.http.NettyHttpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SetUp implements CommandLineRunner {

    @Autowired
    private GateNetConfig gnc;


    @Override
    @Async
    public void run(String... args) throws Exception {
        //clean online userId cache
        UserCacheUtil.cleanAllOnlineUserInfo();
        startListener();
    }

    public void startListener() {
        log.info("start netty by port :{}",gnc.getPort());
        new NettyHttpServer().bind(new WebSocketHandler(), gnc.getPort()); // tcp 服务
    }
}
