package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2021-12-03 16:44
 */
@Service("AccountOfUpdateLeadAliasRequest")
public class AccountOfUpdateLeadAliasRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private FollowListService followListService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfUpdateLeadAliasRequest();
        var builder = Account.AccountOfUpdateLeadAliasReply.newBuilder();

        String name = request.getName();
        String leaderId = request.getLeaderId();
        //查询该条关注数据
        FollowList byLeaderIdAndFollowerId = followListService.findByLeaderIdAndFollowerId(leaderId, userId);
        if (Objects.isNull(byLeaderIdAndFollowerId)) {
            return buildReply(userId, builder, Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS);
        }
        //设置
        byLeaderIdAndFollowerId.setAlias(name);
        followListService.save(byLeaderIdAndFollowerId);
        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
