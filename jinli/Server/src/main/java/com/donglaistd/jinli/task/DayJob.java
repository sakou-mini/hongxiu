package com.donglaistd.jinli.task;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.StatisticEnum;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.dao.LiveRecordDaoService;
import com.donglaistd.jinli.database.dao.backoffice.*;
import com.donglaistd.jinli.database.dao.statistic.CommonStatisticInfoDaoService;
import com.donglaistd.jinli.database.dao.statistic.DailyDownloadRecordDaoService;
import com.donglaistd.jinli.database.dao.statistic.DailyLiveRecordInfoDaoService;
import com.donglaistd.jinli.database.entity.backoffice.*;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.database.entity.rank.GiftRank;
import com.donglaistd.jinli.database.entity.statistic.CommonStatisticInfo;
import com.donglaistd.jinli.database.entity.statistic.DailyDownloadRecord;
import com.donglaistd.jinli.database.entity.statistic.StatisticManager;
import com.donglaistd.jinli.service.PlatformLiveService;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import com.donglaistd.jinli.service.UserAgentProcessService;
import com.donglaistd.jinli.service.statistic.CommonStatisticProcess;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.MONTH_DAYS;
import static com.donglaistd.jinli.constant.GameConstant.WEEK_DAYS;

@Component
@EnableAsync
public class DayJob {
    @Autowired
    private GiftLogDaoService giftLogDaoService;
    @Autowired
    UserAgentProcessService userAgentProcessService;
    @Autowired
    StatisticManager statisticManager;
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;
    @Autowired
    DailyDownloadRecordDaoService dailyDownloadRecordDaoService;
    @Autowired
    NewUserReportDataDaoService newUserReportDataDaoService;
    @Autowired
    ActiveUserReportDataDaoService activeUserReportDataDaoService;
    @Autowired
    RetainedUserReportDataDaoService retainedUserReportDataDaoService;
    @Autowired
    CommonStatisticProcess commonStatisticProcess;
    @Autowired
    CommonStatisticInfoDaoService commonStatisticInfoDaoService;
    @Autowired
    MobileDevicesReportDataDaoService mobileDevicesReportDataDaoService;
    @Autowired
    LiveRecordDaoService liveRecordDaoService;
    @Autowired
    DailyLiveRecordInfoDaoService dailyLiveRecordInfoDaoService;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    @Autowired
    PlatformLiveService plantLiveService;

    @Async
    @Scheduled(cron = "${data.task.cron.dayJob}")
    public void dayJob() {
        if(!serverAvailabilityCheckService.isActive()) return;
        long endTime = TimeUtil.todayStartTime(System.currentTimeMillis());
        long startTime = TimeUtil.yesterdayStartTime(endTime);
        //统计每个主播的每日直播信息
        totalDayLiveInfo(startTime,endTime);
        userAgentProcessService.totalDayUserAgentRecord(startTime,endTime);
        //每日新增和下载量
        totalDayNewUserAndDownloadData(startTime,endTime);
        //每日活跃数据
        totalDayActiveData(startTime, endTime);
        //留存率
        totalDayRetainedUserData(startTime);
        //每日设备情况
        totalDayMobileDevicesReportData(startTime, endTime);
        //每月数据统计
        totalMonthUserData(startTime, endTime);
        //所有平台的直播数据以及 用户每日活跃记录统计
        totalAllPlatformTodayLiveAndUserData(startTime, endTime);
    }

    @Async
    @Scheduled(cron = "${data.task.cron.oclock.dayJob}")
    public void dayOneHourOfficial() {
        if(!serverAvailabilityCheckService.isActive()) return;
        plantLiveService.initPlatformLiveT(1);
        plantLiveService.initPlatformLiveQ(1);
    }

    private GiftRank saveContributionRank(List<GiftLog> list, long createTime, boolean isSum) {
        if (isSum) {
            List<Pair<String, Integer>> collect = list.stream().map(g -> new Pair<>(g.getSenderId(), g.getSendAmount())).collect(Collectors.toList());
            return GiftRank.newInstance(createTime, Constant.QueryTimeType.ALL, Constant.RankType.CONTRIBUTION_RANK, collect);
        } else {
            // 贡献榜
            List<Pair<String, Integer>> collect = list.stream().map(g -> new Pair<>(g.getSenderId(), g.getSendAmount())).collect(Collectors.toList());
            return GiftRank.newInstance(createTime, Constant.QueryTimeType.TODAY, Constant.RankType.CONTRIBUTION_RANK, collect);
        }
    }

