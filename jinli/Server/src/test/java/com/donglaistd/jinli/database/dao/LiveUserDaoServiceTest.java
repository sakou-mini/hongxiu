package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;

public class LiveUserDaoServiceTest extends BaseTest {
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;

    @Test
    public void SaveLiveUserTest() {
        var user = userBuilder.createUser("tester", "tester", "fake_token");
        var liveUser = liveUserBuilder.create(user.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        user.setLiveUserId(liveUser.getId());
        dataManager.saveUser(user);
        var expectLiveUser = liveUserDaoService.findById(user.getLiveUserId());
        Assert.assertNotNull(expectLiveUser);
        Assert.assertEquals(ONLINE, expectLiveUser.getLiveStatus());
    }

    @Test
    public void genderGroupTest(){
        var user1 = userBuilder.createUser("tester", "tester", "fake_token");
        var liveUser1 = liveUserBuilder.create(user1.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        liveUser1.setGender(Constant.GenderType.FEMALE);
        liveUserDaoService.save(liveUser1);

        user1.setLiveUserId(liveUser1.getId());
        dataManager.saveUser(user1);

        var user2 = userBuilder.createUser("tester2", "tester2", "fake_token2");
        var liveUser2 = liveUserBuilder.create(user2.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI);
        liveUser2.setGender(Constant.GenderType.MALE);
        liveUserDaoService.save(liveUser2);

        user2.setLiveUserId(liveUser2.getId());
        dataManager.saveUser(user2);

        Map<Constant.GenderType, Long> genderMap = liveUserDaoService.groupLiveUserByGender(Constant.PlatformType.PLATFORM_JINLI);
        Assert.assertEquals(2,(long)genderMap.get(Constant.GenderType.FEMALE));
        Assert.assertEquals(1,(long)genderMap.get(Constant.GenderType.MALE));
    }
}
