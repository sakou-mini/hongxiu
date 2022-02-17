package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.StatisticEnum;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.dao.platform.PlatformRechargeRecordDaoService;
import com.donglaistd.jinli.database.dao.statistic.CommonStatisticInfoDaoService;
import com.donglaistd.jinli.database.dao.statistic.DailyUserActiveRecordDaoService;
import com.donglaistd.jinli.database.dao.statistic.DayLiveDataTotalDaoService;
import com.donglaistd.jinli.database.dao.statistic.DayUserDataTotalDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.database.entity.statistic.CommonStatisticInfo;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import com.donglaistd.jinli.database.entity.statistic.DayLiveDataTotal;
import com.donglaistd.jinli.database.entity.statistic.DayUserDataTotal;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.statistic.UserGiftDetailData;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.PlatformUtil;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.StatisticEnum.DAY_USERDATA;
import static com.donglaistd.jinli.constant.StatisticEnum.StatisticItemEnum.*;

@Component
public class CommonStatisticProcess {
    @Autowired
    CommonStatisticInfoDaoService commonStatisticInfoDaoService;
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    GiftLogDaoService giftLogDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Autowired
    FollowRecordDaoService followRecordDaoService;
    @Autowired
    BulletChatMessageRecordDaoService bulletChatMessageRecordDaoService;
    @Autowired
    LiveRecordDaoService liveRecordDaoService;
    @Autowired
    DayLiveDataTotalDaoService dayLiveDataTotalDaoService;
    @Autowired
    PlatformRechargeRecordDaoService platformRechargeRecordDaoService;
    @Autowired
    GiftOrderDaoService giftOrderDaoService;
    @Autowired
    DayUserDataTotalDaoService dayUserDataTotalDaoService;
    @Autowired
    DailyUserActiveRecordDaoService dailyUserActiveRecordDaoService;

    public CommonStatisticInfo totalCurrentDayUserData(){
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = System.currentTimeMillis();
        return totalUserdataByTimeBetween(startTime, endTime,DAY_USERDATA);
    }

    /**
     *
     * @param startTime must be start time
     * @param endTime   must be end time
     */
    public CommonStatisticInfo totalUserdataByTimeBetween(long startTime, long endTime,StatisticEnum statisticType){
        long registerNum = userDataStatisticsDaoService.countRegisterUserNumByTimeBetween(startTime, endTime);
        GiftLog spendInfo = giftLogDaoService.totalGiftUserNumAndGiftAmountByTimeBetween(startTime, endTime);
        if(spendInfo == null) {
            spendInfo = GiftLog.newInstance("", "", 0, "", 0);
            spendInfo.setCreateTime(0);
        }
        long ipNum = userDataStatisticsDaoService.countIpNumByTimeBetween(startTime, endTime);
        CommonStatisticInfo commonStatisticInfo = CommonStatisticInfo.newInstance(startTime,statisticType);
        commonStatisticInfo.putStatisticItem(REGISTER_NUM,registerNum);
        commonStatisticInfo.putStatisticItem(SPEND_AMOUNT,spendInfo.getSendAmount());
        commonStatisticInfo.putStatisticItem(SPEND_NUM,spendInfo.getCreateTime());
        commonStatisticInfo.putStatisticItem(IP_NUM,ipNum);
        return commonStatisticInfo;
    }

    public PageInfo<UserGiftDetailData> getTodayUserSpendDetail(int size, int page){
        PageInfo<GiftLog> giftLogs = giftLogDaoService.totalPageUserGiftInfoByTimeBetween(TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis(), page, size);
        List<UserGiftDetailData> userGiftDetailData = new ArrayList<>(giftLogs.getContent().size());
        Map<String, User> cacheUser = new HashMap<>();
        User user;
        LiveUser receiveLiveUser;
        Room room;
        for (GiftLog giftLog : giftLogs.getContent()) {
            user = cacheUser.getOrDefault(giftLog.getSenderId(), userDaoService.findById(giftLog.getSenderId()));
            receiveLiveUser = liveUserDaoService.findByUserId(giftLog.getReceiveId());
            if(user == null || receiveLiveUser == null) continue;
            room = roomDaoService.findByLiveUser(receiveLiveUser);
            cacheUser.putIfAbsent(giftLog.getSenderId(), user);
            userGiftDetailData.add(new UserGiftDetailData(user, room, giftLog));
        }
        return new PageInfo<>(userGiftDetailData, giftLogs.getPageable(), giftLogs.getTotal());
    }

