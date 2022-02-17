package com.donglaistd.jinli.processors;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.message.HttpMessageIdEnum;
import com.donglaistd.jinli.http.message.HttpMessageReply;
import com.donglaistd.jinli.http.message.HttpMessageRequest;
import com.donglaistd.jinli.processors.http.HttpMessageService;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class HttpMessageDispatcher implements ApplicationContextAware {

    private final Logger logger = Logger.getLogger(HttpMessageDispatcher.class.getName());

    private final Map<String, HttpMessageService> beans = new ConcurrentHashMap<>();

    public void dispatch(ChannelHandlerContext ctx, String message) {
        HttpMessageRequest request = new Gson().fromJson(message, HttpMessageRequest.class);
        int messageId = request.getMessageId();
        String requestMessage = HttpMessageIdEnum.RequestMapper.getRequestMessageById(messageId);
        //logger.info("receieve httpSocketRequest by " + requestMessage);
        if (StringUtils.isNullOrBlank(requestMessage)) {
            logger.info("notFound request messageId");
            sendErrorMessage(ctx,Constant.ResultCode.UNKNOWN);
            return;
        }
        HttpMessageService httpMessageService = beans.get(requestMessage.toUpperCase());
        if(httpMessageService == null) {
            logger.info("notFound request Handler");
            sendErrorMessage(ctx,Constant.ResultCode.UNKNOWN);
            return;
        }
        HttpMessageReply reply = httpMessageService.handle(ctx, request);
        MessageUtil.sendMessageForTextWeb(ctx.channel(), reply);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        var contextBeans = applicationContext.getBeansOfType(HttpMessageService.class);
        for (var bean : contextBeans.entrySet()) {
            beans.put(bean.getKey().toUpperCase().replace("SERVICE", ""), bean.getValue());
        }
    }


    private void sendErrorMessage(ChannelHandlerContext ctx, Constant.ResultCode resultCode) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(new JSONObject(new HttpMessageReply(0,resultCode)).toString()));
    }
}
