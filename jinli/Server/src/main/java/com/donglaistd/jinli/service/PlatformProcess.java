package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.dao.GiftOrderDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.platform.PlatformRechargeRecordDaoService;
import com.donglaistd.jinli.database.entity.GiftOrder;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.RechargeLog;
import com.donglaistd.jinli.util.PlatformUtil;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class PlatformProcess {
    @Autowired
    PlatformRechargeRecordDaoService platformRechargeRecordDaoService;
    @Autowired
    GiftOrderDaoService giftOrderDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    GiftLogDaoService giftLogDaoService;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    PlatformUtil platformUtil;

    public PageInfo<RechargeLog> queryRechargeRecord(PageRequest pageRequest, Long startTime, Long endTime, String userId, String displayName, Constant.PlatformType platformType){
        switch (platformType){
            case PLATFORM_Q: return getPlatformQRechargeLog(pageRequest, startTime, endTime, userId, displayName);
            case PLATFORM_T: return getPlatformTRechargeLog(pageRequest, startTime, endTime, userId, displayName);
            default: return new PageInfo<>();
        }
    }
    private PageInfo<RechargeLog> getPlatformTRechargeLog(PageRequest pageRequest, Long startTime, Long endTime, String userId, String displayName){
        List<RechargeLog> rechargeLog = new ArrayList<>();
        PageInfo<PlatformRechargeRecord> rechargeRecord = platformRechargeRecordDaoService.pageQuery(pageRequest, startTime,endTime,userId, displayName);
        User user;
        for (PlatformRechargeRecord record : rechargeRecord.getContent()) {
            user = userDaoService.findById(record.getUserId());
            int platformCode = platformUtil.getUserPlatformCreateTag(user);
            rechargeLog.add(RechargeLog.newInstance(user, record,platformCode));
        }
        return new PageInfo<>(rechargeLog, rechargeRecord.getTotal());
    }

    private PageInfo<RechargeLog> getPlatformQRechargeLog(PageRequest pageRequest, Long startTime, Long endTime, String userId, String displayName){
        PageInfo<GiftOrder> giftOrderPageInfo = giftOrderDaoService.pageQueryPlatformQGiftOrders(pageRequest, startTime, endTime, userId, displayName);
        List<RechargeLog> rechargeLogs = new ArrayList<>(giftOrderPageInfo.getContent().size());
        User user;
        for (GiftOrder giftOrder : giftOrderPageInfo.getContent()) {
            user = userDaoService.findById(giftOrder.getSenderId());
            int platformCode = platformUtil.getUserPlatformCreateTag(user);
            rechargeLogs.add(RechargeLog.newInstance(user, giftOrder,platformCode));
        }
        return new PageInfo<>(rechargeLogs, giftOrderPageInfo.getTotal());
    }

    @Autowired
    FansContributionService fansContributionService;

    public void resetLiveUserCoinAndIllegalityGiftLogByPlatform(Constant.PlatformType platform) {
        liveUserDaoService.resetPlatformUserCoin(platform);
        liveUserDaoService.resetPlatformUserCoin(Constant.PlatformType.PLATFORM_Q);
        List<GiftLog> giftLogs = giftLogDaoService.findByPlatform(platform);
        for (GiftLog giftLog : giftLogs) {
            LiveUser liveUser = liveUserDaoService.findByUserId(giftLog.getSenderId());
            if (liveUser != null && verifyUtil.verifyIsLiveUser(liveUser) && !Objects.equals(Constant.PlatformType.PLATFORM_JINLI, liveUser.getPlatformType())){
                giftLogDaoService.deleteGiftLog(giftLog);
            }
        }
    }
}
