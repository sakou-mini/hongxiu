package com.donglai.gate.message.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.donglai.gate.util.GateUtil.sendData;
import static com.donglai.protocol.message.KafkaMessage.ExtraParam.CHANNEL_ID;
import static com.donglai.protocol.util.PbRefUtil.buildReply;

/**
 * 回调处理
 */
@Service("Gate_Default_Message_Service")
@Slf4j
public class Gate_Default_Message_Service implements GateMessageServiceI<String> {

    @Override
    public void Process(String userid, TopicMessage topicMessage, Message message) {
        //log.debug("gate default receive Reply {} resultCode{}",message,topicMessage.getResultCode());
        HongXiu.HongXiuMessageReply buildReply = buildReply(message, topicMessage.getResultCode());
        if (!topicMessage.getBroadCastIds().isEmpty()) {
            log.info("broadCastMessage:{}", buildReply.getReplyCase());
            topicMessage.getBroadCastIds().forEach(id -> sendData(id, buildReply));
        } else {
            String channelId = "";
            if (topicMessage.getExtraParams() != null) {
                channelId = topicMessage.getExtraParams().getOrDefault(CHANNEL_ID, "");
            }
            sendData(userid, buildReply, channelId);
        }
    }

    @Override
    public void Close(String t) {

    }
}
