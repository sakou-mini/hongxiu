package com.donglai.test.handle.message;

import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import com.donglai.test.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountOfThirdPartySignUpReplyHandler extends MessageHandler {

    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        var thirdPartySignUpReply = reply.getAccountOfThirdPartySignUpReply();
        log.info("receive AccountOfThirdPartySignUpReply {}",thirdPartySignUpReply);
        String accountId = thirdPartySignUpReply.getAccountId();
        String password = thirdPartySignUpReply.getPassword();
        userCache = new UserCache(accountId, password);
        DataManager.saveUserCache(userCache, ctx.channel());
    }
}
