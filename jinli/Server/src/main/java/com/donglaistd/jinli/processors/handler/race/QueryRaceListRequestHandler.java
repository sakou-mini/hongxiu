package com.donglaistd.jinli.processors.handler.race;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.RaceBuilder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryRaceListRequestHandler extends MessageHandler {

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryRaceListRequest request = messageRequest.getQueryRaceListRequest();
        Constant.RaceType raceType = request.getRaceType();
        int showNum = request.getShowNum();
        Jinli.QueryRaceListReply.Builder reply = Jinli.QueryRaceListReply.newBuilder();
        reply = RaceBuilder.getRacesByType(reply, raceType, showNum);
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
