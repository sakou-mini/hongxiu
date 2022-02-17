package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
import com.donglai.account.util.WordFilterUtil;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SIGNATURE_TEXT_LENGTH_ERROR;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("AccountOfUpdateSignatureTextRequest")
public class AccountOfUpdateSignatureTextRequest_Service implements TopicMessageServiceI<String> {
    @Value("${signature.text.max.text.length}")
    private int signatureText;
    @Autowired
    private UserService userService;
    @Autowired
    WordFilterUtil wordFilterUtil;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdateSignatureTextRequest();
        var reply = Account.AccountOfUpdateSignatureTextReply.newBuilder();
        String context = request.getContext();
        Constant.ResultCode resultCode;
        if (Objects.isNull(context) || context.length() > signatureText) {
            resultCode = SIGNATURE_TEXT_LENGTH_ERROR;
        } else {
            resultCode = SUCCESS;
            context = wordFilterUtil.replaceSensitiveWord(context, "*");
            User user = userService.findById(userId);
            user.setSignatureText(context);
            userService.save(user);
            reply.setUserInfo(userProcess.buildUerDetailInfo(user));
        }
        return buildReply(userId, reply.build(), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
