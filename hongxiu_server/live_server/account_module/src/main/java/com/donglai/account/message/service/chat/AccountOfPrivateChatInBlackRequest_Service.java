package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChatInBlack;
import com.donglai.model.db.service.account.PrivateChatInBlackService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.*;

/**
 * @author Moon
 * @date 2021-11-01 17:40
 */
@Service("AccountOfPrivateChatInBlackRequest")
public class AccountOfPrivateChatInBlackRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private PrivateChatInBlackService privateChatInBlackService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfPrivateChatInBlackRequest();
        Constant.ResultCode resultCode;
        String blackUserId = request.getUserId();
        if (StringUtils.isEmpty(blackUserId)) {
            return buildReply(userId, Account.AccountOfPrivateChatInBlackReply.newBuilder(), MISSING_OR_ILLEGAL_PARAMETERS);
        }
        PrivateChatInBlack chatInBlack = privateChatInBlackService.findByUserIdAndBlackUserId(userId, blackUserId);
        if (Objects.nonNull(chatInBlack)) {
            resultCode = REPEATED_ADD_BLACK;
        } else {
            resultCode = SUCCESS;
            //拉黑
            chatInBlack = PrivateChatInBlack.newInstance(userId, blackUserId);
            privateChatInBlackService.save(chatInBlack);
        }
        return buildReply(userId, Account.AccountOfPrivateChatInBlackReply.newBuilder().setUserId(blackUserId), resultCode);
    }

    @Override
    public void Close(String s) {

    }
}
