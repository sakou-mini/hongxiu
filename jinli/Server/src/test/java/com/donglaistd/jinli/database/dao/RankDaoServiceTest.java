package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.DailyIncome;
import com.donglaistd.jinli.database.entity.rank.Rank;
import com.donglaistd.jinli.database.entity.rank.RankManager;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class RankDaoServiceTest extends BaseTest {
    @Autowired
    private DailyIncomeDaoService dailyIncomeDaoService;
    @Autowired
    private RankDaoService rankDaoService;
    @Autowired
    private RankManager rankManager;
    @Value("${data.rank.page}")
    private int PAGE;
    @Value("${data.rank.size}")
    private int SIZE;

    @Test
    @Rollback()
    public void testinsertDailyIncome() {
        DailyIncome insert = dailyIncomeDaoService.insert("1", 500);
        dailyIncomeDaoService.delete(insert);
    }

    @Test
    public void testTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date endTime = calendar.getTime();  // 结束时间   2020-06-17 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY,-24);
        Date startTime = calendar.getTime();  // 开始时间  2020-06-16 00:00:00
    }

    @Test
    public void testStartTimeAndEndTime() {
        long endTime = rankManager.todayStartTime(System.currentTimeMillis());
        long startTime = rankManager.yesterdayStartTime(endTime);
        Assert.assertEquals(RankManager.DAY_MILLISECOND,endTime -startTime);
    }
    @Test
    public void testInsertRank() {
        Rank rank = new Rank();
        rank.setRankType(Constant.RankType.INCOME);
        rank.setTime(System.currentTimeMillis());
        rank.setUserId("2");
        Document document = new Document("level", 5);
        rank.setExtra(document);
    }

    @Test
    public void testFindAllByRankTypeIsAndTimeBetween() {
        List<Rank> all = rankDaoService.findAllByRankTypeIsAndTimeBetween(Constant.RankType.LEVEL,1593336039000l, 1593593274000l);
    }


}