    private GiftRank saveGiftRank(List<GiftLog> list, long createTime, boolean isSum) {
        if (isSum) {
            List<Pair<String, Integer>> collect = list.stream().map(g -> new Pair<>(g.getReceiveId(), g.getSendAmount())).collect(Collectors.toList());
            return GiftRank.newInstance(createTime, Constant.QueryTimeType.ALL, Constant.RankType.GIFT_RANK, collect);
        } else {
            // 礼物榜
            List<Pair<String, Integer>> collect = list.stream().map(g -> new Pair<>(g.getReceiveId(), g.getSendAmount())).collect(Collectors.toList());
            return GiftRank.newInstance(createTime, Constant.QueryTimeType.TODAY, Constant.RankType.GIFT_RANK, collect);
        }
    }

    private void totalDayLiveInfo(long startTime, long endTime){
        List<GiftLog> dayGiftList = giftLogDaoService.findByCreateTimeBetweenAndGroupByReceiveId(startTime, endTime);
        statisticManager.totalStatisticInfoTypeOfGiftReward(dayGiftList, startTime);//Statistic the total number of gifts given by audience every day
        statisticManager.totalStatisticInfoTypeOfBetAmount(startTime, endTime);//Statistic liveRoom bet flow every day
        statisticManager.totalStatisticInfoTypeOfLiveRecord(startTime,endTime);//Statistic the number of live audience number and  totalLiveTime every day.
    }

    private void totalDayNewUserAndDownloadData(long startTime, long endTime){
        long registerNum = userDataStatisticsDaoService.countRegisterUserNumByTimeBetween(startTime, endTime);
        DailyDownloadRecord downloadRecord = dailyDownloadRecordDaoService.finByTime(startTime);
        newUserReportDataDaoService.save(NewUserReportData.newInstance(startTime, registerNum, downloadRecord == null ? 0 : downloadRecord.getDownloadCount()));
    }

    private void totalDayActiveData(long startTime,long endTime){
        Map<Boolean, Integer> registerAndActiveMap = userDataStatisticsDaoService.groupIsNewUserDataByTimeBetween(startTime, endTime).stream()
                .collect(Collectors.toMap(UserDataStatistics::isNewUser, UserDataStatistics::getActiveDays));
        Integer newUserNum = registerAndActiveMap.getOrDefault(true, 0);
        Integer oldUserNum = registerAndActiveMap.getOrDefault(false, 0);
        int wau = userDataStatisticsDaoService.countUserActiveNumByActiveDays(TimeUtil.getBeforeDayStartTime(WEEK_DAYS), endTime, 1);
        int mau = userDataStatisticsDaoService.countUserActiveNumByActiveDays(TimeUtil.getBeforeDayStartTime(MONTH_DAYS), endTime, 1);
        activeUserReportDataDaoService.save(ActiveUserReportData.newInstance(startTime, newUserNum, oldUserNum, wau, mau));
    }


    private void totalDayRetainedUserData(long startTime){
        BigDecimal nextDayRetainedRate = userDataStatisticsDaoService.totalRetainedRate(startTime, 1);
        BigDecimal weekRetainedRate = userDataStatisticsDaoService.totalRetainedRate(startTime, WEEK_DAYS);
        BigDecimal monthRetainedRate = userDataStatisticsDaoService.totalRetainedRate(startTime, MONTH_DAYS);
        RetainedUserReportData retainedUserReportData = RetainedUserReportData.newInstance(startTime, nextDayRetainedRate, weekRetainedRate, monthRetainedRate);
        retainedUserReportDataDaoService.save(retainedUserReportData);
    }

    private void totalMonthUserData(long startTime,long endTime){
        if(!TimeUtil.isLastDayOfMonth(startTime)) return;
        CommonStatisticInfo monthUserStatisticInfo = commonStatisticProcess.totalUserdataByTimeBetween(TimeUtil.getMonthOfStartTime(startTime), endTime, StatisticEnum.MONTH_USERDATA);
        commonStatisticInfoDaoService.save(monthUserStatisticInfo);
    }

    private void totalDayMobileDevicesReportData(long startTime,long endTime){
        MobileDevicesReportData devicesReport = MobileDevicesReportData.newInstance(startTime);
        List<UserDataStatistics> userDataList = userDataStatisticsDaoService.findByTimeBetween(startTime, endTime);
        for (UserDataStatistics userDataStatistics : userDataList) {
            if(userDataStatistics.getBrand() == null) continue;
            if(userDataStatistics.isNewUser())
                devicesReport.addNewUserBrandNumByPhoneBrand(userDataStatistics.getBrand(),1);
            else
                devicesReport.addActiveUserBrandNumByPhoneBrand(userDataStatistics.getBrand(),1);
            // TODO check is paying Player
        }
        mobileDevicesReportDataDaoService.save(devicesReport);
    }

    private void totalAllPlatformTodayLiveAndUserData(long startTime,long endTime){
        commonStatisticProcess.totalAllPlatformTodayLiveData(startTime, endTime);
        //total day platformUserTotal data
        commonStatisticProcess.totalAllPlatformTodayUserData(startTime, endTime);
        //total day user data
        commonStatisticProcess.totalAllUserActiveData(startTime, endTime);
    }
}
