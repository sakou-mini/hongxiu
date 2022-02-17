package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BetInfoDaoServiceTest extends BaseTest {
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    private DailyBetInfoDaoService dailyBetInfoDaoService;

    @Autowired
    private DataManager dataManager;
    @Test
    public void testFindByTimeBetween() {
        long now = System.currentTimeMillis();
        DailyBetInfo test1 = new DailyBetInfo("1","2",now-1000*5,100,120,"1", Constant.GameType.NIUNIU);
        DailyBetInfo test2 = new DailyBetInfo("1","2",now-1000*5,1000,-20,"1", Constant.GameType.NIUNIU);
        DailyBetInfo test3 = new DailyBetInfo("1","200",now-1000*5,10000,12000,"1", Constant.GameType.NIUNIU);
        dailyBetInfoDaoService.saveAll(Arrays.asList(test1,test2,test3));
        List<DailyBetInfo> between = dailyBetInfoDaoService.findByTimeBetweenAndSortByWin(10, -1, now);
        between.stream().forEach(System.err::println);
    }

    @Test
    public void testUpdateUserLevel() {
        User user = userBuilder.createUser("test8848_AccountName", "test8848", ",", "test8848_token", true);
        user.updateLevel(15);
        Assert.assertEquals(2, user.getLevel());
    }

    @Test
    public void testSaveRoom() {
        room.addAdministrator("8848");
        dataManager.saveRoom(room);
        Room onlineRoom = DataManager.findOnlineRoom(room.getId());
        System.err.println(onlineRoom);
    }

}
