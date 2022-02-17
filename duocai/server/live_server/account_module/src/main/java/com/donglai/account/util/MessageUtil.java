package com.donglai.account.util;

import com.donglai.account.message.producer.Producer;
import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.google.protobuf.Message;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.donglai.protocol.ProtoBufMapper.MessageType.REPLY_MSG;

public class MessageUtil {

    public static KafkaMessage.TopicMessage buildReply(String userId, Message msg, Constant.ResultCode resultCode){
        Constant.PlatformType platform = Constant.PlatformType.DUOCAI;
        UserService userService = SpringApplicationContext.getBean(UserService.class);
        User user = userService.findById(userId);
        if(Objects.nonNull(user)) platform = user.getPlatform();
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG,messageId);
        return new KafkaMessage.TopicMessage(null, topic, userId, messageId, msg,resultCode);
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message.Builder msg, Constant.ResultCode resultCode){
        return buildReply(userId, msg.build(), resultCode);
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message msg, Constant.ResultCode resultCode, Constant.PlatformType platform){
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG,msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG,messageId);
        return new KafkaMessage.TopicMessage(platform, topic, userId, messageId, msg,resultCode);
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message.Builder msg, Constant.ResultCode resultCode, Constant.PlatformType platform){
        return buildReply(userId, msg.build(), resultCode,platform);
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message msg, Constant.ResultCode resultCode, Map<KafkaMessage.ExtraParam,String> extraMap, Constant.PlatformType platform){
       /* UserService userService = SpringApplicationContext.getBean(UserService.class);
        User user = userService.findById(userId);
        if(Objects.nonNull(user)) platform = user.getPlatform();*/
        KafkaMessage.TopicMessage topicMessage = buildReply(userId, msg, resultCode,platform);
        topicMessage.setExtraParams(extraMap);
        return topicMessage;
    }

    public static void broadCastMessage(Message message, Constant.ResultCode resultCode, Set<String> ids, Constant.PlatformType platform){
        if(Objects.isNull(ids) || ids.isEmpty()) return;
        Producer producer = SpringApplicationContext.getBean(Producer.class);
        producer.broadCastReplyMessage(ids,message,resultCode,platform);
    }
}
