package com.donglai.statistics.message.services;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DefaultMessageService_Service implements TopicMessageServiceI<String> {
    @Override
    public KafkaMessage.TopicMessage Process(String s, HongXiu.HongXiuMessageRequest message, Object... param) {
        log.warn("defaultMessageService nothing todo");
        return null;
    }

    @Override
    public void Close(String s) {

    }
}
