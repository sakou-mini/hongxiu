package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.statistic.DailyLiveRecordInfoDaoService;
import com.donglaistd.jinli.database.entity.statistic.DailyLiveRecordInfo;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DailyLiveRecordInfoDaoServiceTest extends BaseTest {
    @Autowired
    DailyLiveRecordInfoDaoService dailyLiveRecordInfoDaoService;

    @Test
    public void findByTimesTest(){

        DailyLiveRecordInfo recordInfo1 = DailyLiveRecordInfo.newInstance("123", 50, 50, 1, 50,
                TimeUtil.getCurrentDayStartTime(),0, Constant.PlatformType.PLATFORM_JINLI);
        DailyLiveRecordInfo recordInfo2 = DailyLiveRecordInfo.newInstance("124", 50, 50, 1, 50,
                TimeUtil.getCurrentDayStartTime(),0,Constant.PlatformType.PLATFORM_JINLI);
        DailyLiveRecordInfo recordInfo3 = DailyLiveRecordInfo.newInstance("125", 50, 50, 1, 50,
                TimeUtil.getCurrentDayStartTime(),0,Constant.PlatformType.PLATFORM_JINLI);
        DailyLiveRecordInfo recordInfo4 = DailyLiveRecordInfo.newInstance("123", 50, 100, 1, 50,
                TimeUtil.getBeforeDayStartTime(1),0,Constant.PlatformType.PLATFORM_JINLI);
        dailyLiveRecordInfoDaoService.saveAll(Lists.newArrayList(recordInfo1,recordInfo2,recordInfo3,recordInfo4));
        List<DailyLiveRecordInfo> recordInfos = dailyLiveRecordInfoDaoService.findByLiveUserIdAndTimes("123", TimeUtil.getBeforeDayStartTime(1), System.currentTimeMillis());
        Assert.assertEquals(2, recordInfos.size());
    }
}
