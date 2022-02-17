package com.donglai.account.message.service;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;
import static com.donglai.protocol.Constant.ResultCode.USER_NOT_FOUND;

@Service("AccountOfQueryUserInfosRequest")
@Slf4j
public class AccountOfQueryUserInfosRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    FollowListService followListService;
    @Autowired
    UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfQueryUserInfosRequest();
        var reply = Account.AccountOfQueryUserInfosReply.newBuilder();
        User user;
        Constant.ResultCode resultCode = SUCCESS;
        for (String requestUserId : request.getUserIdsList()) {
            user = userService.findById(requestUserId);
            if (user == null) {
                resultCode = USER_NOT_FOUND;
                log.warn("not found user by id {}", requestUserId);
                break;
            } else {
                resultCode = SUCCESS;
                Common.UserInfo userInfo = userProcess.buildUerDetailInfo(user);
                if (!Objects.equals(user.getId(), userId)) userInfo.toBuilder().clearCoin();
                reply.addUserInfos(userInfo);
            }
        }
        return buildReply(userId, reply, resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
