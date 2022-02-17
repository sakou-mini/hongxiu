package com.donglai.blogs.process;

import com.donglai.blogs.message.producer.Producer;
import com.donglai.common.constant.QueueType;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.model.entityBuilder.QueueBuilder;
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


    public QueueExecute createReviewBlogsQueue() {
        QueueExecute queue = null;
        QueueExecute byQueueTypeAndRefId = queueExecuteService.findByQueueTypeAndRefId(QueueType.REVIEW_BOLGS.getValue(), QueueType.REVIEW_BOLGS.name());
        if (Objects.isNull(byQueueTypeAndRefId)) {
            //五分钟
            long date = TimeUnit.MINUTES.toMillis(5);
            queue = QueueBuilder.createQueue(System.currentTimeMillis() + date, QueueType.REVIEW_BOLGS, QueueType.REVIEW_BOLGS.name(), QueueExecute.BLOG);
            queue = queueExecuteService.save(queue);
            producer.sendQueue(queue.getId());
        }
        return queue;
    }

    public void createUpdateBlogsLikeTaskQueue() {
        QueueExecute queue = null;
        QueueExecute byQueueTypeAndRefId = queueExecuteService.findByQueueTypeAndRefId(QueueType.BLOGS_UPLOAD_LIKE.getValue(), QueueType.BLOGS_UPLOAD_LIKE.name());
        if (Objects.isNull(byQueueTypeAndRefId)) {
            //10 分钟更新一次
            long delayTime = TimeUnit.MINUTES.toMillis(10);
            queue = QueueBuilder.createQueue(System.currentTimeMillis() + delayTime, QueueType.BLOGS_UPLOAD_LIKE, QueueType.BLOGS_UPLOAD_LIKE.name(), QueueExecute.BLOG);
            queue = queueExecuteService.save(queue);
            producer.sendQueue(queue.getId());
        }
    }

}
