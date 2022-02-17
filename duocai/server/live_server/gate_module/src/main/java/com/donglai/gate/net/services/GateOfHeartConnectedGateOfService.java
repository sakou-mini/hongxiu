package com.donglai.gate.net.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.util.GateUtil;
import com.donglai.protocol.message.KafkaMessage;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("GateOfHeartConnectedRequest")
@Slf4j
public class GateOfHeartConnectedGateOfService implements GateMessageServiceI<ChannelHandlerContext> {

    @Override
    public void Process(ChannelHandlerContext context, KafkaMessage.TopicMessage topicMessage, Message message) {
        log.info("HeartConnected by {} channelId {}", GateUtil.getMsgUserId(context,null),GateUtil.getChannelId(context));
    }

    @Override
    public void Close(ChannelHandlerContext context) {

    }
}
