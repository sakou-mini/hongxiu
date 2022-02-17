package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.service.account.PrivateChatInBlackService;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-02 11:23
 */
@Service("AccountOfPrivateChatBlackRegainRequest")
public class AccountOfPrivateChatBlackRegainRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private PrivateChatInBlackService privateChatInBlackService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfPrivateChatBlackRegainRequest request = message.getAccountOfPrivateChatBlackRegainRequest();
        //获取要删除的黑名单人员
        String blackUserId = request.getUserId();
        if (StringUtils.isEmpty(blackUserId)) {
            return buildReply(userId, Account.AccountOfPrivateChatBlackRegainReply.newBuilder(), MISSING_OR_ILLEGAL_PARAMETERS);
        }
        privateChatInBlackService.deleteByUserIdAndBlackUserId(userId, blackUserId);
        return buildReply(userId, Account.AccountOfPrivateChatBlackRegainReply.newBuilder().setUserId(blackUserId).build(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
