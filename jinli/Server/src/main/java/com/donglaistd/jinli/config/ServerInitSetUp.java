package com.donglaistd.jinli.config;

import com.donglaistd.jinli.database.dao.LiveRecordDaoService;
import com.donglaistd.jinli.database.dao.LiveWatchRecordDaoService;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.dao.statistic.DailyUserActiveRecordDaoService;
import com.donglaistd.jinli.database.dao.statistic.LiveSourceLineConfigDaoService;
import com.donglaistd.jinli.database.dao.system.GameServerConfigDaoService;
import com.donglaistd.jinli.database.dao.system.GiftConfigDaoService;
import com.donglaistd.jinli.database.dao.system.domainConfig.DomainConfigDaosService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.http.service.OfficialLiveService;
import com.donglaistd.jinli.service.DomainProcess;
import com.donglaistd.jinli.service.PlatformLiveService;
import com.donglaistd.jinli.service.PlatformProcess;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import com.donglaistd.jinli.task.timewheel.TimerQueueProcess;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;

@Component
public class ServerInitSetUp implements CommandLineRunner {
    Logger logger = Logger.getLogger(ServerInitSetUp.class.getName());
    @Autowired
    private  OfficialLiveService officialLiveService;
    @Autowired
    private GameServerConfigDaoService gameServerConfigDaoService;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    @Autowired
    PlatformLiveService plantLiveService;
    @Autowired
    GiftConfigDaoService giftConfigDaoService;
    @Autowired
    PlatformProcess platformProcess;
    @Autowired
    LiveRecordDaoService liveRecordDaoService;

    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Autowired
    LiveSourceLineConfigDaoService liveSourceLineConfigDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    DomainProcess domainProcess;
    @Autowired
    DailyUserActiveRecordDaoService dailyUserActiveRecordDaoService;

    public void closeOfficialLive(){
        officialLiveService.closeNotExitOfficialLive();
    }

    public void initOfficialLiveByPlatformT() {
      List<LiveUser> liveUsers = plantLiveService.initPlatformLiveT(1);
      logger.info("init platform t live: size " + liveUsers.size());
    }

    public void initOfficialLiveByPlatformQ() {
        List<LiveUser> liveUsers = plantLiveService.initPlatformLiveQ(1);
        logger.info("init platform q live: size " + liveUsers.size());
    }

    @Autowired
    DomainConfigDaosService domainConfigDaosService;

    @Override
    public void run(String... args){
        dataManager.clearRedisAll();
        closeOfficialLive();
        gameServerConfigDaoService.initGameServer();
        //uploadServerService.initConnected();
        initOfficialLiveByPlatformT();
        initOfficialLiveByPlatformQ();
        serverAvailabilityCheckService.startCheckServer();
        TimerQueueProcess.init();
        giftConfigDaoService.initGiftConfig();
        //liveDomainConfigDaoService.initDomainConfig();
        //platformProcess.resetLiveUserCoinAndIllegalityGiftLogByPlatform(Constant.PlatformType.PLATFORM_T);
        liveSourceLineConfigDaoService.initLiveSourceLineConfig();
        //更新玩家观看直播时长
        //userAttributeDaoService.updateAllUserWatchLiveTimeInfo();
        cleanErrorData();
        //domainProcess.initPlatformDomain();
        logger.info("start JinliServer SUCCESS!  -------------------->");
    }

    public void cleanErrorData(){
        domainConfigDaosService.deleteEmptyPlatformDomainConfig();
        liveWatchRecordDaoService.cleanErrorData();
        dailyUserActiveRecordDaoService.deleteAllIfUserIfIsNull();
    }



}
