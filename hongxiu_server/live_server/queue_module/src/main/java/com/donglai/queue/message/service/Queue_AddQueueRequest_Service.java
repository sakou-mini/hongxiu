package com.donglai.queue.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.queue.process.QueueProcess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("QueueOfAddQueueRequest")
@Slf4j
public class Queue_AddQueueRequest_Service implements TopicMessageServiceI<String> {

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Common.QueueOfAddQueueRequest addQueueRequest = message.getQueueOfAddQueueRequest();
        log.info("收到游戏服务器数据，开始执行：{}", addQueueRequest);
        QueueProcess.addTask(addQueueRequest.getQueueId());
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
