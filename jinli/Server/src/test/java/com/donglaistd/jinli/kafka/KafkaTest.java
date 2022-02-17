package com.donglaistd.jinli.kafka;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Kafka;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaTest extends BaseTest {

    @Autowired
    private KafkaProducer kafkaProducer;


    @Test
    public void TestShouldSendAndReceiveMessage() throws InterruptedException {
//        var kafkaMessage = Kafka.KafkaMessage.newBuilder().setGameResultMessage(Kafka.GameResultMessage.newBuilder().build()).build();
//        kafkaProducer.sendMessage("topic-test", kafkaMessage);
//        Thread.sleep(1000);
//        Mockito.verify(kafkaMessageHandler).handle(kafkaMessage);
    }
}
