package com.donglai.test.handle.message;

import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LiveOfStartLiveReplyHandler extends MessageHandler {
    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        Constant.ResultCode resultCode = reply.getResultCode();
        log.info("startlive result is {}", resultCode);
    }
}
