package com.donglaistd.jinli.database.dao;

import cn.hutool.core.util.RandomUtil;
import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.config.SpringContext;
import com.donglaistd.jinli.database.dao.statistic.DailyUserActiveRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveRecord;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import com.donglaistd.jinli.domain.LiveRecordResult;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LiveRecordDaoServiceTest extends BaseTest {
    @Autowired
    LiveRecordDaoService liveRecordDaoService;


    @Test
    public void liveRecordTest(){
        String liveUserId = "123";
        long time = TimeUtil.getCurrentDayStartTime();
        LiveRecord liveRecord1 = LiveRecord.newInstance(liveUserId,"1", "123", time + 30, 30, 50, 50, 5, Constant.GameType.BACCARAT,Constant.PlatformType.PLATFORM_JINLI);
        liveRecord1.setAudienceHistory(Set.of("1","2","3"));
        LiveRecord liveRecord2 = LiveRecord.newInstance(liveUserId, "1","123", time + 40, 30, 50, 50, 5, Constant.GameType.BACCARAT,Constant.PlatformType.PLATFORM_JINLI);
        liveRecord2.setAudienceHistory(Set.of("4","2","6"));
        LiveRecord liveRecord3 = LiveRecord.newInstance(liveUserId, "1","123", time + 50, 30, 50, 50, 5, Constant.GameType.BACCARAT,Constant.PlatformType.PLATFORM_JINLI);
        liveRecord3.setAudienceHistory(Set.of("7","8","9"));
        liveRecordDaoService.save(liveRecord1);
        liveRecordDaoService.save(liveRecord2);
        liveRecordDaoService.save(liveRecord3);
        LiveRecord recentLiveRecord = liveRecordDaoService.findRecentLiveRecordByLiveUser(liveUserId);
        Assert.assertEquals(time + 50, recentLiveRecord.getLiveStartTime());
        LiveRecord liveRecord = liveRecordDaoService.totalLiveRecordInfo(liveUserId);
        Assert.assertEquals(150, liveRecord.getGameFlow());
        Assert.assertEquals(90, liveRecord.getLiveTime());
        Assert.assertEquals(3, liveRecord.getRecordTime());

        LiveRecord liveRecord4 = LiveRecord.newInstance("7788", "2","56", time + 30, 30, 50, 50, 5, Constant.GameType.BACCARAT,Constant.PlatformType.PLATFORM_JINLI);
        liveRecord4.setAudienceHistory(Set.of("7","8","9"));
        LiveRecord liveRecord5 = LiveRecord.newInstance("7788", "2","56", time + 40, 30, 50, 50, 5, Constant.GameType.BACCARAT,Constant.PlatformType.PLATFORM_JINLI);
        liveRecord5.setAudienceHistory(Set.of("7","8","9"));
        liveRecordDaoService.save(liveRecord4);
        liveRecordDaoService.save(liveRecord5);
        List<LiveRecordResult> totalGroupLiveRecordByTimes = liveRecordDaoService.getTotalGroupLiveRecordByTimes(time, System.currentTimeMillis());
        //Map<String, Integer> audienceNumByTimes = liveRecordDaoService.getAudienceNumByTimes(time, System.currentTimeMillis());
        Assert.assertEquals(2, totalGroupLiveRecordByTimes.size());
        Map<String, LiveRecordResult> liveUserRecords = totalGroupLiveRecordByTimes.stream().collect(Collectors.toMap(LiveRecordResult::getLiveUserId, record -> record));

        Assert.assertEquals(3, liveUserRecords.get("7788").getAudienceNum());
        Assert.assertEquals(8, liveUserRecords.get("123").getAudienceNum());
        long liveTime = liveRecordDaoService.totalLiveUserLiveTimeByTimeBetween(liveUserId, time, System.currentTimeMillis());
        Assert.assertEquals(90,liveTime);
    }

    @Test
    public void initDailyUserActiveRecord(){
        UserDaoService userDaoService = SpringContext.getBean(UserDaoService.class);
        DailyUserActiveRecordDaoService dailyUserActiveRecordDaoService = SpringContext.getBean(DailyUserActiveRecordDaoService.class);
        List<DailyUserActiveRecord> records = new ArrayList<>();
        for (User user : userDaoService.findAll()) {
            LiveWatchRecordResult liveWatchRecordResult = new LiveWatchRecordResult(10, 5, RandomUtil.randomInt(5000, 90000), 1, 2, user.getId(), 1, RandomUtil.randomInt(1, 10), user.getPlatformType());
            records.add(DailyUserActiveRecord.newInstance(liveWatchRecordResult, TimeUtil.getCurrentDayStartTime()));
            records.add(DailyUserActiveRecord.newInstance(liveWatchRecordResult, TimeUtil.getBeforeDayStartTime(2)));
        }
        dailyUserActiveRecordDaoService.saveAll(records);
    }
}
