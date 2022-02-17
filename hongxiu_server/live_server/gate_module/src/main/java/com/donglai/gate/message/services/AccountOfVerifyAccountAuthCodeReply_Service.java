package com.donglai.gate.message.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.util.GateUtil;
import com.donglai.protocol.message.KafkaMessage;
import com.google.protobuf.Message;
import org.springframework.stereotype.Service;

import static com.donglai.protocol.message.KafkaMessage.ExtraParam.CHANNEL_ID;
import static com.donglai.protocol.util.PbRefUtil.buildReply;

@Service("AccountOfVerifyAccountAuthCodeReply")
public class AccountOfVerifyAccountAuthCodeReply_Service implements GateMessageServiceI<String> {

    @Override
    public void Process(String userId, KafkaMessage.TopicMessage topicMessage, Message message) {
        String channelId = topicMessage.getExtraParams() != null ? topicMessage.getExtraParams().get(CHANNEL_ID) : "";
        GateUtil.sendData(userId, buildReply(message, topicMessage.getResultCode()), channelId);
    }

    @Override
    public void Close(String s) {

    }
}
