package com.donglai.account.message.listener;

import com.alibaba.fastjson.JSON;
import com.donglai.account.message.dispatcher.AccountMessageDispatcher;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import lombok.extern.java.Log;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

//消息订阅转发
@Component
@Log
public class AccountMessageListener {

    @KafkaListener(topics = "#{'${kafka.listener_topics}'.split(',')}")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            TopicMessage message = JSON.parseObject(kafkaMessage.get().toString(), TopicMessage.class);
            // 转发
            AccountMessageDispatcher.dispatcher(message);
        }
    }
}