    public CommonStatisticInfo  totalPlatformUserDataByTimeBetween(long startTime, long endTime, Constant.PlatformType platform, StatisticEnum statisticType){
        List<Room> platformRoom = DataManager.getOnlineRoomList().stream().filter(room -> room.isLive() && room.isSharedToPlatform(platform)).collect(Collectors.toList());
        GiftLog giftLog = giftLogDaoService.totalGiftAmountByTimeBetweenAndPlatform(startTime, endTime,platform);
        if(giftLog == null){
            giftLog = GiftLog.newInstance("", "", 0, "", 0);
            giftLog.setCreateTime(0);
        }
        long liveNum = platformRoom.size();
        long liveWatchNum = totalLiveAudienceNumByPlatform(platformRoom,platform);
        long userNum = userDaoService.countNormalActiveUserNumByPlatform(platform);
        long spendAmount = giftLog.getSendAmount();
        long spendNum = giftLog.getCreateTime();
        long registerNum = userDataStatisticsDaoService.countRegisterUserNumByTimeBetweenAndPlatform(startTime, endTime, platform);
        long ipNum = userDataStatisticsDaoService.countIpNumByTimeBetweenAndPlatform(startTime, endTime, platform);
        long chatNum = bulletChatMessageRecordDaoService.countByTimesAndPlatform(startTime, endTime, platform);
        long loginNum = userDataStatisticsDaoService.countUserNumByTimesAndPlatformAndNewUser(startTime, endTime, platform, false);
        long followNum = followRecordDaoService.countFollowNumByTimeBetween(startTime, endTime, platform);
        LiveWatchRecord liveWatchRecord = Optional.ofNullable(liveWatchRecordDaoService.totalLiveWatchRecordByTimesAndPlatform(startTime, endTime, platform)).orElse(new LiveWatchRecord());
        long liveWatchTime = liveWatchRecord.getWatchTime();
        long avgLiveWatchTime = liveWatchRecord.getConnectedLiveCount() <= 0 ? 0 : liveWatchTime / loginNum;
        long liverUserNum = liveUserDaoService.findAllPassLiveUserByPlatform(platform).size();
        //用户留言（弹幕条数）
        //用户关注（今日关注记录）
        CommonStatisticInfo commonStatisticInfo = CommonStatisticInfo.newInstance(startTime,statisticType);
        commonStatisticInfo.putStatisticItem(LIVE_NUM,liveNum);
        commonStatisticInfo.putStatisticItem(LIVE_WATCH_NUM,liveWatchNum);
        commonStatisticInfo.putStatisticItem(USER_NUM,userNum);
        commonStatisticInfo.putStatisticItem(SPEND_AMOUNT,spendAmount);
        commonStatisticInfo.putStatisticItem(LOGIN_NUM,loginNum);
        commonStatisticInfo.putStatisticItem(REGISTER_NUM,registerNum);
        commonStatisticInfo.putStatisticItem(SPEND_NUM,spendNum);
        commonStatisticInfo.putStatisticItem(IP_NUM,ipNum);
        commonStatisticInfo.putStatisticItem(BULLET_CHAT_NUM,chatNum);
        commonStatisticInfo.putStatisticItem(FOLLOW_NUM,followNum);
        commonStatisticInfo.putStatisticItem(LIVE_WATCH_TIME, liveWatchTime);
        commonStatisticInfo.putStatisticItem(AVG_LIVE_WATCH_TIME,avgLiveWatchTime);
        commonStatisticInfo.putStatisticItem(LIVEUSER_NUM,liverUserNum);
        return commonStatisticInfo;
    }

