package com.donglai.test.handle.message;

import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountOfUpdatePasswordReplyHandler extends MessageHandler {

    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        log.info("updatepassword :" + reply.getResultCode().name());
    }
}
