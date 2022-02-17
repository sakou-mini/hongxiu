package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;

@Service("AccountOfIsFollowRequest")
public class AccountOfIsFollowRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    FollowListService followListService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfIsFollowRequest();
        var reply = Account.AccountOfIsFollowReply.newBuilder();
        boolean userFollower = followListService.isUserFollower(request.getUserId(), userId);
        reply.setIsFollow(userFollower).setLeaderUserId(request.getUserId());
        return buildReply(userId, reply, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
