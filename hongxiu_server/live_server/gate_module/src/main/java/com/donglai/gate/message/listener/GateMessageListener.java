package com.donglai.gate.message.listener;

import com.alibaba.fastjson.JSON;
import com.donglai.gate.message.dispatcher.GateMessageDispatcher;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

//消息订阅转发

/**
 * 回调监听
 */
@Component
public class GateMessageListener {
    @Autowired
    GateMessageDispatcher gateMessageDispatcher;

    @KafkaListener(topics = "#{'${kafka.listener_topics}'.split(',')}")
    //@KafkaListener(topics = { "GATE"})
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            TopicMessage message = JSON.parseObject(kafkaMessage.get().toString(), TopicMessage.class);
            // 转发
            GateMessageDispatcher.dispatcher(message);
        }
    }
}
