package com.donglai.account.message.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.donglai.account.entityBuilder.UserBuilder;
import com.donglai.account.process.AuthCodeProcess;
import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.service.RedisService;
import com.donglai.common.util.CastUtil;
import com.donglai.common.util.PasswordUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.donglai.account.util.MessageUtil.buildReply;

@Slf4j
@Service("AccountOfVerifyAccountAuthCodeRequest")
public class AccountOfVerifyAccountAuthCodeRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    UserProcess userProcess;
    @Autowired
    RedisService redisService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Map<KafkaMessage.ExtraParam, String> extraParam = CastUtil.cast(param[0]);
        var request = message.getAccountOfVerifyAccountAuthCodeRequest();
        var reply = Account.AccountOfVerifyAccountAuthCodeReply.newBuilder();
        Constant.ResultCode resultCode;
        String phoneNumber = request.getPhoneNumber();
        String authCode = AuthCodeProcess.getAuthCode(request.getPhoneNumber(), Constant.AuthCodeType.ACCOUNT_AUTH);
        //TODO verifyCode check
        /* if (!Objects.equals(authCode, request.getCode())) {
            resultCode = Constant.ResultCode.ERROR_AUTH_CODE;
        } else */
        {
            resultCode = Constant.ResultCode.SUCCESS;
            User user = userService.findByPhoneNumber(phoneNumber);
            if (Objects.isNull(user)) {
                String password = PasswordUtil.encodePassword(UUID.randomUUID().toString());
                user = userBuilder.createUserByPhone(password, System.currentTimeMillis(), phoneNumber);
                user.setSource("phone");
            }
            reply.setAccountId(user.getAccountId()).setPassword(PasswordUtil.decodePassword(user.getPassword()));
        }
        return buildReply(userId, reply.build(), resultCode, extraParam, message.getPlatform());
    }

    @Override
    public void Close(String s) {

    }
}
