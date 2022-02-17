package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("AccountOfUpdateUserAvatarRequest")
public class AccountOfUpdateUserAvatarRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdateUserAvatarRequest();
        var reply = Account.AccountOfUpdateUserAvatarReply.newBuilder();
        Constant.ResultCode resultCode;
        if (StringUtil.isNullOrEmpty(request.getAvatarUrl())) {
            resultCode = MISSING_OR_ILLEGAL_PARAMETERS;
        } else {
            resultCode = SUCCESS;
            User user = userService.findById(userId);
            user.setAvatarUrl(request.getAvatarUrl());
            userService.save(user);
            reply.setUserInfo(userProcess.buildUerDetailInfo(user));
        }
        return buildReply(userId, reply.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
