package com.donglai.gate.cache;

import com.donglai.common.util.StringUtils;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GateCache {

    // 用戶channel緩存 <useId,channel>
    private static final Map<String, ChannelHandlerContext> userCtxCache = new ConcurrentHashMap<>();

    // 用戶channel臨時緩存<channelname,channel> key channelId  value channel
    private static final Map<String, ChannelHandlerContext> ctxTempCache = new ConcurrentHashMap<>();

    /***********************************************platformUserCtxCache操作*************************************************/
    public static ChannelHandlerContext geCtxByUserid(String userid) {
        if (StringUtils.isNullOrBlank(userid))
            return null;
        return userCtxCache.get(userid);
    }

    public static void putPlatformChannel(String userid, ChannelHandlerContext channel) {
        if (channel == null || StringUtils.isNullOrBlank(userid))
            return;
        userCtxCache.put(userid, channel);
    }

    public static void removeUserChannel(String userid) {
        if (Strings.isNullOrEmpty(userid) || !userCtxCache.containsKey(userid))
            return;
        userCtxCache.remove(userid);
    }

    /***********************************************ctxTempCache操作*************************************************/
    public static ChannelHandlerContext getCtxByChannelId(String channelId) {
        if (StringUtils.isNullOrBlank(channelId))
            return null;
        return ctxTempCache.get(channelId);
    }

    public static void putCtxTempCache(String channelId, ChannelHandlerContext channel) {
        if (Strings.isNullOrEmpty(channelId) || channel == null)
            return;
        ctxTempCache.put(channelId, channel);
    }

    public static ChannelHandlerContext removeCtxTempCacheByChnnelIdx(String channelIdx) {
        if (!ctxTempCache.containsKey(channelIdx))
            return null;

        return ctxTempCache.remove(channelIdx);
    }

    /*获取当前平台所有的用户缓存*/
    public static Set<String> getAllCacheUserId() {
        return userCtxCache.keySet();
    }
}
