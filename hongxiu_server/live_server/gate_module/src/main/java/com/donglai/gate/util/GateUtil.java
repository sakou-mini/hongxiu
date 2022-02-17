package com.donglai.gate.util;

import com.donglai.gate.cache.GateCache;
import com.donglai.gate.constant.GateConstant;
import com.donglai.protocol.util.PbRefUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class GateUtil {

    public static Object getChannelAttr(String key, Object value, ChannelHandlerContext ctx) {
        if (ctx == null || ctx.channel() == null)
            return null;
        return ctx.channel().attr(AttributeKey.valueOf(key)).setIfAbsent(value);
    }

    public static void setChannelAttr(String key, Object value, ChannelHandlerContext ctx) {
        if (ctx != null && ctx.channel() != null) {
            ctx.channel().attr(AttributeKey.valueOf(key)).set(value);
        }
    }

    public static String getChannelId(ChannelHandlerContext ctx) {
        return Optional.ofNullable(ctx).map(ChannelHandlerContext::channel).map(Channel::id).map(ChannelId::asLongText).orElse(null);
    }

    public static String getMsgUserId(ChannelHandlerContext ctx, String userid) {
        Object obj = GateUtil.getChannelAttr(GateConstant.ChannelAttrUserId, userid, ctx);
        if (obj != null)
            return obj.toString();
        return null;
    }

    public static String getMsgPlatform(ChannelHandlerContext ctx) {
        Object obj = GateUtil.getChannelAttr(GateConstant.ChannelAttrPlatform, null, ctx);
        if (obj != null)
            return obj.toString();
        return null;
    }

    public static void sendData(String userid, Object msg) {
        sendData(userid, msg, "");
    }

    public static void sendData(String userid, Object msg, String channelId) {
        ChannelHandlerContext ctx = GateCache.geCtxByUserid(userid);
        if (ctx == null) {
            ctx = GateCache.getCtxByChannelId(channelId);
            if (ctx == null) {
                log.info("未找到消息源，不再发送");
                return;
            }
        }
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(PbRefUtil.getPbBytes(msg))));
    }

    public static void sendMessageToChannel(ChannelHandlerContext ctx, Object msg) {
        ctx.writeAndFlush(new BinaryWebSocketFrame(Unpooled.copiedBuffer(PbRefUtil.getPbBytes(msg))));
    }
}
