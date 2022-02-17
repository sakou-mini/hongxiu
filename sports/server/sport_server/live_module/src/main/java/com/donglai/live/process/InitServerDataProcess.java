package com.donglai.live.process;

import com.donglai.live.message.producer.Producer;
import com.donglai.live.message.services.queue.handler.UpdateGiftRankHandler;
import com.donglai.live.process.LiveDomainProcess;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglai.common.constant.QueueType.GIFT_CONTRIBUTE_INCOME_RANK;
import static com.donglai.common.constant.QueueType.SPORT_EVENT;
import static com.donglai.live.constant.Constant.GIFT_CONTRIBUTE_INCOME_RANK_ID;

@Component
@Slf4j
public class InitServerDataProcess {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    UpdateGiftRankHandler updateGiftRankHandler;
    @Autowired
    LiveDomainProcess liveDomainProcess;
    @Autowired
    Producer producer;
    @Autowired
    QueueProcess queueProcess;

    public void initRankQueue() {
        queueProcess.initRankQueue();
    }

    public void initSportEventListQueue(){
        queueProcess.initSportEventListQueue();
    }

    public void initMinuteJobQueue() {
        queueProcess.initMinuteJobQueue();
    }

    public void initLiveDomain() {
        liveDomainProcess.initDomainConfig();
    }
}
