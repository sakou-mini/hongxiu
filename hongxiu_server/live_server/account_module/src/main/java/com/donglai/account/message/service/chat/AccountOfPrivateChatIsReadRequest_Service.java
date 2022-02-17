package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.service.account.PrivateChatService;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-04 14:08
 */
@Service("AccountOfPrivateChatIsReadRequest")
public class AccountOfPrivateChatIsReadRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private PrivateChatService privateChatService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfPrivateChatIsReadRequest request = message.getAccountOfPrivateChatIsReadRequest();
        //获取需要已读ID
        List<Long> messageIdsList = request.getMessageIdsList().stream().map(Long::parseLong).collect(Collectors.toList());
        //校验参数
        if (CollectionUtils.isEmpty(messageIdsList)) {
            return buildReply(userId, Account.AccountOfPrivateChatIsReadReply.newBuilder(), MISSING_OR_ILLEGAL_PARAMETERS);
        }
        //查询对象
        List<PrivateChat> byIdInOrderByTimeAsc = privateChatService.findByIdInOrderByTimeAsc(messageIdsList);
        //遍历修改
        for (PrivateChat privateChat : byIdInOrderByTimeAsc) {
            privateChat.setRead(true);
            privateChatService.save(privateChat);
        }
        return buildReply(userId, Account.AccountOfPrivateChatIsReadReply.newBuilder(), SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
