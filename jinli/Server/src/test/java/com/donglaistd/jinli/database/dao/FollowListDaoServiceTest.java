package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.FollowList;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.donglaistd.jinli.Constant.LiveStatus.OFFLINE;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FollowListDaoServiceTest extends BaseTest {
    @Autowired
    FollowListDaoService followListDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;

    @Test

    public void FindEmptyFolloweeByUserTest() {
        var user = userBuilder.createUser("tester", "tester", "fake_token");
        var followers = followListDaoService.findAllByFollower(user);
        Assert.assertEquals(0, followers.size());
    }

    @Test

    public void FindOneFolloweeByUserTest() {
        var follower = userBuilder.createUser("tester", "tester", "fake_token");

        var followee = userBuilder.createUser("followee", "followee", "fake_token");
        var liveUser = liveUserBuilder.create(followee.getId(),OFFLINE, Constant.PlatformType.PLATFORM_JINLI);
        followee.setLiveUserId(liveUser.getId());
        dataManager.saveUser(followee);
        var followList = new FollowList(follower, liveUser);
        followListDaoService.save(followList);

        var followers = followListDaoService.findAllByFollower(follower);
        Assert.assertEquals(1, followers.size());
    }

    @Test

    public void FindMultiFolloweeByUserTest() {
        var follower = userBuilder.createUser("tester", "tester", "fake_token");

        var followee = userBuilder.createUser("followee", "followee", "fake_token");

        var followee2 = userBuilder.createUser("followee2", "followee2", "fake_token");

        var liveUser = liveUserBuilder.create(followee.getId(),OFFLINE, Constant.PlatformType.PLATFORM_JINLI);
        dataManager.saveUser(followee);

        var liveUser2 = liveUserBuilder.create(followee2.getId(),OFFLINE, Constant.PlatformType.PLATFORM_JINLI);
        dataManager.saveUser(followee2);

        var followList = new FollowList(follower, liveUser);
        followListDaoService.save(followList);
        var followList2 = new FollowList(follower, liveUser2);
        followListDaoService.save(followList2);

        var followers = followListDaoService.findAllByFollower(follower);
        Assert.assertEquals(2, followers.size());
    }

    @Test

    public void findFolloweeIdsByFollowerIdTest() {
        var follower = userBuilder.createUser("tester", "tester", "fake_token");

        var follower2 = userBuilder.createUser("tester2", "tester2", "fake_token");

        var followee = userBuilder.createUser("followee", "followee", "fake_token");
        
        var liveUser = liveUserBuilder.create(followee.getId(),OFFLINE, Constant.PlatformType.PLATFORM_JINLI);
        dataManager.saveUser(followee);

        var followList1 = new FollowList(follower, liveUser);
        followListDaoService.save(followList1);
        var followList2 = new FollowList(follower2, liveUser);
        followListDaoService.save(followList2);

        List<String> record = followListDaoService.findFolloweeIdsByFollowerId(liveUser.getId());
        Assert.assertEquals(2, record.size());

        FollowList byFollowerIdAndFollower = followListDaoService.findByFollowerIdAndFollower(liveUser.getId(), follower.getId());
        Assert.assertNotNull(byFollowerIdAndFollower);
    }
}
