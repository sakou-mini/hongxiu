package com.donglaistd.jinli.netty.server;

import com.donglaistd.jinli.config.SpringBootNettyProperties;
import com.donglaistd.jinli.netty.WebSocketHandler;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.logging.Logger;

@Component
public class NettyServer implements ApplicationContextAware {
    private static final Logger logger = Logger.getLogger(WebSocketHandler.class.getName());

    @Autowired
    private WebSocketHandler webSocketHandler;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private ApplicationContext context;

    public void start(SpringBootNettyProperties springBootNettyProperties) {

        logger.info("Netty Server starting...");

        EventLoopGroup bossGroup = new NioEventLoopGroup(springBootNettyProperties.getBossGroupThreadSize());
        EventLoopGroup workerGroup = new NioEventLoopGroup(springBootNettyProperties.getWorkGroupThreadSize());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ChannelHandler webSocketHandler = context.getBeansOfType(WebSocketHandler.class).entrySet().iterator().next().getValue();
                    ch.pipeline().addLast("http-codec", new HttpServerCodec()).addLast("websocket_aggregator", new WebSocketFrameAggregator(1_000_0000))
                            .addLast("http_aggregator", new HttpObjectAggregator(1_000_0000)).addLast("http-chunked", new ChunkedWriteHandler())
                            .addLast(new IdleStateHandler(springBootNettyProperties.getReaderIdleTimeSeconds(), springBootNettyProperties.getWriterIdleTimeSeconds(), springBootNettyProperties.getAllIdleTimeSeconds()))
                            .addLast(webSocketHandler);
                }
            });

            setOption(b, springBootNettyProperties);

            int port = springBootNettyProperties.getPort();

            String ip = springBootNettyProperties.getAddress();
            if (ip == null || StringUtils.isNullOrBlank(ip)) {
                ip = getServerIp();
            }

            ChannelFuture bindChannelFuture;
            logger.info("try to bind Netty Server at " + ip + " :" + port);
            bindChannelFuture = b.bind(ip, port).sync();

            bindChannelFuture.sync();
            this.bossGroup = bossGroup;
            this.workerGroup = workerGroup;
        } catch (InterruptedException e) {
            stop();
            e.printStackTrace();
        }
    }

    public static String getServerIp() {
        String localIP = null;
        String netIP = null;
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean found = false;
            while (netInterfaces.hasMoreElements() && !found) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration address = ni.getInetAddresses();
                while (address.hasMoreElements()) {

                    InetAddress ip = (InetAddress) address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        netIP = ip.getHostAddress();
                        found = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {
                        localIP = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            return "0.0.0.0";
        }

        if (netIP != null && !"".equals(netIP)) {
            return netIP;
        } else {
            return localIP;
        }
    }

    private void setOption(ServerBootstrap b, SpringBootNettyProperties springBootNettyProperties) {
        setOption(b, ChannelOption.CONNECT_TIMEOUT_MILLIS, springBootNettyProperties.getConnectTimeoutMillis());
        setOption(b, ChannelOption.WRITE_SPIN_COUNT, springBootNettyProperties.getWriteSpinCount());
        setOption(b, ChannelOption.ALLOW_HALF_CLOSURE, springBootNettyProperties.getAllowHalfClosure());
        setOption(b, ChannelOption.AUTO_READ, springBootNettyProperties.getAutoRead());
        setOption(b, ChannelOption.SO_BROADCAST, springBootNettyProperties.getSoBroadcast());
        setOption(b, ChannelOption.SO_KEEPALIVE, springBootNettyProperties.getSoKeepalive());
        setOption(b, ChannelOption.SO_SNDBUF, springBootNettyProperties.getSoSndbuf());
        setOption(b, ChannelOption.SO_RCVBUF, springBootNettyProperties.getSoRcvbuf());
        setOption(b, ChannelOption.SO_REUSEADDR, springBootNettyProperties.getSoReuseaddr());
        setOption(b, ChannelOption.SO_BACKLOG, springBootNettyProperties.getSoBacklog());
        setOption(b, ChannelOption.IP_TOS, springBootNettyProperties.getIpTos());
        setOption(b, ChannelOption.IP_MULTICAST_ADDR, springBootNettyProperties.getIpMulticastAddr());
        setOption(b, ChannelOption.IP_MULTICAST_IF, springBootNettyProperties.getIpMulticastIf());
        setOption(b, ChannelOption.IP_MULTICAST_TTL, springBootNettyProperties.getIpMulticastTtl());
        setOption(b, ChannelOption.IP_MULTICAST_LOOP_DISABLED, springBootNettyProperties.getIpMulticastLoopDisabled());
        setOption(b, ChannelOption.TCP_NODELAY, springBootNettyProperties.getTcpNodelay());
        setOption(b, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, springBootNettyProperties.getSingleEventexecutorPerGroup());
        setOption(b, ChannelOption.SO_LINGER, springBootNettyProperties.getSoLinger());
        setOption(b, ChannelOption.SO_TIMEOUT, springBootNettyProperties.getSoTimeout());
    }

    private void setOption(ServerBootstrap b, ChannelOption option, Object value) {
        if (value != null) {
            b.option(option, value);
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
