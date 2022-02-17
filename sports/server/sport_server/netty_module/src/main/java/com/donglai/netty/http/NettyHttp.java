package com.donglai.netty.http;

import io.netty.channel.ChannelHandlerContext;

public interface NettyHttp {
	void channelActive(ChannelHandlerContext ctx);

	void channelRead(ChannelHandlerContext ctx, Object msg);

	void exceptionCaught(ChannelHandlerContext ctx, Throwable cause);

	void channelInactive(ChannelHandlerContext ctx);

	void userEventTriggered(ChannelHandlerContext ctx, Object evt);
}
