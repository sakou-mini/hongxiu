package com.donglai.gate.process;


import com.donglai.gate.cache.GateCache;
import com.donglai.gate.constant.GateConstant;
import com.donglai.gate.util.GateUtil;
import com.donglai.protocol.message.KafkaMessage;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class GenerlProcess
{
    //驗證CHANNEL是否合法
    public static boolean isVerify(String userid){
        if(Strings.isNullOrEmpty(userid))
            return false;
        return GateCache.geCtxByUserid(userid) != null;
    }

    //未登錄處理
    public static void unVerify(KafkaMessage.TopicMessage message, ChannelHandlerContext ctx){
        //未登录
        if (Strings.isNullOrEmpty(message.getUserid()))
        {
            Map<KafkaMessage.ExtraParam, String> extras = new HashMap<>();
            String channelId = GateUtil.getChannelId(ctx);
            extras.put(KafkaMessage.ExtraParam.CHANNEL_ID, channelId);
            extras.put(KafkaMessage.ExtraParam.IP, GateUtil.getChannelAttr(GateConstant.ChannelAttrIP, "", ctx).toString());
            //记录临时通道
            GateCache.putCtxTempCache(channelId, ctx);
            message.setExtraParams(extras);
        }
    }
}
