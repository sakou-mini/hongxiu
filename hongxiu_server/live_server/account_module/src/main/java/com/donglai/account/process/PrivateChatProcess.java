package com.donglai.account.process;

import com.donglai.account.message.producer.Producer;
import com.donglai.common.constant.UserPermissionSettingType;
import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.entity.account.PrivateChatSession_Message;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.account.PrivateChatService;
import com.donglai.model.db.service.account.PrivateChatSessionMessageService;
import com.donglai.model.db.service.account.PrivateChatSessionService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.service.UserPermissionService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglai.protocol.Constant.ResultCode.*;

@Component
public class PrivateChatProcess {
    @Autowired
    PrivateChatService privateChatService;
    @Autowired
    PrivateChatSessionService privateChatSessionService;
    @Autowired
    PrivateChatSessionMessageService privateChatSessionMessageService;
    @Autowired
    Producer producer;
    @Autowired
    UserService userService;
    @Autowired
    UserPermissionService userPermissionService;


    public Constant.ResultCode verifyPrivateChat(String sender, User receiver, String message) {
        if (Objects.isNull(receiver)) {
            return USER_NOT_FOUND;
        } else if (Objects.equals(sender, receiver.getId())) {
            return CAN_NOT_CHAT_YOURSELF;
        } else if (!userPermissionService.permissionAllow(UserPermissionSettingType.PER_PRIVATE_CHAT, receiver.getId(), sender))
            return NO_PERMISSION;
        return SUCCESS;
    }

    public PrivateChat processRecordPrivateChat(String fromUserId, String toUserId, String content) {

        PrivateChat privateChat = PrivateChat.newInstance(content, fromUserId, toUserId, System.currentTimeMillis());
        User byId = userService.findById(toUserId);
        if (!byId.isOnline()) {
            //入库消息
            privateChat = privateChatService.save(privateChat);
        }
        //双方都添加会话 //有则不添加  无则添加
        long sendSessionId = recordOrUpdateChatSessionIfAbsent(fromUserId, toUserId, content);
        long toSessionId = recordOrUpdateChatSessionIfAbsent(toUserId, fromUserId, content);
        long messageId = privateChat.getId();
        //添加中间表
        privateChatSessionMessageService.save(PrivateChatSession_Message.newInstance(messageId, sendSessionId));

        privateChatSessionMessageService.save(PrivateChatSession_Message.newInstance(messageId, toSessionId));
        if (byId.isOnline()) {
            broadCastPrivateChatToReceiver(toUserId, privateChat, toSessionId);
        }
        return privateChat;
    }

    private long recordOrUpdateChatSessionIfAbsent(String fromUserId, String toUserId, String content) {
        PrivateChatSession session = privateChatSessionService.findByFromUidAndToUid(fromUserId, toUserId);
        //如果为空则添加会话
        if (Objects.isNull(session)) {
            session = PrivateChatSession.newInstance(content, System.currentTimeMillis(), fromUserId, toUserId, false);
        } else {
            session.setLastTime(System.currentTimeMillis());
            session.setLastMessage(content);
            session.setDel(false);
        }
        session = privateChatSessionService.save(session);
        return session.getId();
    }

    private void broadCastPrivateChatToReceiver(String userId, PrivateChat privateChat, long sessionId) {
        Account.AccountOfPrivateChatBroadCastMessage message = Account.AccountOfPrivateChatBroadCastMessage.newBuilder()
                .setPrivateChat(privateChat.toProto()).setSessionId(String.valueOf(sessionId)).build();
        producer.sendReply(userId, message, SUCCESS);
    }
}
