package com.donglaistd.jinli.kafka;

import com.donglaistd.jinli.Kafka;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class KafkaConsumer {
    private static final Logger logger = Logger.getLogger(KafkaConsumer.class.getName());
    @Autowired
    KafkaMessageHandler kafkaMessageHandler;

    @KafkaListener(id = "test", topics = {"topic-test"})
    public void listen(Kafka.KafkaMessage data) {
        logger.fine("kafka received message:" + data);
        kafkaMessageHandler.handle(data);
    }
}