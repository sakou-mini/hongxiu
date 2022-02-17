package com.donglaistd.jinli.kafka;

import com.donglaistd.jinli.Kafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.logging.Logger;


@Component
public class KafkaProducer {
    private static final Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    public void sendMessage(String topicName, Kafka.KafkaMessage data) {
        logger.info(MessageFormat.format("sending kafka object：{0}", data));
        try {
            kafkaTemplate.send(topicName, data);
            logger.fine("send success");
        } catch (Exception e) {
            e.printStackTrace();
            logger.severe(MessageFormat.format("send data error, topic:{0},data:{1}", topicName, data));
        }
    }
}