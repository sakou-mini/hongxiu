package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.service.common.PersonalSettingService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;


@Service("AccountOfQueryPersonalSettingRequest")
@Slf4j
public class AccountOfQueryPersonalSettingRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    PersonalSettingService personalSettingService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfQueryPersonalSettingRequest();
        Account.AccountOfQueryPersonalSettingReply.Builder replyBuilder = Account.AccountOfQueryPersonalSettingReply.newBuilder();
        String requestUserId = request.getUserId();
        PersonalSetting setting = personalSettingService.findByUserId(requestUserId);
        if (Objects.nonNull(setting)) {
            replyBuilder.setPersonalSetting(setting.toProto());
        }
        return buildReply(userId, replyBuilder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
