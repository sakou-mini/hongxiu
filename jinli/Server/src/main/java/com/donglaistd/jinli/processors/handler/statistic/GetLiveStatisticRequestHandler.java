package com.donglaistd.jinli.processors.handler.statistic;


import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.StatisticManager;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.NOT_LIVE_USER;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class GetLiveStatisticRequestHandler extends MessageHandler {
    @Autowired
    private StatisticManager statisticManager;

    @Autowired
    DataManager dataManager;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GetLiveStatisticReply.Builder reply = Jinli.GetLiveStatisticReply.newBuilder();
        if (Objects.isNull(user.getLiveUserId())) return buildReply(reply, NOT_LIVE_USER);
        Jinli.GetLiveStatisticReply buildReply = statisticManager.buildReply(user.getLiveUserId());
        return buildReply(buildReply, SUCCESS);
    }
}
