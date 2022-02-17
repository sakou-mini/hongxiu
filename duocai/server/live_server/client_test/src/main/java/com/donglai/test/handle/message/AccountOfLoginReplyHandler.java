package com.donglai.test.handle.message;

import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import com.donglai.test.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Service;

@Service
public class AccountOfLoginReplyHandler extends MessageHandler {

    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        Account.AccountOfLoginReply liveLoginReply = reply.getAccountOfLoginReply();
        System.out.println(liveLoginReply.getUserInfo());
        DataManager.getUserCache(ctx.channel()).setUserId(liveLoginReply.getUserInfo().getUserId());
    }
}
