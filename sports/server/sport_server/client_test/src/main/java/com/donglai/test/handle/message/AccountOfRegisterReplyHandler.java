package com.donglai.test.handle.message;

import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import com.donglai.test.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountOfRegisterReplyHandler extends MessageHandler {

    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        Account.AccountOfRegisterReply liveRegisterReply = reply.getAccountOfRegisterReply();
        String accountId = liveRegisterReply.getAccountId();
        String password = liveRegisterReply.getPassword();
        log.info("send Login");
        userCache = new UserCache(accountId, password);
        DataManager.saveUserCache(userCache, ctx.channel());
    }
}
