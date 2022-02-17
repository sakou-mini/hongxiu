package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MessageRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryUnReadMessageRequestHandler extends MessageHandler {
    @Autowired
    MessageRecordDaoService messageRecordDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryUnReadMessageReply.Builder builder = Jinli.QueryUnReadMessageReply.newBuilder();
        List<MessageRecord> unReadMessage = messageRecordDaoService.findAllUnReadMessage(user.getId());
        unReadMessage.sort(Comparator.comparing(MessageRecord::getSendTime));
        for (MessageRecord messageRecord : unReadMessage) {
            messageRecord.setRead(true);
            builder.addMessageRecord(messageRecord.toProto());
        }
        messageRecordDaoService.saveAll(unReadMessage);
        return buildReply(builder, Constant.ResultCode.SUCCESS);
    }
}
