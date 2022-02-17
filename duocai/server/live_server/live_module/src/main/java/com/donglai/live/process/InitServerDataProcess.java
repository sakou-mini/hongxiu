package com.donglai.live.process;

import com.donglai.live.message.producer.Producer;
import com.donglai.live.message.services.queue.handler.UpdateGiftRankHandler;
import com.donglai.model.db.service.common.QueueExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    public void initMinuteJobQueue() {
        queueProcess.initMinuteJobQueue();
    }

    public void initLiveDomain() {
        liveDomainProcess.initDomainConfig();
    }
}
