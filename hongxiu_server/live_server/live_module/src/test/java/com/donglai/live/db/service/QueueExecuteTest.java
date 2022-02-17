package com.donglai.live.db.service;

import com.donglai.live.app.LiveLogicApp;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.entityBuilder.QueueBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LiveLogicApp.class)
public class QueueExecuteTest {

    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void TestSaveQueue() {
        QueueExecute queue = QueueBuilder.createQueue(System.currentTimeMillis(), 1, "123", QueueExecute.LIVE);
        queue = queueExecuteService.save(queue);
        Assert.assertNotNull(queue);
        queue = queueExecuteService.findById(queue.getId());
        Assert.assertNotNull(queue);
    }
}

