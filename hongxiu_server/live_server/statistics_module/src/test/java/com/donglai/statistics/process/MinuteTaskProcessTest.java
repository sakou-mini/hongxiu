package com.donglai.statistics.process;

import com.donglai.common.service.RedisService;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.entity.statistics.TodayOfServerData;
import com.donglai.model.db.service.statistics.DailyOfServerDataService;
import com.donglai.statistics.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static com.donglai.common.constant.RedisConstant.TODAY_OF_SERVERDATA;

public class MinuteTaskProcessTest extends BaseTest {
    @Autowired
    MinuteTaskProcess minuteTaskProcess;
    @Autowired
    DailyOfServerDataService dailyOfServerDataService;
    @Autowired
    RedisService redisService;

    @Test
    public void totalTodayOfServerDataTest() {
        DailyOfServerData dailyOfServerData = DailyOfServerData.newInstance(1, 2, 3, 5, 6, 1, new HashSet<>(), TimeUtil.getBeforeDayStartTime(7));
        dailyOfServerData.setTotalCommentNum(50);
        dailyOfServerData.setTotalBlogsNum(6);
        dailyOfServerDataService.save(dailyOfServerData);
        dailyOfServerData = DailyOfServerData.newInstance(2, 3, 4, 6, 6, 1, new HashSet<>(), TimeUtil.getBeforeDayStartTime(1));
        dailyOfServerData.setTotalCommentNum(20);
        dailyOfServerData.setTotalBlogsNum(3);
        dailyOfServerDataService.save(dailyOfServerData);
        minuteTaskProcess.totalTodayOfServerData();
        TodayOfServerData todayOfServerData = (TodayOfServerData) redisService.get(TODAY_OF_SERVERDATA);
        Assert.assertEquals(-1.0, todayOfServerData.getBlogsDOD(), 0);
        Assert.assertEquals(-1.0, todayOfServerData.getBlogsWOW(), 0);
        Assert.assertEquals(-1.0, todayOfServerData.getCommentWOW(), 0);
        Assert.assertEquals(-1.0, todayOfServerData.getCommentDOD(), 0);

    }
}
