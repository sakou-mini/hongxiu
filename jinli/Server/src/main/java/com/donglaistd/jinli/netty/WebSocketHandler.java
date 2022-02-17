package com.donglaistd.jinli.netty;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.UserDisconnectEvent;
import com.donglaistd.jinli.processors.GameMessageDispatcher;
import com.donglaistd.jinli.processors.HttpMessageDispatcher;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MyExecutorService;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Component
@Scope("prototype")
public class WebSocketHandler extends SimpleChannelInboundHandler<Object> {
    @Value("${spring.profiles.active:default}")
    private String profileName;
    @Value("${channel.inactive.close.delayTime}")
    private long channelInactiveCloseDelayTime;


    private static final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    public WebSocketHandler() {
    }

    @Autowired
    private GameMessageDispatcher gameMessageDispatcher;
    @Autowired
    HttpMessageDispatcher httpMessageDispatcher;

    WebSocketServerHandshaker handshake;

    static WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/websocket", null, false);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.info("channel connected:" + ctx);
        ctx.fireChannelActive();
    }

    @Autowired
    DataManager dataManager;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        logger.warning("channel disconnected: userId--->" + ctx.channel().attr(USER_KEY));
        User user = dataManager.getUserFromChannel(ctx);
        if(user != null) {
            ScheduledFuture<?> schedule = ScheduledTaskUtil.schedule(() -> {
                Channel removedChannel = DataManager.getUserChannel(user.getId());
                if(ctx.channel().equals(removedChannel)){
                    logger.warning("断线超时,清除玩家channel缓存");
                    dataManager.removeUserChannel(user.getId());
                    EventPublisher.publish(new UserDisconnectEvent(user, ctx.channel()));
                }else{
                    logger.warning("channel 已更新,不清除缓存");
                }
            }, channelInactiveCloseDelayTime);
            DataManager.addUserDisconnectTask(user.getId(), schedule);
        }
        DataManager.removeHttpChannel(ctx.channel());
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        String ip = com.donglaistd.jinli.util.HttpUtil.getIP(req);
        DataManager.saveRealIpToChannel(ctx, ip);
        logger.info("real ip is:"+ip);
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
            handshake.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            logger.warning("CloseWebSocketFrame 关闭连接---》" + ctx.channel().attr(USER_KEY).get());
            ctx.close();
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            if (profileName.equals("debug")) {
                logger.fine("someone is pinging" + ctx.channel());
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            }
            return;
        }

        if(frame instanceof TextWebSocketFrame){
            var request = (TextWebSocketFrame) frame;
            logger.fine(request.text());
            httpMessageDispatcher.dispatch(ctx,request.text());
            return;
        }

        if (!(frame instanceof BinaryWebSocketFrame)) {
            logger.warning("not supported format");
            return;
        }

        var request = (BinaryWebSocketFrame) frame;

        logger.fine(ctx.channel() + "received" + request);
        var size = request.content().readableBytes();
        var byteBuffer = ByteBuffer.allocate(size);
        request.content().getBytes(0, byteBuffer);
        MyExecutorService.getExecutorServicePool().execute(()->{
            try {
                gameMessageDispatcher.dispatch(ctx, Jinli.JinliMessageRequest.parseFrom(byteBuffer.array()));
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        });
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warning("异常断开 netty channel exception:" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx,evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                String userId = ctx.channel().attr(USER_KEY).get();
                logger.warning("关闭这个不会活跃的通道，user id is---->  "+userId);
                ctx.channel().close();
            }
        }
    }
}