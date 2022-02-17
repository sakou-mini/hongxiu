package com.donglai.netty.http;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class NettyHttpHandler extends SimpleChannelInboundHandler<Object> {

    private final NettyHttp nettyHttp;

    public NettyHttpHandler(NettyHttp nettyHttpHandlet) {
        this.nettyHttp = nettyHttpHandlet;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        nettyHttp.channelRead(channelHandlerContext, o);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        nettyHttp.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        nettyHttp.channelInactive(ctx);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("netty channel exception:" + cause.getMessage());
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        nettyHttp.userEventTriggered(ctx, evt);
    }
}