    public long totalLiveAudienceNumByPlatform(List<Room> platformRoom){
        return platformRoom.stream().mapToInt(room -> {
            LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
            if (liveUser != null) {
                return (int) room.getAllPlatformAudienceList().stream().filter(id -> !Objects.equals(liveUser.getUserId(), id)).count();
            } else {
                return room.getAllPlatformAudienceList().size();
            }
        }).sum();
    }

    public long totalLiveAudienceNumByPlatform(List<Room> platformRoom, Constant.PlatformType platform){
        return platformRoom.stream().mapToInt(room -> room.getAudienceByPlatform(platform).size()).sum();
    }

    //==============统计各个平台直播每日数据=====================
    public DayLiveDataTotal totalPlatformTodayLiveData(Constant.PlatformType platform, long startTime, long endTime) {
        List<LiveRecord> records = liveRecordDaoService.findLiveRecordsByPlatformAndTimes(platform, startTime, endTime);
        long liveCount = records.size();
        long banLiveUserNum = liveUserDaoService.countBanLiveUserByTimeAndPlatform(platform, startTime, endTime);
        long banUserNum = userDaoService.countBanUserByTimeAndPlatform(platform, startTime, endTime);
        long rewardCount = giftLogDaoService.countByPlatformAndTimes(platform, startTime, endTime);

        GiftLog giftLog = Optional.ofNullable(giftLogDaoService.totalGiftAmountAndReceiverNumByTimeBetweenAndPlatform(startTime, endTime, platform)).orElse(new GiftLog());
        long incomeUserNum = giftLog.getSendNum();
        long incomeCoin = giftLog.getSendAmount();
        Set<String> noRepeatAudienceList = new HashSet<>(); //观众人数
        Set<String> noRepeatLiveLiverUerIds = new HashSet<>();//开播主播
        long bulletChatNum = bulletChatMessageRecordDaoService.countByTimesAndPlatform(startTime,endTime,platform); //实时最新，不依赖于直播记录
        long liveTime=0;
        List<LiveWatchRecord> liveWatchRecords = liveWatchRecordDaoService.findByTimesAndPlatform(startTime, endTime, platform);
        long liveVisitorCount = liveWatchRecords.size(); //实时最新，不依赖于直播记录
        long connectedLiveCount = 0;
        for (LiveWatchRecord watchRecord : liveWatchRecords) {
            noRepeatAudienceList.add(watchRecord.getUserId());
            connectedLiveCount += watchRecord.getConnectedLiveCount();
        }
        for (LiveRecord record : records) {
            noRepeatLiveLiverUerIds.add(record.getLiveUserId());
            liveTime += record.getLiveTime();
        }
        long attentionNum = followRecordDaoService.countFollowNumByTimeBetween(startTime, endTime, platform);
        long liveUserNum = noRepeatLiveLiverUerIds.size();
        DayLiveDataTotal dayLiveDataTotal = new DayLiveDataTotal();
        dayLiveDataTotal.setLiveCount(liveCount);
        dayLiveDataTotal.setLiveUserNum(liveUserNum);
        dayLiveDataTotal.setBanLiveUserCount(banLiveUserNum);
        dayLiveDataTotal.setBanUserCount(banUserNum);
        dayLiveDataTotal.setRewardCount(rewardCount);
        dayLiveDataTotal.setIncomeUserNum(incomeUserNum);
        dayLiveDataTotal.setIncomeCoin(incomeCoin);
        dayLiveDataTotal.setVisitorNum(noRepeatAudienceList.size());
        dayLiveDataTotal.setLiveVisitorCount(liveVisitorCount);
        dayLiveDataTotal.setBulletChatNum(bulletChatNum);
        dayLiveDataTotal.setConnectedLiveCount(connectedLiveCount);
        dayLiveDataTotal.setAttentionNum(attentionNum);
        dayLiveDataTotal.setLiveTime(liveTime);
        dayLiveDataTotal.setVisitorIds(noRepeatAudienceList);
        return dayLiveDataTotal;
    }

