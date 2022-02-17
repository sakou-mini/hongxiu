package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DailyBetInfoDaoServiceTest extends BaseTest {
    @Autowired
    DailyBetInfoDaoService dailyBetInfoDaoService;

    @Test
    public void test(){
        long startTime = TimeUtil.getCurrentDayStartTime();
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), "1", System.currentTimeMillis(), 500));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), "2", System.currentTimeMillis(), 600));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), "3", System.currentTimeMillis(), 700));
        DailyBetInfo totalInfo = dailyBetInfoDaoService.findTotalBetInfoByTimes(startTime, System.currentTimeMillis());
        Assert.assertEquals(1800,totalInfo.getBetAmount());
        Assert.assertEquals(3,totalInfo.getWin());
    }

}
