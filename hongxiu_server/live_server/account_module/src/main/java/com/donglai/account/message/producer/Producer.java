package com.donglai.account.message.producer;

import com.alibaba.fastjson.JSON;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
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
import java.util.Objects;
import java.util.Set;

import static com.donglai.protocol.ProtoBufMapper.MessageType.REPLY_MSG;
import static com.donglai.protocol.ProtoBufMapper.MessageType.REQUEST_MSG;
import static com.donglai.protocol.util.PbRefUtil.buildRequest;

@Component
public class Producer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    UserService userService;

    public void send(String topic, String userid, int opCode, Message msg, Map<KafkaMessage.ExtraParam, String> extraMap, Constant.PlatformType platform) {
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setUserid(userid);
        topicMessage.setMessageId(opCode);
        topicMessage.setContent(msg.toByteArray());
        topicMessage.setExtraParams(extraMap);
        topicMessage.setPlatform(platform);
        kafkaTemplate.send(topic, userid, JSON.toJSONString(topicMessage));
    }

    public void send(TopicMessage topicMessage) {
        kafkaTemplate.send(topicMessage.getTopic(), topicMessage.getUserid(), JSON.toJSONString(topicMessage));
    }

    public void sendMessageRequest(String userId, Message msg, Constant.PlatformType platform) {
        sendMessageRequest(userId, msg, platform, null);
    }

    public void sendMessageRequest(String userId, Message msg, Constant.PlatformType platform, Map<KafkaMessage.ExtraParam, String> extraParam) {
        int messageId = PbRefUtil.getPbRefMsgId(REQUEST_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setUserid(userId);
        topicMessage.setMessageId(messageId);
        topicMessage.setContent(buildRequest(msg).toByteArray());
        topicMessage.setPlatform(platform);
        topicMessage.setExtraParams(extraParam);
        kafkaTemplate.send(topic, userId, JSON.toJSONString(topicMessage));
    }


    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode, Map<KafkaMessage.ExtraParam, String> extraMap) {
        Constant.PlatformType platformType = null;
        User user = userService.findById(userId);
        if (!Objects.isNull(user)) platformType = user.getPlatform();
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(platformType, topic, userId, messageId, msg, resultCode);
        topicMessage.setExtraParams(extraMap);
        kafkaTemplate.send(topic, userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode, Constant.PlatformType platform, Map<KafkaMessage.ExtraParam, String> extraMap) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(platform, topic, userId, messageId, msg, resultCode);
        topicMessage.setExtraParams(extraMap);
        kafkaTemplate.send(topic, userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode) {
        Constant.PlatformType platform = null;
        User user = userService.findById(userId);
        if (!Objects.isNull(user)) platform = user.getPlatform();
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(platform, topic, userId, messageId, msg, resultCode);
        kafkaTemplate.send(topic, userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message.Builder msg, Constant.ResultCode resultCode) {
        sendReply(userId, msg.build(), resultCode);
    }


    public void sendQueue(String queueId, Constant.PlatformType platform) {
        Common.QueueOfAddQueueRequest msg = Common.QueueOfAddQueueRequest.newBuilder().setQueueId(queueId).build();
        int messageId = PbRefUtil.getPbRefMsgId(REQUEST_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setMessageId(messageId);
        topicMessage.setContent(buildRequest(msg).toByteArray());
        topicMessage.setPlatform(platform);
        kafkaTemplate.send(topic, JSON.toJSONString(topicMessage));
    }

    public void broadCastReplyMessage(Set<String> userIds, Message msg, Constant.ResultCode resultCode, Constant.PlatformType platform) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(platform, topic, messageId, msg, resultCode, userIds);
        kafkaTemplate.send(topic, JSON.toJSONString(topicMessage));
    }
}
