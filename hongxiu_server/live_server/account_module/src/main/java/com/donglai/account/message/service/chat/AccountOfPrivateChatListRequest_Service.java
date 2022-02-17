package com.donglai.account.message.service.chat;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.entity.account.PrivateChatSession_Message;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.account.PrivateChatService;
import com.donglai.model.db.service.account.PrivateChatSessionMessageService;
import com.donglai.model.db.service.account.PrivateChatSessionService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Common;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

/**
 * @author Moon
 * @date 2021-11-01 15:56
 */
@Service("AccountOfPrivateChatListRequest")
public class AccountOfPrivateChatListRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private PrivateChatService privateChatService;
    @Autowired
    private PrivateChatSessionService privateChatSessionService;
    @Autowired
    private PrivateChatSessionMessageService privateChatSessionMessageService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserProcess userProcess;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var replyBuilder = Account.AccountOfPrivateChatListReply.newBuilder();
        Account.AccountOfPrivateChatListRequest request = message.getAccountOfPrivateChatListRequest();
        String name = request.getName() == null ? "" : request.getName().replaceAll(" ", "");
        List<String> userIds = userService.findByNicknameLike(name).stream().map(User::getId).collect(Collectors.toList());

        List<PrivateChatSession> sessions = privateChatSessionService.findByFromUidAndDelIsAndToUidInOrderByLastTimeDesc(userId, false, userIds);
        //初始化我的 私聊列表
        List<Account.PrivateChatSession> privateChatMessageList = new ArrayList<>();
        //遍历组装
        for (PrivateChatSession session : sessions) {
            //添加UserInfo 和未读数
            Common.UserInfo userInfo = userProcess.buildUerDetailInfo(userService.findById(session.getToUid()));
            List<PrivateChatSession_Message> bySessionId = privateChatSessionMessageService.findBySessionId(session.getId());
            List<Long> messageIds = bySessionId.stream().map(PrivateChatSession_Message::getMessageId).collect(Collectors.toList());
            List<PrivateChat> unread = privateChatService.getUnreadBySessionIdAndToUid(userId, messageIds);
            privateChatMessageList.add(session.toProto(unread.size(), userInfo));
        }
        replyBuilder.addAllPrivateChatSession(privateChatMessageList).setName(name);
        return buildReply(userId, replyBuilder, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
