/*
package com.donglai.gate.message.services;

import com.donglai.common.dispatcher.GateMessageServiceI;
import com.donglai.gate.message.producer.Producer;
import com.donglai.gate.process.LoginCacheProcess;
import com.donglai.gate.util.GateUtil;
import com.donglai.gate.util.UserCacheUtil;
import com.donglai.protocol.Constant;
import com.donglai.protocol.message.KafkaMessage.TopicMessage;
import com.google.protobuf.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.protocol.message.KafkaMessage.ExtraParam.CHANNEL_ID;
import static com.donglai.protocol.util.PbRefUtil.buildReply;

*/
/**
 * 连接登录处理
 *//*

@Service("LiveOfLoginReply")
@Slf4j
public class LiveOfLoginReply_Service implements GateMessageServiceI<String> {
    @Autowired
    Producer producer;

    @Override
    public void Process(String userid, TopicMessage topicMessage, Message message) {
        if (Objects.equals(Constant.ResultCode.SUCCESS, topicMessage.getResultCode())) {
            log.info("{}  登录成功", userid);
            LoginCacheProcess.loginSuccess(userid, topicMessage.getExtraParams(), topicMessage.getPlatform());
            UserCacheUtil.addOnlineUser(userid, topicMessage.getPlatform());
        }
        GateUtil.sendData(userid, buildReply(message, topicMessage.getResultCode()), topicMessage.getExtraParams().get(CHANNEL_ID));
    }

    @Override
    public void Close(String s) {

    }
}
*/
