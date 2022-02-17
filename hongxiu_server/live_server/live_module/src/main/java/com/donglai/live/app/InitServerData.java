package com.donglai.live.app;

import com.donglai.live.message.producer.Producer;
import com.donglai.live.message.services.queue.handler.UpdateGiftRankHandler;
import com.donglai.live.process.LiveDomainProcess;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.QueueType.GIFT_CONTRIBUTE_INCOME_RANK;
import static com.donglai.live.constant.Constant.GIFT_CONTRIBUTE_INCOME_RANK_ID;

@Component
@Slf4j
public class InitServerData {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    UpdateGiftRankHandler updateGiftRankHandler;
    @Autowired
    LiveDomainProcess liveDomainProcess;
    @Autowired
    Producer producer;

    public void initRankQueue() {
        QueueExecute giftRankKey = queueExecuteService.findByQueueTypeAndRefId(GIFT_CONTRIBUTE_INCOME_RANK.getValue(), GIFT_CONTRIBUTE_INCOME_RANK_ID);
        if (giftRankKey == null) {
            QueueExecute giftRankTaskQueue = updateGiftRankHandler.createInitGiftRankTaskQueue();
            producer.sendQueue(giftRankTaskQueue.getId());
            log.info("init task to update gift rank {}", giftRankTaskQueue.getId());
        } else {
            log.info("已经初始化了 排行queue {}", giftRankKey.getId());
        }
    }

    public void initLiveDomain() {
        liveDomainProcess.initDomainConfig();
    }
}
