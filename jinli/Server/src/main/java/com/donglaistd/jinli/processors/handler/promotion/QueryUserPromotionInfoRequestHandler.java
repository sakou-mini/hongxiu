package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.UserAgentProcessService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryUserPromotionInfoRequestHandler extends MessageHandler {
    @Autowired
    UserAgentProcessService userAgentProcessService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryUserPromotionInfoReply.Builder reply = Jinli.QueryUserPromotionInfoReply.newBuilder();
        Jinli.UserPromotionInfo promotionInfo = userAgentProcessService.getUserPromotionInfo(user.getId());
        reply.setUserPromotionInfo(promotionInfo);
        return buildReply(reply.build(),SUCCESS);
    }
}
