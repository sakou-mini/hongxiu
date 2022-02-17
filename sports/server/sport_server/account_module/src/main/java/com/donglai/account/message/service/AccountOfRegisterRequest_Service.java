package com.donglai.account.message.service;

import com.donglai.account.service.impl.platform.PlatformServiceFactory;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.CastUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service("AccountOfRegisterRequest")
@Slf4j
public class AccountOfRegisterRequest_Service implements TopicMessageServiceI<String> {

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var platform = message.getPlatform();
        var request = message.getAccountOfRegisterRequest();
        Map<KafkaMessage.ExtraParam, String> extraParam = CastUtil.cast(param[0]);
        var replyBuilder = Account.AccountOfRegisterReply.newBuilder();
        String password = request.getPassword();
        if (StringUtils.isNullOrBlank(password)) {
            password = UUID.randomUUID().toString();
            request = request.toBuilder().setPassword(password).build();
        }
        User user = PlatformServiceFactory.getPlatformService(platform).register(request, platform);
        replyBuilder.setPassword(password).setAccountId(user.getAccountId());
        log.info("new user register saved: accountId:<{}>,display name:<{}>", user.getAccountId(), user.getNickname());
        return buildReply(user.getId(), replyBuilder.build(), SUCCESS, extraParam, message.getPlatform());
    }

    @Override
    public void Close(String s) {

    }
}
