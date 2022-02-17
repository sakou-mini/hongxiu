package com.donglai.statistics.process;

import com.donglai.common.service.RedisService;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.entity.statistics.TodayOfServerData;
import com.donglai.model.db.service.statistics.DailyOfServerDataService;
import com.donglai.statistics.db.service.BlogsTotalService;
import com.donglai.statistics.db.service.UserTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static com.donglai.common.constant.RedisConstant.TODAY_OF_SERVERDATA;

@Service
public class MinuteTaskProcess {
    @Value("${today.server.data.expire.time}")
    private long todayOfServerDataExpireTime;

    @Autowired
    UserTotalService userTotalService;
    @Autowired
    BlogsTotalService blogsTotalService;
    @Autowired
    DailyOfServerDataService dailyOfServerDataService;
    @Autowired
    RedisService redisService;

    public void totalTodayOfServerData() {
        long now = System.currentTimeMillis();
        long startTime = TimeUtil.getCurrentDayStartTime();
        long totalSignUpNum = userTotalService.countNewUserNumByTimeBetween(0, now, false);
        long todaySignUpNum = userTotalService.countNewUserNumByTimeBetween(startTime, now, false);
        long totalTouristNum = userTotalService.countNewUserNumByTimeBetween(0, now, true);
        long todayTouristNum = userTotalService.countNewUserNumByTimeBetween(startTime, now, true);
        long totalBlogsNum = blogsTotalService.countNewBlogsNumByTimeBetween(0, now);
        long todayBlogsNum = blogsTotalService.countNewBlogsNumByTimeBetween(startTime, now);
        DailyOfServerData yesterdayServerData = Optional.ofNullable(dailyOfServerDataService.findByTime(TimeUtil.getBeforeDayStartTime(1))).orElse(new DailyOfServerData());
        DailyOfServerData weekAgoServerData = Optional.ofNullable(dailyOfServerDataService.findByTime(TimeUtil.getBeforeDayStartTime(7))).orElse(new DailyOfServerData());
        //动态周同比
        double blogsWOW = calculateGrowthRate(totalBlogsNum, weekAgoServerData.getTotalBlogsNum());
        //动态日同比
        double blogsDOD = calculateGrowthRate(totalBlogsNum, yesterdayServerData.getTotalBlogsNum());
        long totalCommentNum = blogsTotalService.countNewCommentNumByTimeBetween(0, now);
        long todayCommentNum = blogsTotalService.countNewCommentNumByTimeBetween(startTime, now);
        //日环比
        double commentWOW = calculateGrowthRate(totalCommentNum, weekAgoServerData.getTotalCommentNum());
        double commentDOD = calculateGrowthRate(totalCommentNum, yesterdayServerData.getTotalCommentNum());
        TodayOfServerData todayOfServerData = TodayOfServerData.newInstance(totalSignUpNum, todaySignUpNum, totalTouristNum, todayTouristNum, totalBlogsNum, todayBlogsNum, blogsWOW,
                blogsDOD, totalCommentNum, todayCommentNum, commentWOW, commentDOD, now);
        redisService.set(TODAY_OF_SERVERDATA, todayOfServerData, todayOfServerDataExpireTime);
    }

    private double calculateGrowthRate(long nowData, long fromData) {
        double rate;
        if (fromData <= 0 && nowData > 0) rate = 1;
        else if (fromData <= 0) rate = 0;
        else {
            BigDecimal nowDataDecimal = BigDecimal.valueOf(nowData);
            BigDecimal fromDataDecimal = BigDecimal.valueOf(fromData);
            rate = (nowDataDecimal.subtract(fromDataDecimal)).divide(fromDataDecimal, 2, RoundingMode.HALF_UP).doubleValue();
            ;
        }
        return rate;
    }

}
