package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.invite.DayGroupContributionRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.DayGroupContributionRecord;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.UserAgentProcessService;
import com.donglaistd.jinli.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryDayGroupContributionRequestHandler extends MessageHandler {
    @Value("${inviteIncome.record.max.day}")
    private int maxDay;

    @Autowired
    DayGroupContributionRecordDaoService dayGroupContributionRecordDaoService;
    @Autowired
    UserAgentProcessService userAgentProcessService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        long startTime = TimeUtil.getBeforeDayStartTime(maxDay);
        long endTime = System.currentTimeMillis();
        Jinli.QueryDayGroupContributionReply.Builder reply = Jinli.QueryDayGroupContributionReply.newBuilder();

        List<DayGroupContributionRecord> records = dayGroupContributionRecordDaoService.findDayAgentRecordByTimeAndUserId(user.getId(), startTime, endTime);
        records.forEach(record->{
            reply.addDayIncomeDetails(record.toProto());
        });
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
