package com.donglai.blogs.message.services.queue;

import com.donglai.blogs.message.services.queue.handler.TriggerHandler;
import com.donglai.blogs.message.services.queue.handler.impl.BlogLikeTaskHandler;
import com.donglai.blogs.message.services.queue.handler.impl.BlogsReviewHandler;
import com.donglai.common.constant.QueueType;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service("BlogsOfQueueFinishRequest")
@Slf4j
public class BlogsOfQueueFinishRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private BlogsReviewHandler blogsReviewHandler;
    @Autowired
    BlogLikeTaskHandler blogLikeTaskHandler;
    @Autowired
    private QueueExecuteService queueExecuteService;

    @Override
    public KafkaMessage.TopicMessage Process(String s, HongXiu.HongXiuMessageRequest message, Object... param) {
        var queueRequest = message.getBlogsOfQueueFinishRequest();
        String queueId = queueRequest.getQueueId();
        var queueExecute = queueExecuteService.findById(queueId);
        if (queueExecute == null) {
            log.info("队列已失效, queue id is {}", queueId);
            return null;
        }
        log.info("队列的结束时间为：{}", queueExecute.getEndTime());
        TriggerHandler triggerHandler = BlogsOfQueueHandlerFactory.getTriggerHandlerByQueueType(QueueType.valueOf(queueExecute.getTriggerType()));
        if (Objects.nonNull(triggerHandler))
            triggerHandler.deal(queueExecute);
        else {
            log.warn("not found queue handler by type:{}", QueueType.valueOf(queueExecute.getTriggerType()));
        }
        queueExecuteService.del(queueExecute);
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
