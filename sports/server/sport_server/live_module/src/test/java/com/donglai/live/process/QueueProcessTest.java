package com.donglai.live.process;

import com.donglai.common.constant.QueueType;
import com.donglai.live.BaseTest;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.live.message.producer.Producer;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.entityBuilder.QueueBuilder;
import com.donglai.protocol.Constant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Moon
 * @date 2021-10-19 16:14
 */

public class QueueProcessTest extends BaseTest {
    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    Producer producer;

    @Test
    public void test() {
        long endTime = System.currentTimeMillis() + 10;
        QueueExecute queue = QueueBuilder.createQueue(endTime, QueueType.END_LIVE, "refid",QueueExecute.LIVE);
        queue = queueExecuteService.save(queue);
        producer.sendQueue(queue.getId());
    }
}