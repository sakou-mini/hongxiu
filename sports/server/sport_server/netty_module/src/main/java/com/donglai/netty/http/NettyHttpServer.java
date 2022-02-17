package com.donglai.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyHttpServer {
	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	public void bind(NettyHttp http, int port) {
		if(http==null) return;
		HttpServerInitializer init = new HttpServerInitializer(http);
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup(4);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 128)
					.option(ChannelOption.SO_REUSEADDR, true) //重用地址
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(init);
			//setOption(b, springBootNettyProperties);
			ChannelFuture future = b.bind(port).sync();
			log.info("start server success on port " + port);
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			log.warn("start server failed on port " + port);
			System.exit(-1);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	private void setOption(ServerBootstrap b, ChannelOption option, Object value) {
		if (value != null) b.option(option, value);
	}

	public void stop() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}
}
