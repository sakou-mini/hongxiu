package com.donglaistd.jinli.database.entity.game;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.DailyBetInfoDaoService;
import com.donglaistd.jinli.database.dao.invite.DayGroupContributionRecordDaoService;
import com.donglaistd.jinli.database.dao.invite.DaySubordinatesAgentDaoService;
import com.donglaistd.jinli.database.dao.invite.UserInviteRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.DayGroupContributionRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import com.donglaistd.jinli.listener.GameFinishListener;
import com.donglaistd.jinli.service.UserAgentProcessService;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class UserAgentTest extends BaseTest {
    Logger logger = Logger.getLogger(BaseTest.class.getName());

    @Autowired
    UserInviteRecordDaoService userInviteRecordDaoService;
    @Autowired
    UserAgentProcessService userAgentProcessService;
    @Autowired
    DailyBetInfoDaoService dailyBetInfoDaoService;
    @Autowired
    DayGroupContributionRecordDaoService dayGroupContributionRecordDaoService;
    @Autowired
    DaySubordinatesAgentDaoService daySubordinatesAgentDaoService;
    @Test
    public void bindUserAgentAndgetAgentListTest(){
        User tester1 = createTester(60000, "tester1");
        User tester2 = createTester(60000, "tester2");

        User tester3 = createTester(60000, "tester3");
        User tester4 = createTester(60000, "tester4");

        boolean result = userAgentProcessService.bindUserAgent(tester1, tester2.getId());
        Assert.assertTrue(result);

        result = userAgentProcessService.bindUserAgent(tester2,tester3.getId());   //first:tester3   second:tester4
        Assert.assertFalse(false);   //can't bind ；because already invite others

        result = userAgentProcessService.bindUserAgent(tester3,tester1.getId());   //first:tester1   second:tester2
        Assert.assertTrue(result);


        UserInviteRecord record = userInviteRecordDaoService.findByBeInviteUserId(tester1.getId()); //
        Assert.assertEquals( tester2.getId(),record.getInviteUserId());

        record = userInviteRecordDaoService.findByBeInviteUserId(tester3.getId());
        Assert.assertEquals( tester1.getId(),record.getInviteUserId());

        List<UserInviteRecord> records = userInviteRecordDaoService.findUserInviteRecordForDirect(tester1.getId()); // tester1 所直接代理的人
        Assert.assertEquals(1,records.size());
        Assert.assertEquals(tester3.getId(),records.get(0).getBeInviteUserId());

        records = userInviteRecordDaoService.findUserInviteRecordForInDirect(tester2.getId());    //tester2 所间接代理的所有人
        Assert.assertEquals(1,records.size());
        Assert.assertEquals(tester3.getId(),records.get(0).getBeInviteUserId());

        result = userAgentProcessService.bindUserAgent(tester4,tester1.getId());   //first:tester1   second:tester
        Assert.assertTrue(result);
        records = userInviteRecordDaoService.findUserInviteRecordForDirect(tester1.getId()); // tester1 所直接代理的人
        Assert.assertEquals(2,records.size());
        List<String> beInviteIds = records.stream().map(UserInviteRecord::getBeInviteUserId).collect(Collectors.toList());
        Assert.assertTrue(beInviteIds.containsAll(Lists.newArrayList(tester3.getId(),tester4.getId())));

        records = userInviteRecordDaoService.findUserInviteRecordForInDirect(tester2.getId());
        Assert.assertEquals(2,records.size());
        beInviteIds = records.stream().map(UserInviteRecord::getBeInviteUserId).collect(Collectors.toList());
        Assert.assertTrue(beInviteIds.containsAll(Lists.newArrayList(tester3.getId(),tester4.getId())));
    }

    @Autowired
    GameFinishListener gameFinishListener;

    public User createUser(long coin,String name){
        User user = userBuilder.createNoSavedUser(name, name, "");
        user.setId(name);
        user.setGameCoin(coin);
        return dataManager.saveUser(user);
    }

    @Test
    public void bindAndBetTotalTest(){
        User tester1 = createUser(60000, "tester1");
        User tester2 = createUser(60000, "tester2");
        User tester3 = createUser(60000, "tester3");
        User tester4 = createUser(60000, "tester4");

        userAgentProcessService.bindUserAgent(tester1,tester2.getId());
        Assert.assertFalse(userAgentProcessService.bindUserAgent(tester2,tester1.getId()));
        userAgentProcessService.bindUserAgent(tester3,tester2.getId());
        userAgentProcessService.bindUserAgent(tester4,tester3.getId());

        List<UserInviteRecord> records = userInviteRecordDaoService.findUserInviteRecordForDirect(tester2.getId()); // tester1 所直接代理的人
        Assert.assertEquals(2,records.size());
        List<String> ids = records.stream().map(UserInviteRecord::getBeInviteUserId).collect(Collectors.toList());
        Assert.assertTrue(ids.containsAll(Lists.newArrayList(tester3.getId(),tester1.getId())));

        List<UserInviteRecord> secondInviteRecords = userInviteRecordDaoService.findUserInviteRecordForInDirect(tester2.getId());
        Assert.assertEquals(1,secondInviteRecords.size());
        Assert.assertEquals(tester4.getId(),secondInviteRecords.get(0).getBeInviteUserId());
        //find All agent
        List<String> allInviteUser = userInviteRecordDaoService.findAllInviteUser();
        Assert.assertEquals(2,allInviteUser.size());
        Assert.assertTrue(allInviteUser.containsAll(Lists.newArrayList(tester2.getId(),tester3.getId())));

        //mock bet
        gameFinishListener.updateUserBetRecord(tester1.getId(),500);
        gameFinishListener.updateUserBetRecord(tester3.getId(),600);
        gameFinishListener.updateUserBetRecord(tester4.getId(),700);
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester1.getId(), System.currentTimeMillis(), 500));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester3.getId(), System.currentTimeMillis(), 600));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester4.getId(), System.currentTimeMillis(), 700));
        long endTime = TimeUtil.getCurrentDayStartTime()+TimeUnit.DAYS.toMillis(1);
        userAgentProcessService.totalDayUserAgentIncome(tester2.getId(), TimeUtil.getCurrentDayStartTime(),endTime);

        //queryToday DayGroupContributionRecord
        List<DayGroupContributionRecord> dayGroupContributionRecords = dayGroupContributionRecordDaoService.findDayAgentRecordByTimeAndUserId(tester2.getId(),
                TimeUtil.getCurrentDayStartTime(), endTime);
        Assert.assertEquals(1, dayGroupContributionRecords.size());
        DayGroupContributionRecord record = dayGroupContributionRecords.get(0);
        logger.info(record.toString());
        Assert.assertEquals(1100,record.getFirstAgentTotalBet());
        Assert.assertEquals(700,record.getSecondAgentTotalBet());
        Assert.assertEquals(4.7,record.getAwardCoin(),0);  // 1100*0.003+ 700*0.002   3.3
    }
}
