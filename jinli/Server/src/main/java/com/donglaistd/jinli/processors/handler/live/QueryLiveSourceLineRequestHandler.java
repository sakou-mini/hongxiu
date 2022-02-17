package com.donglaistd.jinli.processors.handler.live;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.statistic.LiveSourceLineConfigDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryLiveSourceLineRequestHandler extends MessageHandler {
    @Autowired
    LiveSourceLineConfigDaoService liveSourceLineConfigDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        LiveSourceLineConfig liveSourceLineConfig = liveSourceLineConfigDaoService.findLiveSourceLineConfigByPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        return buildReply( Jinli.QueryLiveSourceLineReply.newBuilder().addAllLiveSourceLines(liveSourceLineConfig.getAvailableLine()), Constant.ResultCode.SUCCESS);
    }
}
