package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.PersonalSetting;
import com.donglai.model.db.service.common.PersonalSettingService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Service(" AccountOfSetPersonalSettingRequest")
public class AccountOfSetPersonalSettingRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    PersonalSettingService personalSettingService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfSetPersonalSettingRequest();
        var reply = Account.AccountOfSetPersonalSettingReply.newBuilder();
        Common.PersonalSetting requestPersonalSetting = request.getPersonalSetting();
        PersonalSetting personalSetting = Optional.ofNullable(personalSettingService.findByUserId(userId)).orElse(PersonalSetting.newInstance(userId));
        personalSetting.setCommentPermission(requestPersonalSetting.getCommentPermission());
        personalSetting.setPrivateChatPermission(requestPersonalSetting.getPrivateChatPermission());
        personalSetting.setShowFansAndLeaderList(requestPersonalSetting.getShowFansAndLeaderList());
        personalSetting.setShowMyBlogsPraise(requestPersonalSetting.getShowMyBlogsPraise());
        personalSettingService.save(personalSetting);
        return buildReply(userId, reply, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
