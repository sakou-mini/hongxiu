package com.donglai.account.message.service.queue;

import com.donglai.account.message.service.queue.handler.AccountOfQueueHandlerFactory;
import com.donglai.account.message.service.queue.handler.TriggerHandler;
import com.donglai.common.constant.QueueType;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service("AccountOfQueueFinishRequest")
public class AccountOfQueueFinishRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private QueueExecuteService queueExecuteService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfQueueFinishRequest();
        String queueId = request.getQueueId();
        var queueExecute = queueExecuteService.findById(queueId);
        if (queueExecute == null) {
            log.info("队列已失效, queue id is {}", queueId);
            return null;
        }
        log.info("队列的结束时间为：{}", queueExecute.getEndTime());
        TriggerHandler triggerHandler = AccountOfQueueHandlerFactory.getTriggerHandlerByQueueType(QueueType.valueOf(queueExecute.getTriggerType()));
        if (Objects.nonNull(triggerHandler)) {
            triggerHandler.deal(queueExecute);
        } else {
            log.warn("not found queue handler by type:{}", QueueType.valueOf(queueExecute.getTriggerType()));
        }
        queueExecuteService.del(queueExecute);
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
