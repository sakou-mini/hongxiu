package com.donglai.gate.message.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.protocol.message.KafkaMessage;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("AccountOfLogoutRequest")
public class AccountOfLogoutReply_Service implements GateMessageServiceI<String> {

    @Override
    public void Process(String userId, KafkaMessage.TopicMessage topicMessage, Message message) {
/*        Map<KafkaMessage.ExtraParam, String> extraParams = topicMessage.getExtraParams();
        String channelId = extraParams.getOrDefault(KafkaMessage.ExtraParam.CHANNEL_ID, "");
        log.info("{}  退出了登录", userId);
        ChannelHandlerContext ctx = GateCache.geCtxByUserid(userId);
        if(Objects.isNull(ctx)) return;
        if(Objects.equals(GateUtil.getChannelId(ctx),channelId)){
            ctx.disconnect();
            //非线程安全
            GateCache.removeUserChannel(userId);
        }*/
    }

    @Override
    public void Close(String s) {

    }
}

