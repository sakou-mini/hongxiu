package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
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

@Service("AccountOfUpdateGenderRequest")
public class AccountOfUpdateGenderRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdateGenderRequest();
        var reply = Account.AccountOfUpdateGenderReply.newBuilder();
        User user = userService.findById(userId);
        user.setGender(request.getGender());
        userService.save(user);
        reply.setUserInfo(userProcess.buildUerDetailInfo(user));
        return buildReply(userId, reply.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
