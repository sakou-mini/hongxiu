package com.donglai.queue.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("Game_Default_Message_Service")
@Slf4j
public class Queue_Default_Message_Service implements TopicMessageServiceI<String> {

    @Override
    public KafkaMessage.TopicMessage Process(String s, HongXiu.HongXiuMessageRequest message, Object... param) {
        log.warn("nothing do something");
        log.info(message.toString());
        return null;
    }

    @Override
    public void Close(String t) {

    }
}
