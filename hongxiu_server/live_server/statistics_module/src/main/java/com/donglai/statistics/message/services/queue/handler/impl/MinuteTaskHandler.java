package com.donglai.statistics.message.services.queue.handler.impl;

import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.statistics.message.services.queue.handler.TriggerHandler;
import com.donglai.statistics.process.MinuteTaskProcess;
import com.donglai.statistics.process.QueueProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinuteTaskHandler implements TriggerHandler {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    MinuteTaskProcess minuteTaskProcess;
    @Autowired
    QueueProcess queueProcess;

    @Override
    public void deal(QueueExecute queueExecute) {
        queueExecuteService.del(queueExecute);
        minuteTaskProcess.totalTodayOfServerData();
        queueProcess.initMinuteTask();
    }
}
