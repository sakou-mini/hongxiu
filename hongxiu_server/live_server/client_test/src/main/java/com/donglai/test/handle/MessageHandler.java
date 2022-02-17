package com.donglai.test.handle;

import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

public abstract class MessageHandler {
    public static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    public void handle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        doHandle(ctx, reply, userCache);
    }

    protected abstract void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache);

}