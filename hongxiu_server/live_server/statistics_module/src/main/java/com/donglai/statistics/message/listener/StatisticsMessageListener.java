package com.donglai.statistics.message.listener;

import com.alibaba.fastjson.JSON;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.statistics.message.dispatcher.StatisticsMessageDispatcher;
import lombok.extern.java.Log;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

//消息订阅转发
@Component
@Log
public class StatisticsMessageListener {

    @KafkaListener(topics = "#{'${kafka.listener_topics}'.split(',')}")
    public void listen(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            TopicMessage message = JSON.parseObject(kafkaMessage.get().toString(), TopicMessage.class);
            // 转发
            StatisticsMessageDispatcher.dispatcher(message);
        }
    }
}
