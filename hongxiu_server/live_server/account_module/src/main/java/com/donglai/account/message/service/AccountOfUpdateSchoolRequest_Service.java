package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SCHOOL_IS_EMPTY;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("AccountOfUpdateSchoolRequest_Service")
public class AccountOfUpdateSchoolRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdateSchoolRequest();
        var replyBuilder = Account.AccountOfUpdateSchoolReply.newBuilder();
        User user = userService.findById(userId);
        Constant.ResultCode resultCode;
        if (StringUtils.isNullOrBlank(request.getSchool())) {
            resultCode = SCHOOL_IS_EMPTY;
        } else {
            resultCode = SUCCESS;
            user.setSchool(request.getSchool());
            userService.save(user);
            replyBuilder.setUserInfo(userProcess.buildUerDetailInfo(user));
        }
        return buildReply(userId, replyBuilder.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
