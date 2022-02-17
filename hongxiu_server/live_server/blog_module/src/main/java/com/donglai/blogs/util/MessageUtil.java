package com.donglai.blogs.util;

import com.donglai.blogs.message.producer.Producer;
import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.util.StringUtils;
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

    public static final UserService userService = SpringApplicationContext.getBean(UserService.class);

    private static Constant.PlatformType getPlatformByUserId(String userId) {
        Constant.PlatformType platform = Constant.PlatformType.HONG_XIU;
        if (!StringUtils.isNullOrBlank(userId)) {
            User user = userService.findById(userId);
            if (Objects.nonNull(user)) platform = user.getPlatform();
        }
        return platform;
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message msg, Constant.ResultCode resultCode) {
        int messageId = PbRefUtil.getPbRefMsgId(REPLY_MSG, msg);
        String topic = PbRefUtil.getSendTopic(REPLY_MSG, messageId);
        return new KafkaMessage.TopicMessage(getPlatformByUserId(userId), topic, userId, messageId, msg, resultCode);
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message.Builder msg, Constant.ResultCode resultCode) {
        return buildReply(userId, msg.build(), resultCode);
    }

    public static KafkaMessage.TopicMessage buildReply(String userId, Message msg, Constant.ResultCode resultCode, Map<KafkaMessage.ExtraParam, String> extraMap) {
        KafkaMessage.TopicMessage topicMessage = buildReply(userId, msg, resultCode);
        topicMessage.setExtraParams(extraMap);
        return topicMessage;
    }

    public static void broadCastMessage(Message message, Constant.ResultCode resultCode, Set<String> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) return;
        Producer producer = SpringApplicationContext.getBean(Producer.class);
        producer.broadCastReplyMessage(ids, message, resultCode);
    }
}
