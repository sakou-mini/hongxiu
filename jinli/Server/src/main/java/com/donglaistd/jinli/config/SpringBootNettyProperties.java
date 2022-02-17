package com.donglaistd.jinli.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.NetworkInterface;

@ConfigurationProperties(prefix = "spring.boot.netty")
public class SpringBootNettyProperties {
    @Value("${netty.server.ip}")
    private String address;
    @Value("${netty.server.port}")
    int port;
    private Integer readerIdleTimeSeconds = 60;
    private Integer writerIdleTimeSeconds = 30;
    private Integer allIdleTimeSeconds = 30;
    private Integer bossGroupThreadSize = 1;
    private Integer workGroupThreadSize = 4;

    /**
     * #{@link ChannelOption#ALLOCATOR}
     */
//    private ByteBufAllocator allocator = null;

    /**
     * #{@link ChannelOption#RCVBUF_ALLOCATOR}
     */
//    private RecvByteBufAllocator rcvbufAllocator = null;

    /**
     * #{@link ChannelOption#MESSAGE_SIZE_ESTIMATOR}
     */
//    private MessageSizeEstimator messageSizeEstimator = null;

    /**
     * #{@link ChannelOption#CONNECT_TIMEOUT_MILLIS}
     */
    private Integer connectTimeoutMillis = null;

    /**
     * #{@link ChannelOption#WRITE_SPIN_COUNT}
     */
    private Integer writeSpinCount = null;

    /**
     * #{@link ChannelOption#ALLOW_HALF_CLOSURE}
     */
    private Boolean allowHalfClosure = null;

    /**
     * #{@link ChannelOption#AUTO_READ}
     */
    private Boolean autoRead = null;

    /**
     * #{@link ChannelOption#SO_BROADCAST}
     */
    private Boolean soBroadcast = null;

    /**
     * #{@link ChannelOption#SO_KEEPALIVE}
     */
    private Boolean soKeepalive = null;

    /**
     * #{@link ChannelOption#SO_SNDBUF}
     */
    private Integer soSndbuf = null;

    /**
     * #{@link ChannelOption#SO_RCVBUF}
     */
    private Integer soRcvbuf = null;

    /**
     * #{@link ChannelOption#SO_REUSEADDR}
     */
    private Boolean soReuseaddr = null;

    /**
     * #{@link ChannelOption#SO_LINGER}
     */
    private Integer soLinger = null;

    /**
     * #{@link ChannelOption#SO_BACKLOG}
     */
    private Integer soBacklog = null;

    /**
     * #{@link ChannelOption#SO_TIMEOUT}
     */
    private Integer soTimeout = null;

    /**
     * #{@link ChannelOption#IP_TOS}
     */
    private Integer ipTos = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_ADDR}
     */
    private InetAddress ipMulticastAddr = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_IF}
     */
    private NetworkInterface ipMulticastIf = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_TTL}
     */
    private Integer ipMulticastTtl = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_LOOP_DISABLED}
     */
    private Boolean ipMulticastLoopDisabled = null;

    /**
     * #{@link ChannelOption#TCP_NODELAY}
     */
    private Boolean tcpNodelay = null;

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public Integer getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public Integer getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public Integer getBossGroupThreadSize() {
        return bossGroupThreadSize;
    }

    public Integer getWorkGroupThreadSize() {
        return workGroupThreadSize;
    }

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public Integer getWriteSpinCount() {
        return writeSpinCount;
    }

    public Boolean getAllowHalfClosure() {
        return allowHalfClosure;
    }

    public Boolean getAutoRead() {
        return autoRead;
    }

    public Boolean getSoBroadcast() {
        return soBroadcast;
    }

    public Boolean getSoKeepalive() {
        return soKeepalive;
    }

    public Integer getSoSndbuf() {
        return soSndbuf;
    }

    public Integer getSoRcvbuf() {
        return soRcvbuf;
    }

    public Boolean getSoReuseaddr() {
        return soReuseaddr;
    }

    public Integer getSoLinger() {
        return soLinger;
    }

    public Integer getSoBacklog() {
        return soBacklog;
    }

    public Integer getSoTimeout() {
        return soTimeout;
    }

    public Integer getIpTos() {
        return ipTos;
    }

    public InetAddress getIpMulticastAddr() {
        return ipMulticastAddr;
    }

    public NetworkInterface getIpMulticastIf() {
        return ipMulticastIf;
    }

    public Integer getIpMulticastTtl() {
        return ipMulticastTtl;
    }

    public Boolean getIpMulticastLoopDisabled() {
        return ipMulticastLoopDisabled;
    }

    public Boolean getTcpNodelay() {
        return tcpNodelay;
    }

    public Boolean getSingleEventexecutorPerGroup() {
        return singleEventexecutorPerGroup;
    }

    /**
     * #{@link ChannelOption#SINGLE_EVENTEXECUTOR_PER_GROUP}
     */
    private Boolean singleEventexecutorPerGroup = null;
}
