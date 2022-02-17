package com.donglaistd.jinli.kafka;

import com.donglaistd.jinli.Kafka;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
public class KafkaMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaMessageHandler.class);

    public void handle(Kafka.KafkaMessage data) {
        logger.info(MessageFormat.format("handle kafka message：{0}", data));
    }
}
