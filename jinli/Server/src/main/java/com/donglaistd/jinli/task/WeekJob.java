package com.donglaistd.jinli.task;

import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.dao.GiftRankDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableAsync
public class WeekJob {
    @Value("${data.rank.size}")
    private int SIZE;
    @Autowired
    private GiftLogDaoService giftLogDaoService;
    @Autowired
    private GiftRankDaoService giftRankDaoService;

    @Async
    @Scheduled(cron = "${data.task.cron.weekJob}")
    public void dayJob() {
      /*  long endTime = TimeUtil.todayStartTime(System.currentTimeMillis());
        long startTime = TimeUtil.mondayStartTime(endTime);
        List<GiftLog> list = giftLogDaoService.findByCreateTimeBetweenAndGroupBySenderId(SIZE, startTime, endTime);
        GiftRank contributionRank = saveContributionRank(list, endTime);
        list = giftLogDaoService.findByCreateTimeBetweenAndGroupByReceiveId(SIZE, startTime, endTime);
        GiftRank giftRank = saveGiftRank(list, endTime);
        giftRankDaoService.saveAll(Arrays.asList(contributionRank, giftRank));*/
    }

   /* private GiftRank saveContributionRank(List<GiftLog> list, long createTime) {
        List<Pair<String, Integer>> collect = list.stream().map(g -> new Pair<>(g.getSenderId(), g.getSendAmount())).collect(Collectors.toList());
        return GiftRank.newInstance(createTime, Constant.QueryTimeType.WEEK, Constant.RankType.CONTRIBUTION_RANK, collect);
    }

    private GiftRank saveGiftRank(List<GiftLog> list, long createTime) {
        List<Pair<String, Integer>> collect = list.stream().map(g -> new Pair<>(g.getReceiveId(), g.getSendAmount())).collect(Collectors.toList());
        return GiftRank.newInstance(createTime, Constant.QueryTimeType.WEEK, Constant.RankType.GIFT_RANK, collect);
    }*/
}
