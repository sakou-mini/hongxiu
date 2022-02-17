package com.donglaistd.jinli.database.entity.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.statistic.DailyLiveRecordInfoDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class StatisticManager {
    @Autowired
    private StatisticInfoDaoService statisticInfoDaoService;
    @Autowired
    private DailyBetInfoDaoService dailyBetInfoDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    GiftLogDaoService giftLogDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    LiveRecordDaoService liveRecordDaoService;
    @Autowired
    DailyLiveRecordInfoDaoService dailyLiveRecordInfoDaoService;

    @Value(value = "${data.task.statistic.size}")
    private int SIZE;

    private StatisticManager() {
    }

    public void totalStatisticInfoTypeOfBetAmount(long startTime, long endTime) {
        List<DailyBetInfo> list = dailyBetInfoDaoService.findTotalBetAmountGroupByLiveUserIdAndTimeBetween(startTime, endTime);
        List<StatisticInfo> collect = list.stream()
                .map(d -> new StatisticInfo(d.getLiveUserId(), startTime, (int) d.getBetAmount(), Constant.StatisticType.BET_AMOUNT))
                .collect(Collectors.toList());
        statisticInfoDaoService.saveAll(collect);
    }

    public void totalStatisticInfoTypeOfLiveRecord(long startTime, long endTime){
        List<DailyLiveRecordInfo> dailyLiveRecordInfos = dailyLiveRecordInfoDaoService.totalDailyLiveInfo(startTime, endTime);
        dailyLiveRecordInfoDaoService.saveAll(dailyLiveRecordInfos);
        List<StatisticInfo> statisticInfos = new ArrayList<>(dailyLiveRecordInfos.size()*2);
        for (DailyLiveRecordInfo dailyLiveInfo : dailyLiveRecordInfos) {
            statisticInfos.add(new StatisticInfo(dailyLiveInfo.getLiveUserId(), startTime, dailyLiveInfo.getAudienceNum(), Constant.StatisticType.LIVE_POPULATION));
            statisticInfos.add(new StatisticInfo(dailyLiveInfo.getLiveUserId(), startTime, (int) dailyLiveInfo.getLiveTime(), Constant.StatisticType.LIVE_TIME));
        }
        statisticInfoDaoService.saveAll(statisticInfos);
    }

    public void totalStatisticInfoTypeOfGiftReward(List<GiftLog> giftLogs, long time){
        List<StatisticInfo> statisticInfos = new ArrayList<>(giftLogs.size());
        User user;
        for (GiftLog giftLog : giftLogs) {
            user = userDaoService.findById(giftLog.getReceiveId());
            if(user == null) continue;
            statisticInfos.add(new StatisticInfo(user.getLiveUserId(), time, giftLog.getSendAmount(), Constant.StatisticType.REWARD));
        }
        statisticInfoDaoService.saveAll(statisticInfos);
    }

    public List<Jinli.StatisticCurve> getCurveByLiveUserIdAndType(String liveUserId, Constant.StatisticType type, Map<Constant.StatisticType, List<StatisticInfo>> map) {
        List<StatisticInfo> statisticInfos = Lists.newArrayList(statisticInfoDaoService.findByLiveUserIdIsAndTypeIsOrderByTimeDesc(liveUserId, type, 0, SIZE));
        statisticInfos.add(getTodayStatistic(liveUserId,type));
        statisticInfos.sort(Comparator.comparing(StatisticInfo::getTime));
        map.put(type, statisticInfos);
        return statisticInfos.stream().map(StatisticInfo::toProto).collect(Collectors.toList());
    }

    public List<Jinli.StatisticSummary> getStatisticSummaryList(Map<Constant.StatisticType, List<StatisticInfo>> map) {
        List<Jinli.StatisticSummary> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(getStatisticSummary(k, v)));
        return list;
    }

    private Jinli.StatisticSummary getStatisticSummary(Constant.StatisticType type, List<StatisticInfo> list) {
        Jinli.StatisticSummary.Builder builder = Jinli.StatisticSummary.newBuilder();
        StatisticInfo today;
        if (Objects.isNull(list) ) {
            return builder.setIncrease(0).setNumber(0).setArrow(Constant.Arrow.NO_CHANGE).setType(type).build();
        }else if(list.size() < 2){
            today = list.get(0);
            return builder.setIncrease(today.getValue()).setNumber(today.getValue()).setArrow(Constant.Arrow.UP).setType(type).build();
        }
        today = list.get(list.size()-1);
        StatisticInfo yesterday = list.get(list.size()-2);
        int compare = today.getValue() - yesterday.getValue();
        if (compare > 0) {
            builder.setArrow(Constant.Arrow.UP);
        } else if (compare == 0) {
            builder.setArrow(Constant.Arrow.NO_CHANGE);
        } else if (compare < 0) {
            builder.setArrow(Constant.Arrow.DOWNWARD);
        }
        builder.setNumber(today.getValue()).setType(type).setIncrease(compare);
        return builder.build();
    }

    public Jinli.GetLiveStatisticReply buildReply(String liveUserId) {
        Jinli.GetLiveStatisticReply.Builder builder = Jinli.GetLiveStatisticReply.newBuilder();
        Map<Constant.StatisticType, List<StatisticInfo>> map = new HashMap<>();

        builder.addAllRewardCurve(getCurveByLiveUserIdAndType(liveUserId, Constant.StatisticType.REWARD, map));
        builder.addAllBetCurve(getCurveByLiveUserIdAndType(liveUserId, Constant.StatisticType.BET_AMOUNT, map));
        builder.addAllLivePopulationCurve(getCurveByLiveUserIdAndType(liveUserId, Constant.StatisticType.LIVE_POPULATION, map));
        builder.addAllLiveTimeCurve(getCurveByLiveUserIdAndType(liveUserId, Constant.StatisticType.LIVE_TIME, map));
        builder.addAllSummary(getStatisticSummaryList(map));
        return builder.build();
    }


    public StatisticInfo getTodayStatistic(String liveUserId, Constant.StatisticType type){
        StatisticInfo statisticInfo=null;
        DailyLiveRecordInfo dailyLiveRecordInfo;
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = System.currentTimeMillis();
        switch (type){
            case REWARD:
                LiveUser liveUser = liveUserDaoService.findById(liveUserId);
                GiftLog todayGiftLog = giftLogDaoService.findByReceiveIdAndTimeBetween(liveUser.getUserId(), startTime, endTime);
                if(todayGiftLog ==null) todayGiftLog = GiftLog.newInstance("", "", 0,"",0);
                statisticInfo = new StatisticInfo(liveUserId, startTime, todayGiftLog.getSendAmount(), type);
                break;
            case LIVE_TIME:
                dailyLiveRecordInfo = dailyLiveRecordInfoDaoService.totalDailyLiveInfoBuLiveUser(liveUserId, startTime, endTime);
                statisticInfo = new StatisticInfo(liveUserId, startTime, (int) dailyLiveRecordInfo.getLiveTime(), type);
                break;
            case LIVE_POPULATION:
                dailyLiveRecordInfo = dailyLiveRecordInfoDaoService.totalDailyLiveInfoBuLiveUser(liveUserId, startTime, endTime);
                statisticInfo = new StatisticInfo(liveUserId, startTime, dailyLiveRecordInfo.getAudienceNum(), type);
                break;
            case BET_AMOUNT:
                DailyBetInfo todayBetInfo = dailyBetInfoDaoService.findTotalBetAmountGroupByLiveUserIdAndTimeBetween(liveUserId,startTime, endTime);
                if(todayBetInfo==null) todayBetInfo = new DailyBetInfo();
                statisticInfo = new StatisticInfo(liveUserId, startTime, (int) todayBetInfo.getBetAmount(), type);
                break;
        }
        return statisticInfo;
    }
}
