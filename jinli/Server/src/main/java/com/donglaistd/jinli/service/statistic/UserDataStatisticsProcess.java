package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.constant.StatisticEnum;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.util.TimeUtil;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserDataStatisticsProcess {
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;
    @Autowired
    VerifyUtil verifyUtil;

    public void totalRegisterUserDataStatistics(Jinli.RegisterRequest registerRequest, String userId, Constant.PlatformType platform){
        UserDataStatistics registerUserData = UserDataStatistics.newInstance(userId, true);
        registerUserData.setBrand(registerRequest.getBrand());
        registerUserData.setOs(registerRequest.getOs());
        registerUserData.setResolution(registerRequest.getResolution());
        registerUserData.setAppVersion(registerRequest.getAppVersion());
        registerUserData.setMobileModel(registerRequest.getMobileModel());
        registerUserData.setPlatformType(platform);
        userDataStatisticsDaoService.save(registerUserData);
    }

    public void totalLoginUserDataStatistics(Jinli.LoginRequest loginRequest, User user) {
       /* if(verifyUtil.checkIsLiveUser(user)) {
            userDataStatisticsDaoService.deleteTodayUserDataStatistics(user.getId());
            return;
        }*/
        UserDataStatistics todayUserData = userDataStatisticsDaoService.findUserDataByTime(user.getId(), TimeUtil.getCurrentDayStartTime(),false);
        if(todayUserData == null)
            todayUserData = UserDataStatistics.newInstance(user.getId(), false);
        UserDataStatistics yesterdayUserData = userDataStatisticsDaoService.findUserDataByTime(user.getId(), TimeUtil.yesterdayStartTime(System.currentTimeMillis()),false);
        if(yesterdayUserData == null)
            todayUserData.setActiveDays(1);
        else if(!todayUserData.hasLogin())
            todayUserData.setActiveDays(yesterdayUserData.getActiveDays()+1);
        todayUserData.addIpRecord(user.getLastIp());
        todayUserData.setBrand(loginRequest.getBrand());
        todayUserData.setOs(loginRequest.getOs());
        todayUserData.setResolution(loginRequest.getResolution());
        todayUserData.setMobileModel(loginRequest.getMobileModel());
        todayUserData.setAppVersion(loginRequest.getAppVersion());
        todayUserData.setPlatformType(user.getPlatformType());
        userDataStatisticsDaoService.save(todayUserData);
    }

    public Map<StatisticEnum.ActiveDaysEnum,Long> totalActiveDaysForGroupByTimes(long startTime, long endTime) {
        Map<Integer, Long> activeDayGroup = userDataStatisticsDaoService.totalMaxActiveDaysGroupByUserIdAndTimeBetween(startTime, endTime).stream()
                .collect(Collectors.groupingBy(UserDataStatistics::getActiveDays, Collectors.counting()));
        Map<StatisticEnum.ActiveDaysEnum, Long> groupResult = activeDayGroup.entrySet().stream()
                .collect(Collectors.groupingBy(entry -> StatisticEnum.getActiveTypeByDay(entry.getKey()), Collectors.summingLong(Map.Entry::getValue)));
        groupResult.put(StatisticEnum.ActiveDaysEnum.ACTIVE_NEW_USER, userDataStatisticsDaoService.countRegisterUserNumByTimeBetween(startTime, endTime));
        for (StatisticEnum.ActiveDaysEnum value : StatisticEnum.ActiveDaysEnum.values()) {
            groupResult.putIfAbsent(value, 0L);
        }
        return groupResult;
    }

    public void totalUserOnlineTime(User user){
        UserDataStatistics userData = userDataStatisticsDaoService.findUserDataByTime(user.getId(), TimeUtil.getCurrentDayStartTime(),false);
        if(userData==null) return;
        userData.addOnlineTime(System.currentTimeMillis() - user.getLastLoginTime());
        userDataStatisticsDaoService.save(userData);
    }
}
