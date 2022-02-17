package com.donglaistd.jinli.task;

import com.donglaistd.jinli.database.dao.CoinFlowDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.service.GiftRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
@EnableAsync
public class HourJob {

    @Value("${data.rank.size}")
    private int SIZE;
    @Autowired
    CoinFlowDaoService coinFlowDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    GiftRankService giftRankService;

    @Async
    @Scheduled(cron = "${data.task.cron.halfHourJob}")
    public void halfHourJob(){
        //giftRankService.totalRank(Constant.RankType.GIFT_RANK,SIZE);
    }


    @Async
    @Scheduled(cron = "${data.task.cron.ten.minuteJob}")
    public void tenMinuteJob() {
        //giftRankService.totalRank(Constant.RankType.CONTRIBUTION_RANK,SIZE);
    }

}
