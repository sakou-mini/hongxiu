package com.donglai.account.message.service.chat;

import com.donglai.account.process.PrivateChatProcess;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.account.PrivateChat;
import com.donglai.model.db.entity.account.PrivateChatInBlack;
import com.donglai.model.db.entity.account.PrivateChatSession;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.account.PrivateChatInBlackService;
import com.donglai.model.db.service.account.PrivateChatSessionService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;
import static com.donglai.protocol.Constant.ResultCode.IN_BLACK_LIST;
import static com.donglai.protocol.Constant.ResultCode.SUCCESS;

@Slf4j
@Service("AccountOfPrivateChatRequest")
public class AccountOfPrivateChatRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    UserService userService;
    @Autowired
    PrivateChatSessionService privateChatSessionService;
    @Autowired
    PrivateChatProcess privateChatProcess;
    @Autowired
    PrivateChatInBlackService privateChatInBlackService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        var request = message.getAccountOfPrivateChatRequest();
        //判断接收方是否存 和不能发送给自己
        var replyBuilder = Account.AccountOfPrivateChatReply.newBuilder();
        String content = request.getMessage();
        User toUser = userService.findById(request.getToUid());
        Constant.ResultCode resultCode = privateChatProcess.verifyPrivateChat(userId, toUser, content);
        log.info("userId:{} to uid:{} ", userId, request.getToUid());
        if (Objects.equals(SUCCESS, resultCode)) {
            PrivateChatInBlack blackUser = privateChatInBlackService.findByUserIdAndBlackUserId(request.getToUid(), userId);
            if (Objects.nonNull(blackUser)) {
                resultCode = IN_BLACK_LIST;
            } else {
                PrivateChat privateChat = privateChatProcess.processRecordPrivateChat(userId, request.getToUid(), content);
                replyBuilder.setPrivateChat(privateChat.toProto());
            }
            log.info("userId:{} to uid:{} ", userId, request.getToUid());
            PrivateChatSession byFromUidAndToUid = privateChatSessionService.findByFromUidAndToUid(userId, request.getToUid());
            replyBuilder.setSessionId(String.valueOf(byFromUidAndToUid.getId()));
        }
        return buildReply(userId, replyBuilder, resultCode);
    }

    @Override
    public void Close(String userId) {

    }
}
