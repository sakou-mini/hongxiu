package com.donglai.live.message.producer;

import com.alibaba.fastjson.JSON;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.QueueExecuteService;
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
    UserService userService;
    @Autowired
    QueueExecuteService queueExecuteService;

    @Autowired
    private KafkaTemplate<Object, String> kafkaTemplate;

    private Constant.PlatformType getPlatformByUserId(String userId) {
        Constant.PlatformType platform = Constant.PlatformType.SPORT;
        if (!StringUtils.isNullOrBlank(userId)) {
            User user = userService.findById(userId);
            if (Objects.nonNull(user)) platform = user.getPlatform();
        }
        return platform;
    }

    public void send(String topic, String userId, int opCode, Message msg, Map<KafkaMessage.ExtraParam, String> extraMap) {
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setUserid(userId);
        topicMessage.setMessageId(opCode);
        topicMessage.setContent(msg.toByteArray());
        topicMessage.setExtraParams(extraMap);
        topicMessage.setPlatform(getPlatformByUserId(userId));
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void send(TopicMessage topicMessage) {
        kafkaTemplate.send(topicMessage.getTopic(), topicMessage.getUserid(), JSON.toJSONString(topicMessage));
    }

    public void sendMessageRequest(String userId, Message msg) {
        int messageId = PbRefUtil.getPbRefMsgId(REQUEST_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setUserid(userId);
        topicMessage.setMessageId(messageId);
        topicMessage.setContent(buildRequest(msg).toByteArray());
        topicMessage.setPlatform(getPlatformByUserId(userId));
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }


    public void sendReply(String userId, Message msg, Map<KafkaMessage.ExtraParam, String> extraMap, Constant.ResultCode... resultCode) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(getPlatformByUserId(userId), topic, userId, messageId, msg);
        if (resultCode.length > 0) {
            topicMessage.setResultCode(resultCode[0]);
        }
        topicMessage.setExtraParams(extraMap);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode, Map<KafkaMessage.ExtraParam, String> extraMap) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(getPlatformByUserId(userId), topic, userId, messageId, msg, resultCode);
        topicMessage.setExtraParams(extraMap);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message msg, Constant.ResultCode resultCode) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(getPlatformByUserId(userId), topic, userId, messageId, msg, resultCode);
        kafkaTemplate.send(topicMessage.getTopic(), userId, JSON.toJSONString(topicMessage));
    }

    public void sendReply(String userId, Message.Builder msg, Constant.ResultCode resultCode) {
        sendReply(userId, msg.build(), resultCode);
    }


    public void sendQueue(String queueId) {
        QueueExecute queue = queueExecuteService.findById(queueId);
        if (Objects.isNull(queue)) return;
        Common.QueueOfAddQueueRequest msg = Common.QueueOfAddQueueRequest.newBuilder().setQueueId(queueId).build();
        int messageId = PbRefUtil.getPbRefMsgId(REQUEST_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REQUEST_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage();
        topicMessage.setTopic(topic);
        topicMessage.setMessageId(messageId);
        topicMessage.setContent(buildRequest(msg).toByteArray());
        topicMessage.setPlatform(queue.getPlatform());
        kafkaTemplate.send(topicMessage.getTopic(), JSON.toJSONString(topicMessage));
    }

    public void broadCastReplyMessage(Set<String> userIds, Message msg, Constant.ResultCode resultCode) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        TopicMessage topicMessage = new TopicMessage(null, topic, messageId, msg, resultCode, userIds);
        kafkaTemplate.send(topicMessage.getTopic(), JSON.toJSONString(topicMessage));
    }
}
