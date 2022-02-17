package com.donglai.test.netty;

import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.Live;
import com.google.protobuf.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class HttpClientNetty {

    private String ip;
    // 服务器的端口
    private int port;

    public HttpClientNetty(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public Channel createConnected(WebSocketClientHandler webSocketClientHandler){
        String ipAddress = "ws://"+ ip+":"+port;
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap boot = new Bootstrap();
        boot.option(ChannelOption.SO_KEEPALIVE, true).option(ChannelOption.TCP_NODELAY, true).group(group).handler(new LoggingHandler(LogLevel.DEBUG))
                .channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {

            protected void initChannel(SocketChannel socketChannel) throws NoSuchAlgorithmException, KeyManagementException {
                ChannelPipeline p = socketChannel.pipeline();
                p.addLast(new HttpClientCodec(), new HttpObjectAggregator(1024 * 1024 * 5));
                p.addLast(new IdleStateHandler(60, 10, 60*10, TimeUnit.SECONDS));
                p.addLast("hookedHandler",webSocketClientHandler);
            }
        });
        try {
            HttpHeaders httpHeaders = new DefaultHttpHeaders();
            URI websocketURI = new URI(ipAddress);
            WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, httpHeaders,65536*15);
            Channel channel = boot.connect(websocketURI.getHost(), websocketURI.getPort()).sync().channel();
            WebSocketClientHandler handler = (WebSocketClientHandler) channel.pipeline().get("hookedHandler");
            handler.setHandshaker(handshaker);
            handshaker.handshake(channel);
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void login(Channel channel){
        Account.AccountOfLoginRequest.Builder loginRequest = Account.AccountOfLoginRequest.newBuilder().setAccountId("asdas").setPassword("213213");
        HongXiu.HongXiuMessageRequest message = HongXiu.HongXiuMessageRequest.newBuilder().setAccountOfLoginRequest(loginRequest).build();
        sendMessage(message, channel);
    }

    public static void regist(Channel channel){
        HongXiu.HongXiuMessageRequest message = HongXiu.HongXiuMessageRequest.newBuilder().setAccountOfRegisterRequest(
                Account.AccountOfRegisterRequest.newBuilder().setMobileCode("asd51523").setPassword("123456").build()).build();
        sendMessage(message, channel);
    }

    public static void sendMessage(Message message,Channel channel) {
        byte[] bytes = message.toByteArray();
        channel.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(bytes)));
    }

}
