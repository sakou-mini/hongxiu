package com.donglai.gate.message.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.util.GateUtil;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.google.protobuf.Message;
import org.springframework.stereotype.Service;

import static com.donglai.protocol.message.KafkaMessage.ExtraParam.CHANNEL_ID;
import static com.donglai.protocol.util.PbRefUtil.buildReply;

/**
 * 连接注册处理
 */
@Service("AccountOfRegisterReply")
public class AccountOfRegisterReply_Service implements GateMessageServiceI<String> {

    @Override
    public void Process(String userId, TopicMessage topicMessage, Message message) {
        String channelId = topicMessage.getExtraParams() != null ? topicMessage.getExtraParams().get(CHANNEL_ID) : "";
        GateUtil.sendData(userId, buildReply(message, topicMessage.getResultCode()), channelId);
    }

    @Override
    public void Close(String s) {

    }
}
