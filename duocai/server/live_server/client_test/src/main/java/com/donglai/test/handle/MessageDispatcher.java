package com.donglai.test.handle;

import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.test.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class MessageDispatcher implements ApplicationContextAware {
    Map<String, MessageHandler> beans = new ConcurrentHashMap<>();

    public void dispatcherReply(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply message){
        log.debug("received message replyCode is:" + message.getResultCode()+" message is " + message.getReplyCase());
        if(message.getResultCode().equals(Constant.ResultCode.UNKNOWN)){
            log.warn("the server error!");
        }
        MessageHandler gameMessageHandler = beans.get(message.getReplyCase().toString());
        if(Objects.isNull(gameMessageHandler)) return;
        try {
            gameMessageHandler.handle(ctx, message, DataManager.getUserCache(ctx.channel()));
        } catch (Exception e) {
            log.error("message handler" + message.getReplyCase() + " throws exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        var contextBeans = applicationContext.getBeansOfType(MessageHandler.class);
        for (var bean : contextBeans.entrySet()) {
            beans.put(bean.getKey().toUpperCase().replace("HANDLER", ""), bean.getValue());
        }
    }
}
