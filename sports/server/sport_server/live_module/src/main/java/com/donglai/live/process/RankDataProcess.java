package com.donglai.live.process;

import com.donglai.common.util.TimeUtil;
import com.donglai.live.util.MockDataUtil;
import com.donglai.model.db.entity.live.GiftLog;
import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.GiftLogService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.Live;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.donglai.protocol.Constant.QueryTimeType.TODAY;

@Component
public class RankDataProcess {
    @Value("${gift.rank.top.num}")
    private int rankTopNum;
    private final UserService userService;
    private final GiftLogService giftLogService;

    public RankDataProcess(UserService userService, GiftLogService giftLogService) {
        this.userService = userService;
        this.giftLogService = giftLogService;
    }

    public List<Live.GiftRankInfo> buildRankInfo(GiftRank giftRank) {
        if (giftRank == null || giftRank.getRankInfos() == null) return new ArrayList<>();
        List<Live.GiftRankInfo> giftInfoList = new ArrayList<>();
        Live.GiftRankInfo giftRankInfo;
        for (GiftRank.RankInfo rankInfo : giftRank.getRankInfos()) {
            giftRankInfo = Live.GiftRankInfo.newBuilder().setAmount((int) rankInfo.getScore())
                    .setUserInfo(userService.findById(rankInfo.getUserId()).toDetailProto()).build();
            giftInfoList.add(giftRankInfo);
        }
        //TODO add mockData WILL DELETE
        giftInfoList.addAll(MockDataUtil.mockGiftRackData(10));
        giftInfoList.sort(Comparator.comparing(Live.GiftRankInfo::getAmount).reversed());
        int size = giftInfoList.size();
        return giftInfoList.subList(0, Math.min(rankTopNum, size));
    }


    public List<GiftLog> getUserGiftLogRankByQueryTime(String userId, Constant.QueryTimeType queryTimeType) {
        long endTime = System.currentTimeMillis();
        long startTime = -1;
        if (Objects.equals(TODAY, queryTimeType)) startTime = TimeUtil.getCurrentDayStartTime();
        return giftLogService.findByReceiverUserIdAndGroupSenderIdBetweenTimes(userId, rankTopNum, startTime, endTime);
    }

    public List<Live.GiftRankInfo> buildGiftLogToGiftRankInfo(List<GiftLog> giftLogs) {
        List<Live.GiftRankInfo> giftInfoList = new ArrayList<>();
        Live.GiftRankInfo giftRankInfo;
        for (GiftLog giftLog : giftLogs) {
            giftRankInfo = Live.GiftRankInfo.newBuilder().setAmount((int) giftLog.getSendAmount())
                    .setUserInfo(userService.findById(giftLog.getSenderId()).toDetailProto()).build();
            giftInfoList.add(giftRankInfo);
        }
        //TODO add mockData WILL DELETE
        giftInfoList.addAll(MockDataUtil.mockGiftRackData(10));
        giftInfoList.sort(Comparator.comparing(Live.GiftRankInfo::getAmount).reversed());
        int size = giftInfoList.size();
        return giftInfoList.subList(0, Math.min(rankTopNum, size));
    }
}
