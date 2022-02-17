package com.donglai.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
	private static final Integer readerIdleTimeSeconds = 60;
	private static final Integer writerIdleTimeSeconds = 30;
	private static final Integer allIdleTimeSeconds = 30;

	private final NettyHttp nettyHttp;

	public HttpServerInitializer(NettyHttp nettyHttp) {
		this.nettyHttp = nettyHttp;
	}

	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		// http服务器端对request解码
		pipeline.addLast("http-codec", new HttpServerCodec());
		// 拆包处理
		pipeline.addLast("websocket_aggregator", new WebSocketFrameAggregator(1_000_0000));
		pipeline.addLast("http_aggregator", new HttpObjectAggregator(1_000_0000))
				.addLast("http-chunked", new ChunkedWriteHandler());
		pipeline.addLast(new IdleStateHandler(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds, TimeUnit.SECONDS));
		// http handler
		pipeline.addLast("handler", new NettyHttpHandler(nettyHttp));
	}

}
