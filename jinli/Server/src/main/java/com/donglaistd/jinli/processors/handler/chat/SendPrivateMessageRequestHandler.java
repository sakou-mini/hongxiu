package com.donglaistd.jinli.processors.handler.chat;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.MessageContactDaoService;
import com.donglaistd.jinli.database.dao.MessageRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import com.donglaistd.jinli.util.WordFilterUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SendPrivateMessageRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(SendPrivateMessageRequestHandler.class.getName());

    @Autowired
    DataManager dataManager;
    @Autowired
    MessageRecordDaoService messageRecordDaoService;
    @Autowired
    MessageContactDaoService messageContactDaoService;
    @Autowired
    WordFilterUtil wordFilterUtil;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SendPrivateMessageRequest request = messageRequest.getSendPrivateMessageRequest();
        Jinli.SendPrivateMessageReply.Builder reply = Jinli.SendPrivateMessageReply.newBuilder();
        String receiveId = request.getReceiverId();
        User toUser = dataManager.findOnlineUser(receiveId);
        Constant.ResultCode resultCode = verifySendMessage(user, dataManager.findOnlineUser(receiveId));
        if (!Objects.equals(SUCCESS, resultCode)) return buildReply(reply, resultCode);
        String content = request.getContent();
        content = wordFilterUtil.replaceSensitiveWord(content,"*");
        MessageRecord messageRecord = MessageRecord.newInstance(content, user.getId(), receiveId);
        if(Objects.isNull(DataManager.getUserChannel(toUser.getId()))) {
            messageRecord.setRead(false);
        }
        dealSavePrivateMessageAndConcatRecord(messageRecord);
        sendPrivateMessageToReceiver(messageRecord);
        EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.privateChatLive,1));
        return buildReply(reply.setMessage(messageRecord.toProto()), SUCCESS);
    }

    public Constant.ResultCode verifySendMessage(User fromUser, User toUser) {
        if (Objects.isNull(fromUser) ) {
            logger.warning("private chat not find user");
            return USER_NOT_FOUND;
        }
        if (Objects.equals(fromUser.getId(), toUser.getId())) {
            logger.warning("can not chat to your self");
            return CAN_NOT_CHAT_YOURSELF;
        }
        LiveUser toLiveUser = dataManager.findLiveUser(toUser.getLiveUserId());
        if(toLiveUser != null && toLiveUser.containsDisablePermission(Constant.LiveUserPermission.PERMISSION_PRIVATE_CHAT)){
            return PERMISSION_DISABLED;
        }
        LiveUser liveUser = dataManager.findLiveUser(fromUser.getLiveUserId());
        if (liveUser == null || !Objects.equals(Constant.LiveStatus.ONLINE, liveUser.getLiveStatus())) {
            if (!fromUser.canSendPrivateMessage() && !checkHasCharHistory(fromUser.getId(),toUser.getId())) {
                logger.info("user not unlock privateMessage!");
                return NOT_UNLOCKED_YET;
            }
        }
        return SUCCESS;
    }

    private void sendPrivateMessageToReceiver(MessageRecord messageRecord) {
        String toUid = messageRecord.getToUid();
        Jinli.PrivateMsgBroadCasteMessage.Builder builder = Jinli.PrivateMsgBroadCasteMessage.newBuilder().setMessage(messageRecord.toProto());
        logger.info("sendPrivate Message to----->" + toUid);
        MessageUtil.sendMessage(toUid, buildReply(builder));
    }

    private void dealSavePrivateMessageAndConcatRecord(MessageRecord messageRecord) {
        String fromUid = messageRecord.getFromUid();
        String toUid = messageRecord.getToUid();
        messageRecordDaoService.saveMessage(messageRecord);
        messageContactDaoService.updateMessageContact(fromUid, toUid);
    }

    private boolean checkHasCharHistory(String fromUserId,String toUserId){
        long chatRecord = messageRecordDaoService.countChatHistoryBySenderAndReceiver(toUserId, fromUserId);
        logger.info("privateChatNum is :" + chatRecord);
        return chatRecord > 0;
    }

}
