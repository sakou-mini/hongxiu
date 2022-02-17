package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class ModifyBindPhoneNumberRequestHandler extends MessageHandler{
    @Autowired
    UserDaoService userDaoService;
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.ModifyBindPhoneNumberRequest request = messageRequest.getModifyBindPhoneNumberRequest();
        Jinli.ModifyBindPhoneNumberReply.Builder reply = Jinli.ModifyBindPhoneNumberReply.newBuilder();
        String phoneNumber = request.getPhoneNumber();
        if(StringUtils.isNullOrBlank(phoneNumber))
            return buildReply(reply, ERROR_PHONE_NUMBER);
        if(userDaoService.existByPhoneNumber(phoneNumber))
            return buildReply(reply, PHONE_NUMBER_EXIT);
        user.setPhoneNumber(phoneNumber);
        dataManager.saveUser(user);
        EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.bindPhone,1));
        return buildReply(reply, SUCCESS);
    }
}
