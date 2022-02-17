package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.PasswordUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

@Service("AccountOfUpdatePasswordRequest")
@Slf4j
public class AccountOfUpdatePasswordRequest_Service implements TopicMessageServiceI<String> {
    @Value("${max.password.length}")
    private int maxPasswordLength;

    @Value("${min.password.length}")
    private int minPasswordLength;
    @Autowired
    UserService userService;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdatePasswordRequest();
        var reply = Account.AccountOfUpdatePasswordReply.newBuilder();
        User user = userService.findById(userId);
        String newPassword = request.getNewPassword();
        Constant.ResultCode resultCode;
        if (!PasswordUtil.checkEncodePassword(request.getOldPassword(), user.getPassword())) {
            resultCode = PASSWORD_ERROR;
        } else if (StringUtils.isNullOrBlank(newPassword) || newPassword.length() > maxPasswordLength || newPassword.length() < minPasswordLength) {
            resultCode = PASSWORD_ILLEGAL;
        } else {
            resultCode = SUCCESS;
            user.setPassword(PasswordUtil.encodePassword(newPassword));
            user.setTourist(false);
            userService.save(user);
            log.info("set user password: accountName:<" + user.getAccountId() + ">, display name:<" + user.getNickname() + ">");
            reply.setUserInfo(userProcess.buildUerDetailInfo(user));
        }
        return buildReply(userId, reply.build(), resultCode);

    }

    @Override
    public void Close(String s) {

    }
}