    public List<DayLiveDataTotal> totalAllPlatformTodayLiveData(long startTime, long endTime){
        List<DayLiveDataTotal> dayLiveDataTotalList = new ArrayList<>();
        List<Constant.PlatformType> allPlatform = PlatformUtil.getAllPlatform();
        DayLiveDataTotal dayLiveDataTotal;
        for (Constant.PlatformType platform : allPlatform) {
            dayLiveDataTotal = totalPlatformTodayLiveData(platform, startTime, endTime);
            dayLiveDataTotal.setPlatform(platform);
            dayLiveDataTotal.setRecordTime(startTime);
            dayLiveDataTotalList.add(dayLiveDataTotal);
        }
        return dayLiveDataTotalDaoService.saveAll(dayLiveDataTotalList);
    }

    //==============统计各个平台用户每日数据=====================
    private DayUserDataTotal totalPlatformTodayUserData(Constant.PlatformType platform, long startTime, long endTime){
        DayLiveDataTotal dayPlatformLiveData = Optional.ofNullable(dayLiveDataTotalDaoService.findByRecordTimeAndPlatform(platform, startTime)).orElse(new DayLiveDataTotal());
        LiveWatchRecord liveWatchRecord = Optional.ofNullable(liveWatchRecordDaoService.totalLiveWatchRecordByTimesAndPlatform(startTime, endTime, platform)).orElse(new LiveWatchRecord());
        long exchangeCoinAmount = totalPlatformRechargeCoin(platform, startTime, endTime);
        long liveWatchTime = liveWatchRecord.getWatchTime();
        long avgLiveWatchTime = liveWatchRecord.getConnectedLiveCount() <= 0 ? 0 : liveWatchTime / liveWatchRecord.getConnectedLiveCount();

        DayUserDataTotal dayUserDataTotal = new DayUserDataTotal(startTime,platform);
        dayUserDataTotal.setLiveVisitorCount(dayPlatformLiveData.getLiveVisitorCount());
        dayUserDataTotal.setLiveVisitorIds(dayPlatformLiveData.getVisitorIds());
        dayUserDataTotal.setExchangeCoinAmount(exchangeCoinAmount);
        dayUserDataTotal.setLiveWatchTime(liveWatchTime);
        dayUserDataTotal.setAvgLiveWatchTime(avgLiveWatchTime);
        dayUserDataTotal.setGiftCount(dayPlatformLiveData.getRewardCount());
        dayUserDataTotal.setGiftFlow(dayPlatformLiveData.getIncomeCoin());
        dayUserDataTotal.setConnectedLiveCount(dayPlatformLiveData.getConnectedLiveCount());
        dayUserDataTotal.setBulletMessageCount(dayPlatformLiveData.getBulletChatNum());
        dayUserDataTotal.setBanUserNum(dayPlatformLiveData.getBanUserCount());
        return dayUserDataTotal;
    }

    public List<DayUserDataTotal> totalAllPlatformTodayUserData(long startTime, long endTime){
        List<DayUserDataTotal> userDataTotals = new ArrayList<>();
        List<Constant.PlatformType> allPlatform = PlatformUtil.getAllPlatform();
        for (Constant.PlatformType platform : allPlatform) {
            userDataTotals.add(totalPlatformTodayUserData(platform, startTime, endTime));
        }
        return dayUserDataTotalDaoService.saveAll(userDataTotals);
    }

    private long totalPlatformRechargeCoin(Constant.PlatformType platform, long startTime, long endTime){
        switch (platform){
            case PLATFORM_T:
                return platformRechargeRecordDaoService.totalRechargeCoinByTimes(startTime, endTime);
            case PLATFORM_Q:
                return giftOrderDaoService.totalGiftOrderByTimes(startTime, endTime);
            default:
                return 0;
        }
    }

    public List<DailyUserActiveRecord> totalAllUserActiveData(long startTime, long endTime){
        List<DailyUserActiveRecord> records = new ArrayList<>();
        List<LiveWatchRecordResult> watchRecordResults = liveWatchRecordDaoService.totalAllUserWatchRecordByTimes(startTime, endTime);
        for (LiveWatchRecordResult watchRecordResult : watchRecordResults) {
            records.add(DailyUserActiveRecord.newInstance(watchRecordResult,startTime));
        }
        return dailyUserActiveRecordDaoService.saveAll(records);
    }

}
