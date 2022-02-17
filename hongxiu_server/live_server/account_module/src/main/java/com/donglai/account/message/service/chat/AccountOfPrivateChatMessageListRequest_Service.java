package com.donglai.account.message.service.chat;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.entity.account.PrivateChatSession_Message;
import com.donglai.model.db.service.account.PrivateChatService;
import com.donglai.model.db.service.account.PrivateChatSessionMessageService;
import com.donglai.protocol.Account;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-02 14:04
 */
@Service("AccountOfPrivateChatMessageListRequest")
public class AccountOfPrivateChatMessageListRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private PrivateChatSessionMessageService privateChatSessionMessageService;

    @Autowired
    private PrivateChatService privateChatService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfPrivateChatMessageListRequest request = message.getAccountOfPrivateChatMessageListRequest();
        Account.AccountOfPrivateChatMessageListReply.Builder builder = Account.AccountOfPrivateChatMessageListReply.newBuilder();
        String sessionId = request.getSessionId();
        if (Long.parseLong(sessionId) == 0) {
            return buildReply(userId, builder, MISSING_OR_ILLEGAL_PARAMETERS);
        }
        //我的会话里所有的消息关联
        List<PrivateChatSession_Message> bySessionId = privateChatSessionMessageService.findBySessionId(Long.parseLong(sessionId));
        List<Long> messageIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(bySessionId)) {
            messageIds = bySessionId.stream().map(PrivateChatSession_Message::getMessageId).collect(Collectors.toList());
        }
        List<PrivateChat> privateChatList = privateChatService.findByIdInOrderByTimeAsc(messageIds);
        builder.addAllPrivateChat(privateChatList.stream().map(PrivateChat::toProto).collect(Collectors.toList()));
        builder.setSessionId(sessionId);

        privateChatService.deleteAll(privateChatList);

        return buildReply(userId, builder, SUCCESS);

    }

    @Override
    public void Close(String s) {

    }
}
