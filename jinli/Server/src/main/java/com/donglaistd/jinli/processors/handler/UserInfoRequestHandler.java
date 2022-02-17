package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.UserOperationService;
import com.donglaistd.jinli.service.UserProcess;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.Constant.ResultCode.USER_NOT_FOUND;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UserInfoRequestHandler extends MessageHandler {
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    UserOperationService userOperationService;
    @Autowired
    UserProcess userProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var message = messageRequest.getUserInfoRequest();
        var userIds = message.getUserIdList();
        var reply = Jinli.UserInfoReply.newBuilder();
        User queryUser;
        for (var userId : userIds) {
            queryUser = userDaoService.findById(userId);
            if (Objects.isNull(queryUser)) return buildReply(reply, USER_NOT_FOUND);
            reply.addUserInfo(userProcess.buildUserInfo(queryUser));
        }
        return buildReply(reply, SUCCESS);
    }
}
