package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2021-11-30 11:22
 */
@Service("AccountOfFistLoginRequest")
public class AccountOfFistLoginRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private UserService userService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfFistLoginRequest request = message.getAccountOfFistLoginRequest();
        Account.AccountOfFistLoginReply.Builder builder = Account.AccountOfFistLoginReply.newBuilder();
        User user = userService.findById(userId);
        //修改第一次登录状态
        user.setFirstLogin(false);
        userService.save(user);
        return buildReply(userId, builder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
