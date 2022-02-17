package com.donglai.web.process;

import com.donglai.common.service.RedisService;
import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.entity.statistics.TodayOfServerData;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.statistics.DailyOfServerDataService;
import com.donglai.web.web.dto.reply.DailyOfServerDataReply;
import com.donglai.web.web.dto.request.DailyOfServerDataRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.donglai.common.constant.RedisConstant.TODAY_OF_SERVERDATA;

@Component
public class IndexProcess {
    @Autowired
    DailyOfServerDataService dailyOfServerDataService;
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    //首页每日数据
    public DailyOfServerDataReply queryDailyOfServerData(DailyOfServerDataRequest request) {
        List<DailyOfServerData> dailyData = dailyOfServerDataService.findByTimeBetween(request.getStartTime(), request.getEndTime());
        DailyOfServerData totalData = totalDailyOfServerData(dailyData, request.getEndTime());
        return new DailyOfServerDataReply(totalData, dailyData);
    }

    private DailyOfServerData totalDailyOfServerData(List<DailyOfServerData> dailyDataList, long endTime) {
        long newTouristNum = 0;
        long newUserNum = 0;
        long oldUserNum = userService.countOldUserNumByTime(endTime);
        long newCommentNum = 0;
        long newBlogsNum = 0;
        long newLikeNum = 0;
        Set<String> ipList = new HashSet<>();
        for (DailyOfServerData dailyData : dailyDataList) {
            newTouristNum += dailyData.getNewTouristNum();
            newUserNum += dailyData.getNewUserNum();
            newCommentNum += dailyData.getNewCommentNum();
            newBlogsNum += dailyData.getNewBlogsNum();
            ipList.addAll(dailyData.getIpHistory());
        }
        return DailyOfServerData.newInstance(newTouristNum, newUserNum, oldUserNum, newCommentNum, newBlogsNum, newLikeNum, ipList, 0);
    }


    public TodayOfServerData queryTodayServerData() {
        return (TodayOfServerData) Optional.ofNullable(redisService.get(TODAY_OF_SERVERDATA)).orElse(new TodayOfServerData());
    }
}
