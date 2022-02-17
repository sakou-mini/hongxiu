package com.donglaistd.jinli.task;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.RecommendDiaryDaoService;
import com.donglaistd.jinli.database.dao.ServerRunningRecordService;
import com.donglaistd.jinli.database.entity.ServerRunningRecord;
import com.donglaistd.jinli.service.DomainProcess;
import com.donglaistd.jinli.service.GiftRankService;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@EnableAsync
public class MinuteJob {
    private static final Logger logger = Logger.getLogger(MinuteJob.class.getName());
    @Autowired
    RecommendDiaryDaoService recommendDiaryDaoService;
    @Autowired
    ServerRunningRecordService serverRunningRecordService;
    @Autowired
    GiftRankService giftRankService;
    @Autowired
    DomainProcess domainProcess;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;

    @Async
    @Scheduled(cron = "${data.task.cron.minuteJob}")
    public void minuteJob() {
        if(!serverAvailabilityCheckService.isActive()) return;
        ServerRunningRecord record = serverRunningRecordService.findNearestLastServerRunningRecord();
        if(record == null) {
            record = ServerRunningRecord.newInstance();
        }
        record.setRecordTime(System.currentTimeMillis());
        serverRunningRecordService.save(record);
        if(recommendDiaryDaoService.deleteNotRecommendDiary()>0){
            logger.fine("remove not recommend diary record");
        }
        giftRankService.totalRank(Constant.RankType.CONTRIBUTION_RANK,20);
        giftRankService.totalRank(Constant.RankType.GIFT_RANK,20);
        domainProcess.checkDomainIsAvailable();
    }

}
