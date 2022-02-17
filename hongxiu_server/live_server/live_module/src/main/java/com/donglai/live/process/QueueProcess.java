package com.donglai.live.process;

import com.donglai.common.constant.QueueType;
import com.donglai.live.message.producer.Producer;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.entityBuilder.QueueBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class QueueProcess {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    Producer producer;

    //创建延迟关闭直播队列
    public QueueExecute createAndSendEndLiveQueue(Room room, long delayTime) {
        long endTime = System.currentTimeMillis() + delayTime;
        QueueExecute queue = QueueBuilder.createQueue(room.getUserId(), room.getId(), endTime, QueueType.END_LIVE, QueueExecute.LIVE);
        queue = queueExecuteService.save(queue);
        producer.sendQueue(queue.getId());
        return queue;
    }

    //创建自动关闭直播队列
    public QueueExecute createAndSendAutoEndLiveQueue(Room room, long endTime) {
        QueueExecute queue = QueueBuilder.createQueue(room.getUserId(), room.getId(), endTime, QueueType.TIMEOUT_END_LIVE, QueueExecute.LIVE);
        queue = queueExecuteService.save(queue);
        producer.sendQueue(queue.getId());
        return queue;
    }

}
