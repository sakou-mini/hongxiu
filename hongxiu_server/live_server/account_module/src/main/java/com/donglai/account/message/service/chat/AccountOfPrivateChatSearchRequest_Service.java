package com.donglai.account.message.service.chat;

import com.donglai.account.process.UserProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.entity.account.PrivateChatSession_Message;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.repository.account.PrivateChatSessionMessageRepository;
import com.donglai.model.db.service.account.PrivateChatService;
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
 * @date 2021-12-16 17:28
 */
@Service("AccountOfPrivateChatSearchRequest")
public class AccountOfPrivateChatSearchRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private PrivateChatSessionMessageRepository privateChatSessionMessageService;
    @Autowired
    private UserService userService;
    @Autowired
    private PrivateChatSessionService privateChatSessionService;
    @Autowired
    private UserProcess userProcess;
    @Autowired
    private PrivateChatService privateChatService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfPrivateChatSearchRequest request = message.getAccountOfPrivateChatSearchRequest();

        Account.AccountOfPrivateChatSearchReply.Builder builder = Account.AccountOfPrivateChatSearchReply.newBuilder();

        List<Account.PrivateChatSession> privateChatMessageList = new ArrayList<>();
        String keyword = request.getKeyword();
        List<String> userIds = userService.findByNicknameLike(keyword).stream().map(User::getAccountId).collect(Collectors.toList());
        List<PrivateChatSession> chatSessions = privateChatSessionService.findByFromUidAndToUidInAndDelIsOrderByLastTimeDesc(userId, userIds, false);
        for (PrivateChatSession session : chatSessions) {
            //添加UserInfo 和未读数
            String toUid = session.getToUid();
            Common.UserInfo userInfo = userProcess.buildUerDetailInfo(userService.findById(toUid));

            List<PrivateChatSession_Message> bySessionId = privateChatSessionMessageService.findBySessionId(session.getId());
            List<Long> messageIds = bySessionId.stream().map(PrivateChatSession_Message::getMessageId).collect(Collectors.toList());
            List<PrivateChat> unread = privateChatService.getUnreadBySessionIdAndToUid(userId, messageIds);
            privateChatMessageList.add(session.toProto(unread.size(), userInfo));


        }
        builder.addAllPrivateChatSession(privateChatMessageList);
        return buildReply(userId, builder, SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
