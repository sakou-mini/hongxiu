package com.donglai.blogs.test.service;

import com.donglai.blogs.service.UserBlogsLikeRankCacheService;
import com.donglai.blogs.test.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserBlogsRankServiceTest extends BaseTest {
    @Autowired
    UserBlogsLikeRankCacheService userBlogsLikeRankCacheService;

    @Test
    public void test() {
        for (int i = 0; i < 10; i++) {
            userBlogsLikeRankCacheService.updateUserBlogsLikeNum("user" + i, (long) i);
        }
        List<String> userIds = userBlogsLikeRankCacheService.listTopNBlogsLikeRank(20);
        Assert.assertEquals(9, userIds.size());
        Assert.assertEquals("user9", userIds.get(0));
    }

}
