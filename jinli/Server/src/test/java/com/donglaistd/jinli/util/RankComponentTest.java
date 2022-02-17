package com.donglaistd.jinli.util;

import com.donglaistd.jinli.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

public class RankComponentTest extends BaseTest {
    @Autowired
    RankComponent rankComponent;

    @Test
    public void test(){
        String key = "rankDemo1";
        rankComponent.add(key,"123",-1000);
        rankComponent.add(key,"123",-1100);
        rankComponent.add(key,"124",-1000);
        System.out.println("key 123:" + rankComponent.getScoreInRank(key, "123"));
        System.out.println("key 124:" + rankComponent.getScoreInRank(key, "124"));

        Set<ZSetOperations.TypedTuple<String>> result = rankComponent.rangeWithScore(key, 0, 20);
        Assert.assertEquals(2, result.size());
    }
}
