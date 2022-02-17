package com.donglai.live.message.services.queue.handler;

import com.donglai.live.message.producer.Producer;
import com.donglai.live.service.GiftRankCacheService;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.live.GiftRank;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.db.service.live.GiftRankService;
import com.donglai.model.entityBuilder.QueueBuilder;
import com.donglai.protocol.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.QueueType.GIFT_CONTRIBUTE_INCOME_RANK;
import static com.donglai.live.constant.Constant.GIFT_CONTRIBUTE_INCOME_RANK_ID;

@Component
@Slf4j
public class UpdateGiftRankHandler implements TriggerHandler {
    @Value("${gift.rank.top.num}")
    private int topNum;
    @Value("${gift.rank.update.time}")
    private long giftRankUpdateIntervalTime;
    @Autowired
    GiftRankCacheService giftRankCacheService;
    @Autowired
    GiftRankService giftRankService;
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    Producer producer;

    @Override
    public void deal(QueueExecute queueExecute) {
        var contributionRankInfo = giftRankCacheService.getTopNRankInfoByRankType(Constant.RankType.CONTRIBUTION_RANK, topNum);
        var incomeRankInfo = giftRankCacheService.getTopNRankInfoByRankType(Constant.RankType.INCOME_RANK, topNum);
        GiftRank contributionRank = giftRankService.updateGiftRankByRankTypeAndTimeType(Constant.RankType.CONTRIBUTION_RANK, contributionRankInfo);
        log.info("update gift contribution rank {}", contributionRank);
        GiftRank incomeRank = giftRankService.updateGiftRankByRankTypeAndTimeType(Constant.RankType.INCOME_RANK, incomeRankInfo);
        log.info("update gift income rank {}", incomeRank);
        queueExecuteService.del(queueExecute);
        QueueExecute queue = createInitGiftRankTaskQueue();
        producer.sendQueue(queue.getId());
    }

    public QueueExecute createInitGiftRankTaskQueue() {
        long endTime = System.currentTimeMillis() + giftRankUpdateIntervalTime;
        QueueExecute queue = QueueBuilder.createQueue(endTime, GIFT_CONTRIBUTE_INCOME_RANK, GIFT_CONTRIBUTE_INCOME_RANK_ID, QueueExecute.LIVE);
        return queueExecuteService.save(queue);
    }
}
