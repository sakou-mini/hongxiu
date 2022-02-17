package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreAuth;
import com.donglaistd.jinli.database.entity.User;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@IgnoreAuth
@Component
public class HeartConnectedRequestHandler extends MessageHandler{
    private static final Logger logger = Logger.getLogger(HeartConnectedRequestHandler.class.getName());

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        if(user!=null) logger.info("heart connected by -------->" + user.getId());
        return null;
    }
}
