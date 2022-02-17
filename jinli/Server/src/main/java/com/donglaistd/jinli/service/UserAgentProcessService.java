package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DailyBetInfoDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.invite.*;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.*;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserAgentProcessService {
    @Value("${agent.first.award.rate}")
    private double firstAgentRate;

    @Value("${agent.second.award.rate}")
    private double secondAgentRate;

    @Value("${agent.invite.coin}")
    private long inviteCoin;

    @Autowired
    DataManager dataManager;

    @Autowired
    UserAgentDaoService userAgentDaoService;

    @Autowired
    UserInviteRecordDaoService userInviteRecordDaoService;

    @Autowired
    UserBetRecordDaoService userBetRecordDaoService;

    @Autowired
    DailyBetInfoDaoService dailyBetInfoDaoService;

    @Autowired
    DayGroupContributionRecordDaoService dayGroupContributionRecordDaoService;
    @Autowired
    DaySubordinatesAgentDaoService daySubordinatesAgentDaoService;
    @Autowired
    UserDaoService userDaoService;

    public String getUserShareUrl(String userId) {
        return "/share/shareApplication?inviteCode=" + userId;
    }


    public boolean bindUserAgent(User user, String inviteUserId) {
        if (inviteUserId.equals(user.getId()) || userInviteRecordDaoService.isBeInvite(user.getId())
                || userInviteRecordDaoService.isInviteOthers(user.getId()) || userDaoService.findById(inviteUserId) == null)
            return false;
        UserInviteRecord userInviteRecord = UserInviteRecord.newInstance(inviteUserId, user.getId());
        userInviteRecordDaoService.save(userInviteRecord);
        userAgentDaoService.findOrCreateUserAgent(inviteUserId);
        userAgentDaoService.findOrCreateUserAgent(user.getId());
        userAgentDaoService.addUserAgentCoin(inviteUserId, BigDecimal.valueOf(inviteCoin));
        userAgentDaoService.addUserAgentCoin(user.getId(), BigDecimal.valueOf(inviteCoin));
        return true;
    }

    public Jinli.UserPromotionInfo getUserPromotionInfo(String userId) {
        UserAgent userAgent = userAgentDaoService.findByUserId(userId);
        if (userAgent == null) {
            userAgent = UserAgent.newInstance(userId);
            userAgentDaoService.save(userAgent);
        }
        boolean canBeInvite = !userInviteRecordDaoService.isBeInvite(userId) && !userInviteRecordDaoService.isInviteOthers(userId);
        long startTime = TimeUtil.getBeforeDayStartTime(1);
        long endTime = TimeUtil.getCurrentDayStartTime();
        List<DayGroupContributionRecord> records = dayGroupContributionRecordDaoService.findDayAgentRecordByTimeAndUserId(userId, startTime, endTime);
        double yesterdayIncome = records.isEmpty() ? 0 : records.get(0).getAwardCoin();
        return Jinli.UserPromotionInfo.newBuilder()
                .setTotalIncome(userAgent.getTotalIncome())
                .setCoin(userAgent.getLeftIncome())
                .setCanBeInvite(canBeInvite)
                .setShareUrl(getUserShareUrl(userId))
                .setYesterdayIncome(yesterdayIncome)
                .setTodayInviteIncome(0).build();
    }

    public void totalDayUserAgentRecord(long startTime, long endTime) {
        List<String> ids = userInviteRecordDaoService.findAllInviteUser();
        ids.forEach(id -> totalDayUserAgentIncome(id, startTime, endTime));
    }

    public void totalDayUserAgentIncome(String userId, long starTime, long endTime) {
        long firstAgentTotalBetFlow = 0;
        long secondAgentTotalBetFlow = 0;
        Set<String> firstAgentIds = userInviteRecordDaoService.findUserInviteRecordForDirect(userId).stream().map(UserInviteRecord::getBeInviteUserId).collect(Collectors.toSet());
        List<DaySubordinatesAgent> daySubordinatesAgents = new ArrayList<>();
        for (String firstAgentId : firstAgentIds) {
            //1 firstAgent day flow
            DailyBetInfo dailyBetInfo = dailyBetInfoDaoService.findByUserIdAndTimeTimeBetween(firstAgentId, starTime, endTime);
            long dayBetCoin = dailyBetInfo == null ? 0 : dailyBetInfo.getBetAmount();
            //2 secondAgent day flow
            Set<String> secondAgentIds =
                    userInviteRecordDaoService.findUserInviteRecordForDirect(firstAgentId).stream().map(UserInviteRecord::getBeInviteUserId).collect(Collectors.toSet());
            secondAgentTotalBetFlow += dailyBetInfoDaoService.findByBetUserIdInAndTimeBetween(secondAgentIds, starTime, endTime).stream().mapToLong(DailyBetInfo::getBetAmount).sum();
            firstAgentTotalBetFlow += dayBetCoin;
            daySubordinatesAgents.add(totalSubordinatesFlow(firstAgentId, dayBetCoin, secondAgentIds));
        }
        BigDecimal firstAgentAward = BigDecimal.valueOf(firstAgentTotalBetFlow).multiply(BigDecimal.valueOf(firstAgentRate)).setScale(2, RoundingMode.FLOOR);
        BigDecimal secondAgentAward = BigDecimal.valueOf(secondAgentTotalBetFlow).multiply(BigDecimal.valueOf(secondAgentRate)).setScale(2, RoundingMode.FLOOR);
        BigDecimal income = firstAgentAward.add(secondAgentAward);
        userAgentDaoService.addUserAgentCoin(userId, income);
        DayGroupContributionRecord dayGroupContributionRecord = DayGroupContributionRecord.newInstance(userId, firstAgentTotalBetFlow, secondAgentTotalBetFlow, income, endTime);
        dayGroupContributionRecordDaoService.save(dayGroupContributionRecord);
        daySubordinatesAgentDaoService.saveAll(daySubordinatesAgents);
    }

    private DaySubordinatesAgent totalSubordinatesFlow(String agentUserId, long dayBetFlow, Set<String> secondAgentIds) {
        long totalBetCoin = Optional.ofNullable(userBetRecordDaoService.findByUserId(agentUserId)).orElse(UserBetRecord.newInstance(agentUserId)).getTotalBetCoin();
        long totalBetNum = userBetRecordDaoService.findBetRecordsByIds(secondAgentIds).stream().mapToLong(UserBetRecord::getTotalBetCoin).sum();
        return DaySubordinatesAgent.newInstance(agentUserId, dayBetFlow, totalBetCoin, totalBetNum);
    }

    private long getUserTodayInviteCoinAmount(String userId) {
        List<UserInviteRecord> records = userInviteRecordDaoService.findUserInviteAndBeInviteRecordForToday(userId);
        return records.size() * inviteCoin;
    }

}
