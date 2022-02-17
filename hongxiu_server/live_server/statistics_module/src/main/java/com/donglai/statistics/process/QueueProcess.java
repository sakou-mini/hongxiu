package com.donglai.statistics.process;

import com.donglai.common.constant.QueueType;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.entityBuilder.QueueBuilder;
import com.donglai.statistics.message.producer.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class QueueProcess {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    Producer producer;

    //FIXME 每日任务
    public void initDailyTask() {
        QueueExecute dailyQueue = queueExecuteService.findByQueueTypeAndRefId(QueueType.DAILY_STATISTIC.getValue(), QueueType.DAILY_STATISTIC.name());
        if (Objects.isNull(dailyQueue)) {
            dailyQueue = QueueBuilder.createQueue(TimeUtil.getBeforeDayStartTime(-1), QueueType.DAILY_STATISTIC, QueueType.DAILY_STATISTIC.name(), QueueExecute.STATISTIC);
            dailyQueue = queueExecuteService.save(dailyQueue);
            producer.sendQueue(dailyQueue.getId());
        }
    }

    public void initMinuteTask() {
        QueueExecute dailyQueue = queueExecuteService.findByQueueTypeAndRefId(QueueType.MINUTE_STATISTIC.getValue(), QueueType.MINUTE_STATISTIC.name());
        if (Objects.isNull(dailyQueue)) {
            long endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
            dailyQueue = QueueBuilder.createQueue(endTime, QueueType.MINUTE_STATISTIC, QueueType.MINUTE_STATISTIC.name(), QueueExecute.STATISTIC);
            dailyQueue = queueExecuteService.save(dailyQueue);
            producer.sendQueue(dailyQueue.getId());
        }
    }
}
