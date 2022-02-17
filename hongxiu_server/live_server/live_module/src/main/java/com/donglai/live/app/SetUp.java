package com.donglai.live.app;

import com.donglai.live.process.LiveProcess;
import com.donglai.live.util.MockDataUtil;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
public class SetUp implements CommandLineRunner {
    @Value("${official.room.open}")
    public boolean openOfficialLive;
    @Autowired
    MockDataUtil mockDataUtil;
    @Autowired
    LiveProcess liveProcess;
    @Autowired
    InitServerData initServerData;

    @Override
    public void run(String... args) throws Exception {
        initServerData.initRankQueue();
        initServerData.initLiveDomain();
        liveProcess.cleanAllRobotRoom();
        if (openOfficialLive) mockLiveRoom(Constant.PlatformType.HONG_XIU);
    }

    public void mockLiveRoom(Constant.PlatformType platform) {
        liveProcess.cleanAllRobotRoom();
        int accountId = 10000000;
        for (Constant.LiveTag tag : Constant.LiveTag.values()) {
            if (tag.equals(Constant.LiveTag.UNRECOGNIZED)) continue;
            mockDataUtil.createRobotLiveRoom(String.valueOf(accountId), tag.name(), tag, platform);
            accountId++;
        }
    }
}
