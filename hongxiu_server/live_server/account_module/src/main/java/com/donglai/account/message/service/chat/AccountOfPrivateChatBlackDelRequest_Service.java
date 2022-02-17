package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChatInBlack;
import com.donglai.model.db.service.account.PrivateChatInBlackService;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-02 10:44
 */
@Service("AccountOfPrivateChatBlackDelRequest")
public class AccountOfPrivateChatBlackDelRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private PrivateChatInBlackService privateChatInBlackService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfPrivateChatBlackDelRequest request = message.getAccountOfPrivateChatBlackDelRequest();
        //获取要删除的黑名单人员
        String blackUserId = request.getUserId();
        //校验参数
        if (StringUtils.isEmpty(blackUserId)) {
            return buildReply(userId, Account.AccountOfPrivateChatBlackDelReply.newBuilder(), MISSING_OR_ILLEGAL_PARAMETERS);
        }
        //查询数据
        PrivateChatInBlack byUserIdByBlackUserId = privateChatInBlackService.findByUserIdAndBlackUserId(userId, blackUserId);
        if (Objects.nonNull(byUserIdByBlackUserId)) {
            //设置逻辑删除
            byUserIdByBlackUserId.setDel(true);
            //更新到数据库
            privateChatInBlackService.save(byUserIdByBlackUserId);
        }

        return buildReply(userId, Account.AccountOfPrivateChatBlackDelReply.newBuilder().setUserId(blackUserId), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
