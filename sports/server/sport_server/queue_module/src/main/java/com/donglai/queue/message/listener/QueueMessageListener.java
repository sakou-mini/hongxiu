package com.donglai.queue.message.listener;

import com.alibaba.fastjson.JSON;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.queue.message.dispatcher.QueueMessageDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class QueueMessageListener {

    @KafkaListener(topics = "#{'${kafka.listener_topics}'.split(',')}")
    public void listen(ConsumerRecord<?, ?> record) {
        log.info("收到队列数据");
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()) {
            TopicMessage message = JSON.parseObject(kafkaMessage.get().toString(), TopicMessage.class);
            QueueMessageDispatcher.dispatcher(message);
        }
    }
}
