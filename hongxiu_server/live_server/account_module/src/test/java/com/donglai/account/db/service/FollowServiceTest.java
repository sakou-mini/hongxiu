package com.donglai.account.db.service;

import com.donglai.account.BaseTest;
import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.FollowListService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FollowServiceTest extends BaseTest {
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    FollowListService followListService;

    @Test
    public void findByFollowerByLeaderIdAndNameTest() {
        User user1 = userBuilder.createUser("小张", "", "", "", "pwd");
        User user2 = userBuilder.createUser("小明", "", "", "", "pwd");
        User user3 = userBuilder.createUser("小红", "", "", "", "pwd");
        User user4 = userBuilder.createUser("刘", "", "", "", "pwd");
        FollowList followList = FollowList.newInstance(user.getId(), user1.getId());
        followList.setAlias("别名小张");
        followListService.save(followList);
        FollowList followList2 = FollowList.newInstance(user.getId(), user2.getId());
        followList2.setAlias("别名小明");
        followListService.save(followList2);
        FollowList followList3 = FollowList.newInstance(user.getId(), user3.getId());
        followList3.setAlias("别名小红");
        followListService.save(followList3);

        FollowList followList4 = FollowList.newInstance(user4.getId(), user1.getId());
        followList4.setAlias("别名小刘");
        followListService.save(followList4);

        FollowList followList5 = FollowList.newInstance(user4.getId(), user2.getId());
        followList5.setAlias("别名小明");
        followListService.save(followList5);

        List<FollowList> followLists = followListService.findByFollowerIdAndLeaderName(user2.getId(), "别名");
        Assert.assertEquals(2, followLists.size());

        followLists = followListService.findByFollowerIdAndLeaderName(user2.getId(), "刘");
        Assert.assertEquals(1, followLists.size());
    }
}
