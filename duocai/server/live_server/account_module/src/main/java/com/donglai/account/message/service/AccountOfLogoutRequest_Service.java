package com.donglai.account.message.service;

import com.donglai.account.message.producer.Producer;
import com.donglai.account.process.ModuleNoticeProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("AccountOfLogoutRequest")
@Slf4j
public class AccountOfLogoutRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    Producer producer;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        User user = userService.findById(userId);
        if (user == null) {
            log.warn("not found user {}", userId);
            return null;
        }
        user.setLogoutTime(System.currentTimeMillis());
        userService.save(user);
        //TODO 通知直播服,用户掉线了
        ModuleNoticeProcess.noticeLiveServerUserDisConnection(userId);
        log.info("用户{}退出登录", user.getId());
        return null;
    }


    @Override
    public void Close(String s) {

    }
}
