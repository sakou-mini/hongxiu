package com.donglai.web.message.producer;

import com.alibaba.fastjson.JSON;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.google.protobuf.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

import static com.donglai.protocol.Constant.PlatformType.SPORT;
import static com.donglai.protocol.ProtoBufMapper.MessageType.REPLY_MSG;
import static com.donglai.protocol.ProtoBufMapper.MessageType.REQUEST_MSG;
import static com.donglai.protocol.util.PbRefUtil.buildRequest;

@Component
public class Producer {

    @Autowired
    private KafkaTemplate<Object,String> kafkaTemplate;

    public void send(String topic, String userid, int opCode, Message msg, Map<KafkaMessage.ExtraParam, String> extraMap) {
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setUserid(userid);
        topicMessage.setMessageId(opCode);
        topicMessage.setContent(msg.toByteArray());
        topicMessage.setExtraParams(extraMap);
        topicMessage.setPlatform(SPORT);
        kafkaTemplate.send(topicMessage.getTopic(), userid, JSON.toJSONString(topicMessage));
    }

    public void send(TopicMessage topicMessage) {
        kafkaTemplate.send(topicMessage.getTopic(), topicMessage.getUserid(), JSON.toJSONString(topicMessage));
    }

    public void sendMessageRequest(String userId, Message msg)
    {
        int messageId = PbRefUtil.getPbRefMsgId(REQUEST_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG,messageId);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setUserid(userId);
        topicMessage.setMessageId(messageId);
        topicMessage.setContent(buildRequest(msg).toByteArray());
        topicMessage.setPlatform(SPORT);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }


    public void sendReply(String userId, Message msg, Map<KafkaMessage.ExtraParam,String> extraMap, Constant.ResultCode ...resultCode){
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG,messageId);
        TopicMessage topicMessage = new TopicMessage(SPORT, topic, userId, messageId, msg);
        if(resultCode.length>0) {
            topicMessage.setResultCode(resultCode[0]);
        }
        topicMessage.setExtraParams(extraMap);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode,Map<KafkaMessage.ExtraParam,String> extraMap){
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG,messageId);
        TopicMessage topicMessage = new TopicMessage(SPORT, topic, userId, messageId, msg,resultCode);
        topicMessage.setExtraParams(extraMap);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode){
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG,messageId);
        TopicMessage topicMessage = new TopicMessage(SPORT,topic, userId, messageId, msg,resultCode);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message.Builder msg, Constant.ResultCode resultCode){
        sendReply(userId, msg.build(), resultCode);
    }

    public void sendQueue(String queueId)
    {
        Common.QueueOfAddQueueRequest msg = Common.QueueOfAddQueueRequest.newBuilder().setQueueId(queueId).build();
        int messageId = PbRefUtil.getPbRefMsgId(REQUEST_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG,messageId);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setMessageId(messageId);
        topicMessage.setContent(buildRequest(msg).toByteArray());
        topicMessage.setPlatform(SPORT);
        kafkaTemplate.send(topicMessage.getTopic(), JSON.toJSONString(topicMessage));
    }

    public void broadCastReplyMessage(Set<String> userIds, Message msg, Constant.ResultCode resultCode) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG,messageId);
        TopicMessage topicMessage = new TopicMessage(SPORT, topic, messageId, msg,resultCode,userIds);
        kafkaTemplate.send(topicMessage.getTopic(), JSON.toJSONString(topicMessage));
    }
}
