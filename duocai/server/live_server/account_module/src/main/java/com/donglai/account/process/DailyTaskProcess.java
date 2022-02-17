package com.donglai.account.process;

import com.donglai.account.db.service.UserTotalService;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.service.statistics.DailyOfServerDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DailyTaskProcess {
    @Autowired
    UserTotalService userTotalService;

    @Autowired
    DailyOfServerDataService dailyOfServerDataService;

    public void totalDailyOfServerData(long startTime, long endTime) {
        //当日新增数据
        long newUserNum = userTotalService.countNewUserNumByTimeBetween(startTime, endTime);
        long oldUserNum = userTotalService.countOldUserNumByTime(endTime);//老用户规则：登录过且注册过的用户
        List<User> userList = userTotalService.queryLoginUserNumByTimeBetween(startTime, endTime);
        //今日登录人数
        long loginUserNum = userList.size();
        Set<String> ipHistory = userList.stream().filter(user -> !StringUtils.isNullOrBlank(user.getIp())).map(User::getIp).collect(Collectors.toSet());;
        //总数据
        long totalUserNum = userTotalService.countNewUserNumByTimeBetween(0, endTime);
        DailyOfServerData dailyOfServerData = DailyOfServerData.newInstance(newUserNum, oldUserNum, ipHistory,loginUserNum, startTime);
        dailyOfServerDataService.save(dailyOfServerData);
    }
}
