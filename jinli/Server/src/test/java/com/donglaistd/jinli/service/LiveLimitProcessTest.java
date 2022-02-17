package com.donglaistd.jinli.service;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.LiveLimitListDaoService;
import com.donglaistd.jinli.database.entity.LiveLimitList;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class LiveLimitProcessTest extends BaseTest {
    @Autowired
    LiveLimitProcess liveLimitProcess;
    @Autowired
    LiveLimitListDaoService liveLimitListDaoService;

    @Test
    public void  addLiveLimitTest(){
        LiveLimitList liveLimitList = new LiveLimitList(System.currentTimeMillis(), liveUser.getPlatformType());
        for (int i = 0; i < 24; i++) {
            liveLimitList.addClockWhiteList(i,liveUser.getId());
        }
        liveLimitListDaoService.save(liveLimitList);
        long lastLiveUserLimitList = liveLimitProcess.getLastLiveLimitTimeListByLiveUser(liveUser.getId(), liveUser.getPlatformType());
        long expectedTime = TimeUtil.getCurrentDayStartTime() + TimeUnit.HOURS.toMillis(24) - 1;
        Assert.assertEquals(lastLiveUserLimitList,expectedTime);

        LiveLimitList liveLimitList2 = new LiveLimitList(TimeUtil.getAfterDayStartTime(1), liveUser.getPlatformType());
        for (int i = 0; i <= 10; i++) {
            liveLimitList2.addClockWhiteList(i,liveUser.getId());
        }
        liveLimitList2.getClockWhiteList().remove(9);
        liveLimitListDaoService.save(liveLimitList2);
        lastLiveUserLimitList = liveLimitProcess.getLastLiveLimitTimeListByLiveUser(liveUser.getId(), liveUser.getPlatformType());
        expectedTime = TimeUtil.getAfterDayStartTime(1) + TimeUnit.HOURS.toMillis(9) - 1;
        Assert.assertEquals(lastLiveUserLimitList,expectedTime);
    }



    @Test
    public void test(){
        //跨天测试关播时间
        LiveLimitList liveLimitList = new LiveLimitList(System.currentTimeMillis(), liveUser.getPlatformType());
        for (int i = 11; i < 24; i++) {
            liveLimitList.addClockWhiteList(i,liveUser.getId());
        }
        liveLimitListDaoService.save(liveLimitList);
        LiveLimitList liveLimitList2 = new LiveLimitList(TimeUtil.getAfterDayStartTime(1), liveUser.getPlatformType());
        for (int i = 0; i < 2; i++) {
            liveLimitList2.addClockWhiteList(i,liveUser.getId());
        }
        liveLimitListDaoService.save(liveLimitList2);
        long lastLiveLimitTimeListByLiveUser = liveLimitProcess.getLastLiveLimitTimeListByLiveUser(liveUser.getId(), liveUser.getPlatformType());
        long expectedTime = TimeUtil.getAfterDayStartTime(1) + TimeUnit.HOURS.toMillis(2) - 1;
        Assert.assertEquals(expectedTime,lastLiveLimitTimeListByLiveUser);
    }
}
