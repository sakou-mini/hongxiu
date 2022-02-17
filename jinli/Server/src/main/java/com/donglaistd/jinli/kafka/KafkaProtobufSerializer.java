package com.donglaistd.jinli.kafka;

import com.donglaistd.jinli.Kafka.KafkaMessage;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;
import java.util.logging.Logger;

public class KafkaProtobufSerializer implements Serializer<KafkaMessage> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, KafkaMessage o) {
        byte[] retVal = null;
        try {
            retVal = o.toByteArray();
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
        return retVal;
    }

    @Override
    public void close() {

    }
}