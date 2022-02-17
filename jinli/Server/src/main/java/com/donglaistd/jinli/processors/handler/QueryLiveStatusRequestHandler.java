package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_LIVE_USER;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryLiveStatusRequestHandler extends MessageHandler {
    @Autowired
    LiveUserDaoService liveUserDaoService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryLiveStatusReply.Builder reply = Jinli.QueryLiveStatusReply.newBuilder();
        LiveUser liveUser = liveUserDaoService.findByUserId(user.getId());
        if (Objects.isNull(liveUser)) {
            return buildReply(reply, NOT_LIVE_USER);
        }
        Constant.LiveStatus status = liveUser.getLiveStatus();
        reply.setLiveStatus(status);
        return buildReply(reply, SUCCESS);
    }
}
