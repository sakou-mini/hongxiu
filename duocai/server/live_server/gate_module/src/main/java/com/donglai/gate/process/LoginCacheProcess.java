package com.donglai.gate.process;

import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.common.util.StringUtils;
import com.donglai.gate.cache.GateCache;
import com.donglai.gate.constant.GateConstant;
import com.donglai.gate.message.producer.Producer;
import com.donglai.gate.util.GateUtil;
import com.donglai.gate.util.UserCacheUtil;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.ProtoBufMapper;
import com.donglai.protocol.message.KafkaMessage;
import com.donglai.protocol.util.PbRefUtil;
import com.google.common.base.Strings;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.SUCCESS;
import static com.donglai.protocol.util.PbRefUtil.buildReply;

@Slf4j
public class LoginCacheProcess {
    public static void loginSuccess(String userid, Map<KafkaMessage.ExtraParam, String> extraParams, Constant.PlatformType platform) {
        //登录成功，清除ctxTempCache，考虑幂等性，new_ctx可能为null时，属于重复登录请求
        ChannelHandlerContext new_ctx = null;
        String channelId = extraParams.getOrDefault(KafkaMessage.ExtraParam.CHANNEL_ID, "");
        String lastUserId = extraParams.getOrDefault(KafkaMessage.ExtraParam.LAST_USER_ID, "");
        log.info("channelId is {} ,latUserId is {}",channelId,lastUserId);
        if (!StringUtils.isNullOrBlank(channelId)) {
            //该链接在从未登录的情况下登录
            new_ctx = GateCache.removeCtxTempCacheByChnnelIdx(extraParams.getOrDefault(KafkaMessage.ExtraParam.CHANNEL_ID, ""));
        } else {
            //在已经登录的状态下登录其他账号。此时channel 并未断开
            new_ctx = GateCache.geCtxByUserid(lastUserId);
        }
        if (new_ctx == null)
            return;
        ChannelHandlerContext old_ctx = GateCache.geCtxByUserid(lastUserId);
        ChannelHandlerContext userLoginChannel = GateCache.geCtxByUserid(userid);
        //踢出旧连接
        if (Objects.nonNull(userLoginChannel) && !new_ctx.equals(userLoginChannel)) {
            log.info("{}重复登陆", userid);
            repeatedLogin(userLoginChannel);
            //userLoginChannel.channel().disconnect();
        } else if (!Objects.equals(lastUserId, userid) && Objects.equals(old_ctx, new_ctx)) {
            log.info("{} 切换为 {} 账号登录", lastUserId, userid);
            //将上一个用户剔除 并发送logout
            sendLogout(lastUserId, platform);
        }
        //刷新ctxCache
        GateCache.putPlatformChannel(userid, new_ctx);

        //记录userid 和 平台信息到ctx
        GateUtil.setChannelAttr(GateConstant.ChannelAttrUserId, userid, new_ctx);
        GateUtil.setChannelAttr(GateConstant.ChannelAttrPlatform, platform, new_ctx);
    }

    public static void updateCacheOnClose(ChannelHandlerContext ctx) {
        String userId = (String) GateUtil.getChannelAttr(GateConstant.ChannelAttrUserId, null, ctx);
        Constant.PlatformType platform = (Constant.PlatformType) GateUtil.getChannelAttr(GateConstant.ChannelAttrPlatform, null, ctx);
        UserCacheUtil.removeOnlineUser(userId, platform);
        log.info("连接" + userId + "在" + System.currentTimeMillis() / 1000 + "秒断开");
        //未登录，清理临时缓存
        if (Strings.isNullOrEmpty(userId) || platform == null) {
            GateCache.removeCtxTempCacheByChnnelIdx(GateUtil.getChannelId(ctx));
            return;
        }
        //已登录，清理缓存
        ChannelHandlerContext new_ctx = GateCache.geCtxByUserid(userId);
        if (ctx.equals(new_ctx)) {
            sendLogout(userId, platform);
        }
    }

    public static void sendLogout(String userId,Constant.PlatformType platform){
        SpringApplicationContext.getBean(Producer.class).send(buildLogoutMessage(userId, platform));
        GateCache.removePlatformChannel(userId);
    }

    private static void repeatedLogin(ChannelHandlerContext ctx) {
        var replyBuilder = Account.AccountOfRepeatedLoginBroadcastMessage.newBuilder();
        GateUtil.sendMessageToChannel(ctx, buildReply(replyBuilder.build(), SUCCESS));
    }

    public static KafkaMessage.TopicMessage buildLogoutMessage(String userId, Constant.PlatformType platform) {
        HongXiu.HongXiuMessageRequest.Builder request = HongXiu.HongXiuMessageRequest.newBuilder().setAccountOfLogoutRequest(Account.AccountOfLogoutRequest.newBuilder());
        String topic = PbRefUtil.getSendTopic(ProtoBufMapper.MessageType.REQUEST_MSG, HongXiu.HongXiuMessageRequest.ACCOUNTOFLOGOUTREQUEST_FIELD_NUMBER);
        int messageId = HongXiu.HongXiuMessageRequest.ACCOUNTOFLOGOUTREQUEST_FIELD_NUMBER;
        return new KafkaMessage.TopicMessage(platform, topic, userId, messageId, request.build());
    }
}
