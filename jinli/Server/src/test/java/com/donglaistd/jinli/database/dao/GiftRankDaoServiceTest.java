package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.rank.GiftRank;
import com.donglaistd.jinli.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GiftRankDaoServiceTest extends BaseTest {
    @Autowired
    private GiftRankDaoService giftRankDaoService;
    @Test
    public void testSave() {
        List<Pair<String, Integer>> list = new ArrayList<>();
        list.add(new Pair<>("8848", 1200));
        GiftRank giftRank1 = GiftRank.newInstance(System.currentTimeMillis(), Constant.QueryTimeType.WEEK, Constant.RankType.GIFT_RANK, new ArrayList<>());
        GiftRank giftRank2 = GiftRank.newInstance(System.currentTimeMillis()+1000*60, Constant.QueryTimeType.WEEK, Constant.RankType.GIFT_RANK, new ArrayList<>());
        GiftRank giftRank3 = GiftRank.newInstance(System.currentTimeMillis()+1000*60*2, Constant.QueryTimeType.WEEK, Constant.RankType.GIFT_RANK, list);

        List<GiftRank> giftRanks = giftRankDaoService.saveAll(Arrays.asList(giftRank1, giftRank2, giftRank3));
        GiftRank rank = giftRankDaoService.findByRankTypeAndTimeType(Constant.RankType.CONTRIBUTION_RANK, Constant.QueryTimeType.WEEK);
        Assert.assertNull(rank);
       rank = giftRankDaoService.findByRankTypeAndTimeType(Constant.RankType.GIFT_RANK, Constant.QueryTimeType.WEEK);
       Assert.assertNotNull(rank);
        Assert.assertEquals(giftRank3.getId(), rank.getId());
    }
}
