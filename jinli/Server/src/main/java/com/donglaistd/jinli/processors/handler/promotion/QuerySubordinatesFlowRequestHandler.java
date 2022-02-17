package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.invite.DaySubordinatesAgentDaoService;
import com.donglaistd.jinli.database.dao.invite.UserInviteRecordDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.DaySubordinatesAgent;
import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QuerySubordinatesFlowRequestHandler extends MessageHandler {
    @Autowired
    DaySubordinatesAgentDaoService daySubordinatesAgentDaoService;
    @Autowired
    UserInviteRecordDaoService userInviteRecordDaoService;
    @Autowired
    UserDaoService userDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QuerySubordinatesFlowReply.Builder reply = Jinli.QuerySubordinatesFlowReply.newBuilder();
        Set<String> firstAgentIds = userInviteRecordDaoService.findUserInviteRecordForDirect(user.getId()).stream().map(UserInviteRecord::getBeInviteUserId).collect(Collectors.toSet());
        List<DaySubordinatesAgent> records = daySubordinatesAgentDaoService.findByUserIds(firstAgentIds);
        User agentUser;
        for (DaySubordinatesAgent record : records) {
            agentUser = userDaoService.findById(record.getUserId());
            if(agentUser == null) continue;
            reply.addSubordinatesInfos(Jinli.SubordinatesInfo.newBuilder().setUserId(agentUser.getId())
                    .setUserName(agentUser.getDisplayName()).setDayFlow(record.getUserDayBetIncome()).setTotalFlow(record.getTotalBetIncome())
                    .setTeamFlow(record.getTeamTotalBetIncome()));
        }
        return buildReply(reply,SUCCESS);
    }
}
