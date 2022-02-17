package com.donglai.gate.message.producer;

import com.alibaba.fastjson.JSON;
import com.donglai.protocol.Constant;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Producer {
    @Autowired
    private KafkaTemplate<Object, String> kafkaTemplate;

    public void send(KafkaMessage.TopicMessage message) {
        String key = message.getUserid();
        kafkaTemplate.send(message.getTopic(), key, JSON.toJSONString(message));
    }

    public void send(Integer platformId, KafkaMessage.TopicMessage message) {
        Constant.PlatformType platformType = Constant.PlatformType.valueOf(platformId);
        message.setPlatform(platformType);
        kafkaTemplate.send(message.getTopic(), String.valueOf(platformId), JSON.toJSONString(message));
    }
}
