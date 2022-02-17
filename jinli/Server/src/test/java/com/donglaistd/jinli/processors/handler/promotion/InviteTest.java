package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.dao.DailyBetInfoDaoService;
import com.donglaistd.jinli.database.dao.invite.DayGroupContributionRecordDaoService;
import com.donglaistd.jinli.database.dao.invite.DaySubordinatesAgentDaoService;
import com.donglaistd.jinli.database.dao.invite.UserInviteRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.listener.GameFinishListener;
import com.donglaistd.jinli.service.UserAgentProcessService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class InviteTest extends BaseTest {

    @Autowired
    UserInviteRecordDaoService userInviteRecordDaoService;
    @Autowired
    UserAgentProcessService userAgentProcessService;
    @Autowired
    DayGroupContributionRecordDaoService dayGroupContributionRecordDaoService;
    @Autowired
    DaySubordinatesAgentDaoService daySubordinatesAgentDaoService;
    @Autowired
    GameFinishListener gameFinishListener;
    @Autowired
    DailyBetInfoDaoService dailyBetInfoDaoService;

    public User createUser(long coin, String name){
        User user = userBuilder.createNoSavedUser(name, name, "");
        user.setId(name);
        user.setGameCoin(coin);
        return dataManager.saveUser(user);
    }
    @Test
    public void verifyBindUserAgent() {
        User tester1 = createUser(60000, "tester1");
        User tester2 = createUser(60000, "tester2");
        boolean result = userAgentProcessService.bindUserAgent(tester1, tester2.getId());
        Assert.assertTrue(result);
        result = userAgentProcessService.bindUserAgent(tester2,tester1.getId());
        Assert.assertFalse(result);
    }
}
