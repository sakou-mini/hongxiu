package com.donglaistd.jinli.processors;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreAuth;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.config.GlobalExceptionHandler;
import com.donglaistd.jinli.database.dao.system.GameServerConfigDaoService;
import com.donglaistd.jinli.database.entity.system.GameServerConfig;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;

@Component
public class GameMessageDispatcher implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(GameMessageDispatcher.class.getName());
    private final Map<String, MessageHandler> beans = new ConcurrentHashMap<>();
    @Autowired
    GameServerConfigDaoService gameServerConfigDaoService;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;

    public void dispatch(ChannelHandlerContext ctx, Jinli.JinliMessageRequest message) {
        logger.info("received message:" + message.getRequestCase());
        MessageHandler gameMessageHandler = beans.get(message.getRequestCase().toString());
        if (gameMessageHandler == null) {
            logger.warning("no handler for message type:" + message.getRequestCase());
            sendErrorMessage(ctx, Constant.ResultCode.UNKNOWN);
            return;
        }
        if (ctx.channel().attr(USER_KEY).get() == null && NeedAuthorize(gameMessageHandler)) {
            logger.warning("user not authorized:" + message.getRequestCase());
            sendErrorMessage(ctx, Constant.ResultCode.NOT_AUTHORIZED);
            return;
        }
        if(isDisabledHandler(gameMessageHandler) || !serverAvailabilityCheckService.isActive()){
            logger.warning("message handler is disabled: " + message.getRequestCase());
            sendErrorMessage(ctx, Constant.ResultCode.SERVER_IS_DISABLED);
            return;
        }
        try {
            var reply = gameMessageHandler.handle(ctx, message);
            if(reply!=null)
                ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(reply.toByteArray())));
        } catch (Exception e) {
            logger.warning("message handler" + message.getRequestCase() + " throws exception:" + GlobalExceptionHandler.getExceptionInfo(e));
            e.printStackTrace();
            sendErrorMessage(ctx, Constant.ResultCode.UNKNOWN);
        }
    }

    private void sendErrorMessage(ChannelHandlerContext ctx, Constant.ResultCode resultCode) {
        var reply = Jinli.JinliMessageReply.newBuilder();
        reply.setResultCode(resultCode);
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(reply.build().toByteArray())));
    }

    private boolean NeedAuthorize(MessageHandler gameMessageHandler) {
        boolean present =  AopUtils.getTargetClass(gameMessageHandler).isAnnotationPresent(IgnoreAuth.class);
        //return !(gameMessageHandler instanceof LoginRequestHandler || gameMessageHandler instanceof RegisterRequestHandler || gameMessageHandler instanceof HeartConnectedRequestHandler);
        return !present;
    }

    private boolean isDisabledHandler(MessageHandler gameMessageHandler){
        boolean present =  AopUtils.getTargetClass(gameMessageHandler).isAnnotationPresent(IgnoreShutDown.class);
        if(present){
            GameServerConfig serverConfig = gameServerConfigDaoService.findGameServerConfig();
            return serverConfig != null && serverConfig.getServerStatue().equals(GameServerConfig.ServerStatue.STOP);
        }else
            return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        var contextBeans = applicationContext.getBeansOfType(MessageHandler.class);
        for (var bean : contextBeans.entrySet()) {
            beans.put(bean.getKey().toUpperCase().replace("HANDLER", ""), bean.getValue());
        }
    }
}