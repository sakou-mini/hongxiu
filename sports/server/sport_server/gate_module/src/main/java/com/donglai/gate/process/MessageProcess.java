package com.donglai.gate.process;

import com.donglai.gate.util.GateUtil;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage;
import io.netty.channel.ChannelHandlerContext;

public class MessageProcess {

    // 组合订阅消息体
    public static KafkaMessage.TopicMessage getRequestCustomMessage(ChannelHandlerContext ctx, HongXiu.HongXiuMessageRequest msg) {
        int messageId = msg.getRequestCase().getNumber();
        String topic = ProtoBufMapper.getTopicByOpCode(ProtoBufMapper.MessageType.REQUEST_MSG,messageId);
        String msgUserId = GateUtil.getMsgUserId(ctx, null);
        return new KafkaMessage.TopicMessage(msg.getPlatform(), topic, msgUserId, messageId, msg);
    }
}
