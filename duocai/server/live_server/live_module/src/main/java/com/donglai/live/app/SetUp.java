package com.donglai.live.app;

import com.donglai.live.process.InitServerDataProcess;
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
    InitServerDataProcess initServerDataProcess;

    @Override
    public void run(String... args) throws Exception {
        //1.初始化礼物排行榜队列
        initServerDataProcess.initRankQueue();
        //2.初始化每分钟定时任务
        initServerDataProcess.initMinuteJobQueue();
        //3.初始化直播域名
        initServerDataProcess.initLiveDomain();
        //4.关闭所有机器直播间，并重新模拟机器房间
        liveProcess.cleanAllRobotRoom();
        if (openOfficialLive) mockLiveRoom(Constant.PlatformType.DUOCAI);
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
