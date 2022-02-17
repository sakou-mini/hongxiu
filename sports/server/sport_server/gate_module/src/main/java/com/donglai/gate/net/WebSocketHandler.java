package com.donglai.gate.net;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.gate.constant.GateConstant;
import com.donglai.gate.message.dispatcher.GateNetDispatcher;
import com.donglai.gate.util.GateUtil;
import com.donglai.gate.util.WebSocketHttpUtil;
import com.donglai.netty.http.NettyHttp;
import com.donglai.protocol.HongXiu;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Optional;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
public class WebSocketHandler implements NettyHttp {

    WebSocketServerHandshaker handshake;
    static WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
       // log.info("channel connected:" + ctx);
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            //如果是http请求
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            //如果是ws请求
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        GateNetDispatcher.close(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                String userId = (String) GateUtil.getChannelAttr(GateConstant.ChannelAttrUserId, null, ctx);
                log.error("关闭这个不会活跃的通道，user id is---->{}， channel {}  ",userId,GateUtil.getChannelId(ctx));
                ctx.channel().close();
            }
        }
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        String ip = Optional.ofNullable(WebSocketHttpUtil.getIP(req)).orElse("");
        GateUtil.setChannelAttr(GateConstant.ChannelAttrIP, ip, ctx);
        if (!req.decoderResult().isSuccess() || (!"websocket".equals(req.headers().get("Upgrade")))) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        handshake = wsFactory.newHandshaker(req);
        if (handshake == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshake.handshake(ctx.channel(), req);
        }
    }

    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            //关闭事件
            handshake.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            ctx.close();
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            //ping事件
            log.debug("someone is pinging" + ctx.channel());
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
        }
        if(frame instanceof TextWebSocketFrame){
            var request = (TextWebSocketFrame) frame;
            log.debug(request.text());
            return;
        }
        if (!(frame instanceof BinaryWebSocketFrame)) {
            log.warn("not supported format");
            return;
        }
        var request = (BinaryWebSocketFrame) frame;
        //log.debug(ctx.channel() + "received" + request);
        var size = request.content().readableBytes();
        var byteBuffer = ByteBuffer.allocate(size);
        request.content().getBytes(0, byteBuffer);
        //PROTO BUF 解析
        try {
            HongXiu.HongXiuMessageRequest message = HongXiu.HongXiuMessageRequest.parseFrom(byteBuffer.array());
            //调用消息分发器 执行模块
            SpringApplicationContext.getBean(GateNetDispatcher.class).dispatcher(ctx,message);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpUtil.setContentLength(res, res.content().readableBytes());
        }

        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
