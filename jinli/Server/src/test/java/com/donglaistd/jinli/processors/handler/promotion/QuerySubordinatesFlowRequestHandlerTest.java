package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class QuerySubordinatesFlowRequestHandlerTest extends InviteTest{
    @Autowired
    QuerySubordinatesFlowRequestHandler querySubordinatesFlowRequestHandler;
    @Autowired
    UserDaoService userDaoService;

    public void bindInviteUserAndBet(){
        User tester1 = createUser(60000, "tester1");
        User tester2 = createUser(60000, "tester2");
        User tester3 = createUser(60000, "tester3");
        User tester4 = createUser(60000, "tester4");
        userAgentProcessService.bindUserAgent(tester1,tester2.getId());
        userAgentProcessService.bindUserAgent(tester3,tester2.getId());
        userAgentProcessService.bindUserAgent(tester4,tester3.getId());
        // tester2  first:tester1、 tester3  second：tester4
        gameFinishListener.updateUserBetRecord(tester1.getId(),500);
        gameFinishListener.updateUserBetRecord(tester2.getId(),1000);
        gameFinishListener.updateUserBetRecord(tester3.getId(),600);
        gameFinishListener.updateUserBetRecord(tester4.getId(),700);
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester1.getId(), System.currentTimeMillis(), 500));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester2.getId(), System.currentTimeMillis(), 1000));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester3.getId(), System.currentTimeMillis(), 600));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester4.getId(), System.currentTimeMillis(), 700));
    }

    @Test
    public void querySubordinatesFlowRequestHandlerTest(){
        bindInviteUserAndBet();

        userAgentProcessService.totalDayUserAgentRecord(TimeUtil.getCurrentDayStartTime(),System.currentTimeMillis());

        Jinli.JinliMessageRequest request = Jinli.JinliMessageRequest.newBuilder()
                .setQuerySubordinatesFlowRequest(Jinli.QuerySubordinatesFlowRequest.newBuilder()).build();
        User tester1 = userDaoService.findById("tester1");
        User tester2 = userDaoService.findById("tester2");
        User tester3 = userDaoService.findById("tester3");
        User tester4 = userDaoService.findById("tester4");
        List<Jinli.SubordinatesInfo> subordinatesInfosList = querySubordinatesFlowRequestHandler.doHandle(context, request, tester3).getQuerySubordinatesFlowReply().getSubordinatesInfosList();
        Assert.assertEquals(1, subordinatesInfosList.size());
        Jinli.SubordinatesInfo subordinatesInfo = subordinatesInfosList.get(0);
        Assert.assertEquals(tester4.getId(),subordinatesInfo.getUserId());
        Assert.assertEquals(tester4.getDisplayName(),subordinatesInfo.getUserName());
        Assert.assertEquals(700,subordinatesInfo.getDayFlow());
        Assert.assertEquals(700,subordinatesInfo.getTotalFlow());
        Assert.assertEquals(0,subordinatesInfo.getTeamFlow());


        subordinatesInfosList = querySubordinatesFlowRequestHandler.doHandle(context, request, tester2).getQuerySubordinatesFlowReply().getSubordinatesInfosList();
        Assert.assertEquals(2, subordinatesInfosList.size());

        List<String> firstAgentUserIds = subordinatesInfosList.stream().map(Jinli.SubordinatesInfo::getUserId).collect(Collectors.toList());
        Assert.assertTrue(firstAgentUserIds.containsAll(Lists.newArrayList(tester1.getId(),tester3.getId())));
    }

}
