package com.donglai.gate.net.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.message.producer.Producer;
import com.donglai.gate.process.GenerlProcess;
import com.donglai.gate.process.LoginCacheProcess;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.gate.message.dispatcher.GateNetDispatcher.sendErrorMessage;
import static com.donglai.protocol.Constant.ResultCode.NOT_AUTHORIZED;

@Slf4j
@Service("Gate_Default_Net_Service")
public class Gate_Default_Net_Service implements GateMessageServiceI<ChannelHandlerContext> {
    @Autowired
    Producer producer;

    @Override
    public void Process(ChannelHandlerContext ctx, KafkaMessage.TopicMessage topicMessage, Message message) {
        //登錄驗證判斷
        if (!GenerlProcess.isVerify(topicMessage.getUserid())) {
            log.warn("请求未登录  id{},request{}", topicMessage.getMessageId(), PbRefUtil.getPbRefSimpleNameByMessageId(ProtoBufMapper.MessageType.REQUEST_MSG, topicMessage.getMessageId()));
            sendErrorMessage(ctx, NOT_AUTHORIZED);
            return;
        }
        producer.send(topicMessage);
    }

    @Override
    public void Close(ChannelHandlerContext ctx) {
        LoginCacheProcess.updateCacheOnClose(ctx);
    }
}
