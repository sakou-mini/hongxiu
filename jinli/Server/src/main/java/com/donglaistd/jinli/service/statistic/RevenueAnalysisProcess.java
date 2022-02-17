package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.http.entity.BetRecordSummary;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.statistic.UserGiftDetailData;
import com.donglaistd.jinli.util.PlatformUtil;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RevenueAnalysisProcess {
    @Autowired
    DailyBetInfoDaoService dailyBetInfoDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    GiftLogDaoService giftLogDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    PlatformUtil platformUtil;

    public PageInfo<UserGiftDetailData> findGiftSpendDetail(long startTime, long endTime, String userId, String userName , String roomId, int page, int size, Constant.PlatformType platform){
        PageInfo<GiftLog> pageGiftLogs = giftLogDaoService.findGiftLogByTimesAndSendUserIdAndName(startTime, endTime, userId, userName, roomId, PageRequest.of(page, size),platform);
        List<UserGiftDetailData> giftDetailData = buildUserGiftDetailDataByGiftLogs(pageGiftLogs.getContent());
        return new PageInfo<>(giftDetailData, pageGiftLogs.getPageable(), pageGiftLogs.getTotal());
    }

    public PageInfo<UserGiftDetailData> findGiftIncomeRecords(PageRequest pageRequest,long startTime, long endTime,String liveUserId, String liveUserName,String roomId,
                                                              Constant.PlatformType platform){
        PageInfo<GiftLog> pageGiftLogs = giftLogDaoService.findGiftLogByTimesAndReceiveUserIdAndName(pageRequest, startTime, endTime, liveUserId, liveUserName,roomId,platform);
        List<UserGiftDetailData> giftDetailData = buildUserGiftDetailDataByGiftLogs(pageGiftLogs.getContent());
        return new PageInfo<>(giftDetailData, pageGiftLogs.getPageable(), pageGiftLogs.getTotal());
    }

    private List<UserGiftDetailData> buildUserGiftDetailDataByGiftLogs(Collection<GiftLog> giftLogs){
        List<UserGiftDetailData> giftDetailData = new ArrayList<>(giftLogs.size());
        User sender;
        User receiver;
        Room room;
        for (GiftLog giftLog : giftLogs) {
            sender = userDaoService.findById(giftLog.getSenderId());
            receiver = userDaoService.findById(giftLog.getReceiveId());
            room = roomDaoService.findByLiveUser(liveUserDaoService.findByUserId(giftLog.getReceiveId()));
            int platformTag = platformUtil.getUserPlatformCreateTag(sender);
            giftDetailData.add(new UserGiftDetailData(sender, receiver, room, giftLog,platformTag));
        }
        return giftDetailData;
    }

    private void initGameIncomeData(Map<Constant.GameType, Long> gameBetMap){
        for (Constant.GameType gameType : Constant.GameType.values()) {
            if(gameType.getNumber() <= Constant.GameType.EMPTY_VALUE )
                continue;
            if(gameType.getNumber() > Constant.GameType.LONGHU_VALUE) break;
            gameBetMap.putIfAbsent(gameType, 0L);
        }
    }

    public BetRecordSummary getBetRecordSummary(){
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = System.currentTimeMillis();
        //bet total Amount
        DailyBetInfo todayBetInfoByTimes = Optional.ofNullable(dailyBetInfoDaoService.findTotalBetInfoByTimes(startTime, endTime)).orElse(new DailyBetInfo());
        DailyBetInfo totalAllBetInfo = Optional.ofNullable(dailyBetInfoDaoService.findTotalAllBetInfo()).orElse(new DailyBetInfo());
        //game bet Amount
        Map<Constant.GameType, Long> todayGameBetMap = dailyBetInfoDaoService.totalGameIncomeByTimes(startTime, endTime);
        Map<Constant.GameType, Long> totalGameBetMap = dailyBetInfoDaoService.totalAllGameIncome();
        BetRecordSummary betRecordSummary = new BetRecordSummary(todayBetInfoByTimes.getBetAmount(),todayBetInfoByTimes.getWin(),
                totalAllBetInfo.getBetAmount(),totalAllBetInfo.getWin());
        initGameIncomeData(todayGameBetMap);
        initGameIncomeData(totalGameBetMap);
        betRecordSummary.setTodayGameIncome(todayGameBetMap);
        betRecordSummary.setAllGameIncome(totalGameBetMap);
        return betRecordSummary;
    }

    public PageInfo<DailyBetInfo> getPageBeRecord(int page,int size){
        PageInfo<DailyBetInfo> pageInfo = dailyBetInfoDaoService.findBetPageInfo(page, size);
        for (DailyBetInfo betInfo : pageInfo.getContent()) {
            User user = userDaoService.findById(betInfo.getBetUserId());
            if(Objects.nonNull(user)) {
                betInfo.setDisplayName(user.getDisplayName());
                betInfo.setAvatarUrl(user.getAvatarUrl());
            }
        }
        return pageInfo;
    }
}
