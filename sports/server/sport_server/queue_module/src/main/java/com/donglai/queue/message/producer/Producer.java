package com.donglai.queue.message.producer;

import com.alibaba.fastjson.JSON;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.donglai.protocol.ProtoBufMapper.MessageType.REQUEST_MSG;

@Component
public class Producer {

    @Autowired
    private KafkaTemplate<Object, String> kafkaTemplate;

    public void send(QueueExecute queueExecute, HongXiu.HongXiuMessageRequest msg)
    {
        if(msg == null) return;
        int messageId = msg.getRequestCase().getNumber();
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG,messageId);
        KafkaMessage.TopicMessage message = new KafkaMessage.TopicMessage();
        message.setTopic(topic);
        message.setUserid(queueExecute.getUid());
        message.setMessageId(messageId);
        message.setContent(msg.toByteArray());
        message.setPlatform(queueExecute.getPlatform());
        String key =  queueExecute.getUid();
        kafkaTemplate.send(message.getTopic(), key, JSON.toJSONString(message));
    }
}
