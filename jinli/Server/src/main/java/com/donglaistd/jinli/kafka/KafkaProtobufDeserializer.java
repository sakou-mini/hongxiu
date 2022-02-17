package com.donglaistd.jinli.kafka;

import com.donglaistd.jinli.Kafka;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.logging.Logger;

public class KafkaProtobufDeserializer implements Deserializer<Kafka.KafkaMessage> {

    private final Logger logger = Logger.getLogger(this.getClass().getName());


    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public Kafka.KafkaMessage deserialize(String s, byte[] bytes) {
        Kafka.KafkaMessage message = null;
        try {
            message = Kafka.KafkaMessage.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public void close() {

    }
}