package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
@Component
public class BecomeLiveUserCheckRequestHandler extends MessageHandler {
    @Autowired
    private VerifyUtil verifyUtil;
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.BecomeLiveUserCheckReply.Builder reply = Jinli.BecomeLiveUserCheckReply.newBuilder();
        Constant.ResultCode resultCode = verifyUtil.checkUserHasBindPhoneAndModifyName(user);
        return buildReply(reply,resultCode);
    }
}
