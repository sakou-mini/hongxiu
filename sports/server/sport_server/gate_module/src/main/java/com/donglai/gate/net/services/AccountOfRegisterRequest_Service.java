package com.donglai.gate.net.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.message.producer.Producer;
import com.donglai.gate.process.GenerlProcess;
import com.donglai.protocol.message.KafkaMessage;
import com.google.protobuf.Message;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AccountOfRegisterRequest")
public class AccountOfRegisterRequest_Service implements GateMessageServiceI<ChannelHandlerContext> {
    @Autowired
    Producer producer;

    @Override
    public void Process(ChannelHandlerContext ctx, KafkaMessage.TopicMessage topicMessage, Message message) {
        //未登录处理
        GenerlProcess.unVerify(topicMessage, ctx);
        producer.send(topicMessage);
    }

    @Override
    public void Close(ChannelHandlerContext context) {

    }
}
