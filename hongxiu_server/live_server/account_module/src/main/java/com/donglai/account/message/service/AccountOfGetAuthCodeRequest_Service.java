package com.donglai.account.message.service;

import com.donglai.account.process.AuthCodeProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.CastUtil;
import com.donglai.common.util.VerifyUtil;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.ERROR_PHONE;

@Service("AccountOfGetAuthCodeRequest")
public class AccountOfGetAuthCodeRequest_Service implements TopicMessageServiceI<String> {

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfGetAuthCodeRequest();
        var reply = Account.AccountOfGetAuthCodeReply.newBuilder();
        Map<KafkaMessage.ExtraParam, String> extraParam = CastUtil.cast(param[0]);
        Constant.ResultCode resultCode;
        String phoneNumber = request.getPhoneNumber();
        if (VerifyUtil.verifyPhoneNumber(phoneNumber)) {
            resultCode = AuthCodeProcess.dealSendPhoneAuthCode(phoneNumber, request.getType());
        } else {
            resultCode = ERROR_PHONE;
        }
        return buildReply(userId, reply.build(), resultCode, extraParam, null);
    }

    @Override
    public void Close(String s) {

    }
}
