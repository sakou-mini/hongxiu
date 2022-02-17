package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.http.service.BackOfficeUserService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.*;

import static com.donglaistd.jinli.Constant.LiveStatus.OFFLINE;

public class InsertTestData extends BaseTest {
    @Autowired
    UserDaoService userDaoService;

    @Autowired
    LiveUserDaoService liveUserDaoService;

    @Autowired
    FollowListDaoService followListDaoService;

    @Autowired
    LonghuDaoService longhuDaoService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    RoomDaoService roomService;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    BackOfficeUserService backOfficeUserService;

    //do not change order of these tests , their execute order need to be retained
    @Test
    @Rollback(false)
    public void insertDataTest() {
        var follower = userBuilder.createUser("tester", "tester", "fake_token");

        var followee = userBuilder.createUser("followee", "followee", "fake_token");

        var liveUser = liveUserBuilder.create(followee.getId(),OFFLINE, Constant.PlatformType.PLATFORM_JINLI);
        liveUserDaoService.save(liveUser);

        List<BackOfficeRole> role = new ArrayList<>();
        role.add(BackOfficeRole.ADMIN);
        role.add(BackOfficeRole.LOGIN);
        backOfficeUserService.createBackOfficeUser("xidegui","123456",role);


        List<BackOfficeRole> role2 = new ArrayList<>();
        role2.add(BackOfficeRole.PLATFORM);
        role2.add(BackOfficeRole.LOGIN);
        backOfficeUserService.createBackOfficeUser("platformuser","123456",role2);

        var followList = new FollowList(follower, liveUser);
        followListDaoService.save(followList);
    }

    @Test
    @Rollback(false)
    public void clearAllTest() {
        longhuDaoService.deleteAll();
        followListDaoService.deleteAll();
        liveUserDaoService.deleteAll();
        userDaoService.deleteAll();
        roomService.deleteAll();
        backOfficeUserRepository.deleteAll();
    }

    @After
    @Override
    public void tearDown() {
        //override parent method to avoid data clear
    }

    @Before
    @Override
    public void setUp() {
        //override parent method to avoid data clear
    }
}
