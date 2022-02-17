package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.constant.StatisticEnum;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDataStatisticsDaoServiceTest extends BaseTest {
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;

    @Test
    public void totalUserDataStatisticsTest(){
        long startTime = TimeUtil.getBeforeDayStartTime(1);
        long endTime = TimeUtil.getCurrentDayStartTime();
        UserDataStatistics userInfo = UserDataStatistics.newInstance("userid", true);
        userInfo.addIpRecord("192.168.0.113");
        userInfo.addIpRecord("192.168.0.114");
        userDataStatisticsDaoService.save(userInfo);
        UserDataStatistics userInfo2 = UserDataStatistics.newInstance("userid2", false);
        userInfo2.addIpRecord("192.168.0.113");
        userInfo2.addIpRecord("192.168.0.115");
        userDataStatisticsDaoService.save(userInfo2);
        long ipNum = userDataStatisticsDaoService.countIpNumByTimeBetween(startTime, endTime);
        Assert.assertEquals(3,ipNum);
        long registNum = userDataStatisticsDaoService.countRegisterUserNumByTimeBetween(startTime, endTime);
        Assert.assertEquals(1,registNum);

        List<UserDataStatistics> result = userDataStatisticsDaoService.groupIsNewUserDataByTimeBetween(TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis());
        Assert.assertEquals(2, result.size());
    }

    @Test
    public void countUserActiveNumByActiveDaysTest(){
        long startTime = TimeUtil.getBeforeDayStartTime(2);
        long endTime = TimeUtil.getCurrentDayStartTime();
        UserDataStatistics userInfo = UserDataStatistics.newInstance("1", false);
        userInfo.setRecordTime(startTime);
        userInfo.addActiveDays();
        userDataStatisticsDaoService.save(userInfo);
        UserDataStatistics userInfo2 = UserDataStatistics.newInstance("1", false);
        userInfo2.setRecordTime(endTime);
        userInfo2.addActiveDays();
        userDataStatisticsDaoService.save(userInfo2);
        UserDataStatistics userInfo3 = UserDataStatistics.newInstance("2", false);
        userInfo3.setRecordTime(endTime);
        userInfo3.addActiveDays();
        userDataStatisticsDaoService.save(userInfo3);
        int activeNum = userDataStatisticsDaoService.countUserActiveNumByActiveDays(startTime, endTime, 1);
        Assert.assertEquals(2,activeNum);
    }

    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;

    @Test
    public void countMaxActiveInfoBetweenTimes(){
        long startTime = TimeUtil.getBeforeDayStartTime(2);
        long endTime = TimeUtil.getCurrentDayStartTime();
        UserDataStatistics userInfo = UserDataStatistics.newInstance("1", false);
        userInfo.setRecordTime(startTime);
        userInfo.setActiveDays(2);
        userDataStatisticsDaoService.save(userInfo);
        UserDataStatistics userInfo2 = UserDataStatistics.newInstance("1", false);
        userInfo2.setRecordTime(endTime);
        userInfo2.setActiveDays(4);
        userDataStatisticsDaoService.save(userInfo2);
        UserDataStatistics userInfo3 = UserDataStatistics.newInstance("2", false);
        userInfo3.setRecordTime(endTime);
        userInfo3.setActiveDays(6);
        userDataStatisticsDaoService.save(userInfo3);
        UserDataStatistics userInfo4 = UserDataStatistics.newInstance("6", false);
        userInfo4.setRecordTime(endTime);
        userInfo4.setActiveDays(2);
        userDataStatisticsDaoService.save(userInfo4);

        UserDataStatistics userInfo5 = UserDataStatistics.newInstance("8", false);
        userInfo5.setRecordTime(endTime);
        userInfo5.setActiveDays(6);
        userDataStatisticsDaoService.save(userInfo5);

        List<UserDataStatistics> userDataStatistics = userDataStatisticsDaoService.totalMaxActiveDaysGroupByUserIdAndTimeBetween(startTime, endTime);
        Map<Integer, Long> groupInfo = userDataStatistics.stream().collect(Collectors.groupingBy(UserDataStatistics::getActiveDays, Collectors.counting()));
        Assert.assertEquals(3,groupInfo.size());
        Assert.assertEquals(1L,(long)groupInfo.get(4));
        Assert.assertEquals(2L,(long)groupInfo.get(6));
        Assert.assertEquals(1L,(long)groupInfo.get(2));

        Map<StatisticEnum.ActiveDaysEnum, Long> activeDaysEnumLongMap = userDataStatisticsProcess.totalActiveDaysForGroupByTimes(startTime, endTime);
        Assert.assertEquals(1,activeDaysEnumLongMap.get(StatisticEnum.ActiveDaysEnum.ACTIVE_2_3_DAY).longValue());
        Assert.assertEquals(3,activeDaysEnumLongMap.get(StatisticEnum.ActiveDaysEnum.ACTIVE_4_7_DAY).longValue());
        System.out.println(activeDaysEnumLongMap);
    }

    public UserDataStatistics createUserDataStatistics(String userId, long time, boolean newUser, int activeDay) {
        UserDataStatistics userInfo = UserDataStatistics.newInstance(userId, newUser);
        userInfo.setRecordTime(time);
        userInfo.setActiveDays(activeDay);
        return userDataStatisticsDaoService.save(userInfo);
    }

    @Test
    public void totalRetainedRateTest(){
        long startTime = TimeUtil.getBeforeDayStartTime(1);
        createUserDataStatistics("user1", startTime, true, 1);
        createUserDataStatistics("user2", startTime, true, 1);
        createUserDataStatistics("user1", TimeUtil.getCurrentDayStartTime(), false, 1);
        BigDecimal nextDayRetainedRate = userDataStatisticsDaoService.totalRetainedRate(TimeUtil.getCurrentDayStartTime(), 1);
        Assert.assertEquals(0.5, nextDayRetainedRate.doubleValue(),0);  //1/2

        createUserDataStatistics("user3", TimeUtil.getBeforeDayStartTime(7), true, 1);
        createUserDataStatistics("user4", TimeUtil.getBeforeDayStartTime(7), true, 1);
        createUserDataStatistics("user5", TimeUtil.getBeforeDayStartTime(7), true, 1);
        createUserDataStatistics("user3", TimeUtil.getCurrentDayStartTime(), false, 1);
        createUserDataStatistics("user4", TimeUtil.getCurrentDayStartTime(), false, 7);
        createUserDataStatistics("user5", TimeUtil.getCurrentDayStartTime(), false, 7);
        createUserDataStatistics("user6", TimeUtil.getCurrentDayStartTime(), false, 7);
        BigDecimal weekDayRetainedRate = userDataStatisticsDaoService.totalRetainedRate(TimeUtil.getCurrentDayStartTime(), 7);
        Assert.assertEquals(0.6667,weekDayRetainedRate.doubleValue(),0);
    }


    @Test
    public void groupUserDayLoginTest(){
        long startTime = TimeUtil.getMonthOfStartTime(System.currentTimeMillis());
        long endTime = System.currentTimeMillis();
        createUserDataStatistics("user1", TimeUtil.getBeforeDayStartTime(1), true, 1);
        createUserDataStatistics("user1", TimeUtil.getBeforeDayStartTime(1), false, 1);
        createUserDataStatistics("user2", TimeUtil.getBeforeDayStartTime(1), false, 1);
        createUserDataStatistics("user2", TimeUtil.getBeforeDayStartTime(2), false, 1);
        createUserDataStatistics("user3", TimeUtil.getBeforeDayStartTime(2), false, 1);
        createUserDataStatistics("user4", TimeUtil.getBeforeDayStartTime(2), false, 1);
        createUserDataStatistics("user3", TimeUtil.getBeforeDayStartTime(3), true, 1);
        createUserDataStatistics("user2", TimeUtil.getBeforeDayStartTime(4), false, 1);
        Map<Long, Integer> liveUserLoginMap = userDataStatisticsDaoService
                .groupDayLoginNumByUserIdsAndTimeBetween(startTime, endTime, List.of("user1", "user2", "user3"));
        Assert.assertEquals(2,(int) liveUserLoginMap.getOrDefault(TimeUtil.getBeforeDayStartTime(1), 0));
        Assert.assertEquals(2,(int) liveUserLoginMap.getOrDefault(TimeUtil.getBeforeDayStartTime(2), 0));
        Assert.assertEquals(1,(int) liveUserLoginMap.getOrDefault(TimeUtil.getBeforeDayStartTime(3), 0));
        Assert.assertEquals(1,(int) liveUserLoginMap.getOrDefault(TimeUtil.getBeforeDayStartTime(4), 0));

    }
}
