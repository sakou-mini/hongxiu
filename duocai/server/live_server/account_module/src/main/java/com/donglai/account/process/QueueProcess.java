package com.donglai.account.process;

import com.donglai.account.message.producer.Producer;
import com.donglai.common.constant.QueueType;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.entityBuilder.QueueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class QueueProcess {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    Producer producer;

    //FIXME 每日任务
    public void initDailyTask() {
        QueueExecute dailyQueue = queueExecuteService.findByQueueTypeAndRefId(QueueType.ACCOUNT_DAILY_STATISTIC.getValue(), QueueType.ACCOUNT_DAILY_STATISTIC.name());
        if (Objects.isNull(dailyQueue)) {
            dailyQueue = QueueBuilder.createQueue(TimeUtil.getBeforeDayStartTime(-1), QueueType.ACCOUNT_DAILY_STATISTIC, QueueType.ACCOUNT_DAILY_STATISTIC.name(), QueueExecute.ACCOUNT);
            dailyQueue = queueExecuteService.save(dailyQueue);
            producer.sendQueue(dailyQueue.getId());
        }
    }
}
