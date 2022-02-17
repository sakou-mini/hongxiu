package com.donglai.account.process;

import com.donglai.account.message.producer.Producer;
import com.donglai.common.contxet.SpringApplicationContext;
import com.donglai.protocol.Live;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ModuleNoticeProcess {
    public static Producer producer = SpringApplicationContext.getBean(Producer.class);

    //--------------------------------------LiveServer notice=================================
    //通知用户重连
    public static void noticeLiveServerUserConnection(String userId, Map<KafkaMessage.ExtraParam, String> extraParam) {
        Live.LiveOfUserConnectionRequest.Builder request = Live.LiveOfUserConnectionRequest.newBuilder().setUserId(userId);
        producer.sendMessageRequest(userId, request.build(), null, extraParam);
    }

    //通知用户断开连接
    public static void noticeLiveServerUserDisConnection(String userId) {
        Live.LiveOfUserDisconnectionRequest.Builder request = Live.LiveOfUserDisconnectionRequest.newBuilder().setUserId(userId);
        producer.sendMessageRequest(userId, request.build(), null);
    }

    //通知用户关注和取关消息
  /*  public static void noticeLiveServerUserFollowOrUnFollow(String userId,String leaderId, String followerId, Constant.FollowType followType){
        Live.LiveOfUserFollowOrUnFollowRequest.Builder request = Live.LiveOfUserFollowOrUnFollowRequest.newBuilder()
                .setLeaderId(leaderId).setFollowerUserId(followerId).setFollowType(followType);
        producer.sendMessageRequest(userId, request.build(),null);
    }*/
}
