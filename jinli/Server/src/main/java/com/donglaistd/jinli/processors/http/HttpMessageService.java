package com.donglaistd.jinli.processors.http;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.http.message.HttpMessageReply;
import com.donglaistd.jinli.http.message.HttpMessageRequest;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

@Component
public abstract class HttpMessageService {
    public HttpMessageReply handle(ChannelHandlerContext ctx, HttpMessageRequest messageRequest) {
        return doHandle(ctx, messageRequest);
    }

    protected abstract HttpMessageReply doHandle(ChannelHandlerContext ctx, HttpMessageRequest request);

    public static HttpMessageReply buildErrorMessageReply(int msgId, Constant.ResultCode resultCode){
        return new HttpMessageReply(msgId, resultCode);
    }
}
