package com.donglai.statistics.process;

import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.service.statistics.DailyOfServerDataService;
import com.donglai.statistics.db.service.BlogsTotalService;
import com.donglai.statistics.db.service.UserTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DailyTaskProcess {
    @Autowired
    UserTotalService userTotalService;
    @Autowired
    BlogsTotalService blogsTotalService;
    @Autowired
    DailyOfServerDataService dailyOfServerDataService;

    public void totalDailyOfServerData(long startTime, long endTime) {
        //当日新增数据
        long newTouristNum = userTotalService.countNewUserNumByTimeBetween(startTime, endTime, true);
        long newUserNum = userTotalService.countNewUserNumByTimeBetween(startTime, endTime, false);
        Set<String> ipHistory = userTotalService.queryLoginIpListByTimeBetween(startTime, endTime);
        long oldUserNum = userTotalService.countOldUserNumByTime(endTime);//老用户规则：登录过且注册过的用户
        long newCommentNum = blogsTotalService.countNewCommentNumByTimeBetween(startTime, endTime);
        long newBlogsNum = blogsTotalService.countNewBlogsNumByTimeBetween(startTime, endTime);
        long newLikeNum = blogsTotalService.countNewLikeNumByTimeBetween(startTime, endTime);
        //总数据
        long totalUserNum = userTotalService.countNewUserNumByTimeBetween(0, endTime, false);
        long totalTouristNum = userTotalService.countNewUserNumByTimeBetween(0, endTime, true);
        long totalBlogsNum = blogsTotalService.countNewBlogsNumByTimeBetween(0, endTime);
        long totalCommentNum = blogsTotalService.countNewCommentNumByTimeBetween(0, endTime);
        DailyOfServerData dailyOfServerData = DailyOfServerData.newInstance(newTouristNum, newUserNum, oldUserNum, newCommentNum, newBlogsNum, newLikeNum, ipHistory, startTime);
        dailyOfServerData.setTotalUserNum(totalUserNum);
        dailyOfServerData.setTotalTouristNum(totalTouristNum);
        dailyOfServerData.setTotalBlogsNum(totalBlogsNum);
        dailyOfServerData.setTotalCommentNum(totalCommentNum);
        dailyOfServerDataService.save(dailyOfServerData);
    }
}
