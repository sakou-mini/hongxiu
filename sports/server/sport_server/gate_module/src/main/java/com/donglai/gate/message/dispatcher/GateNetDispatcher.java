package com.donglai.gate.message.dispatcher;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.constant.GateConstant;
import com.donglai.gate.process.MessageProcess;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.donglai.protocol.util.PbRefUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Random;

/**
 * KAFKA消息分发
 *
 */
@Slf4j
@Component
public class GateNetDispatcher {

    public static void close(ChannelHandlerContext ctx) {
        GateMessageServiceI<ChannelHandlerContext> context = getContext(null);
        if (context == null) {
            sendErrorMessage(ctx, Constant.ResultCode.UNKNOWN_MESSAGE);
            return;
        }
        context.Close(ctx);
    }

    /**
     * 消息开始分发
     * @param ctx
     * @param msg
     */
    public void dispatcher(ChannelHandlerContext ctx, HongXiu.HongXiuMessageRequest msg) {
        if(!Objects.equals(msg.getRequestCase().getNumber(),HongXiu.HongXiuMessageRequest.GATEOFHEARTCONNECTEDREQUEST_FIELD_NUMBER)) {
            log.info("收到请求消息{} : 消息id - {},platform is {}", msg.getRequestCase(), msg.getRequestCase().getNumber(), msg.getPlatform());
        }
        TopicMessage topicMessage = MessageProcess.getRequestCustomMessage(ctx, msg);
        //获取对象引用
        GateMessageServiceI<ChannelHandlerContext> context = getContext(PbRefUtil.getPbRefSimpleNameByMessageId(ProtoBufMapper.MessageType.REQUEST_MSG, topicMessage.getMessageId()));
        context.Process(ctx, topicMessage,msg);
    }

    /**
     *
     * @param simpleName 类引用路径
     * @return 相应的对象引用
     */
    public static GateMessageServiceI<ChannelHandlerContext> getContext(String simpleName) {
        GateMessageServiceI<ChannelHandlerContext> p = null;
        try {
            // 分发services
            if (simpleName != null)
                p =(GateMessageServiceI<ChannelHandlerContext>) SpringApplicationContext.getBean(simpleName);
        } catch (Exception e) {
            //log.info("not found handler, use default handler");
        }
        // 默认处理
        if (p == null)
            p = (GateMessageServiceI<ChannelHandlerContext>) SpringApplicationContext.getBean(GateConstant.DefaultNetService);
        return p;
    }

    public static void sendErrorMessage(ChannelHandlerContext ctx, Constant.ResultCode resultCode) {
        var reply = HongXiu.HongXiuMessageReply.newBuilder();
        reply.setResultCode(resultCode);
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(reply.build().toByteArray())));
    }

}
