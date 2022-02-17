package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.DayGroupContributionRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QueryDayGroupContributionRequestHandlerTest extends InviteTest{

    @Autowired
    QueryDayGroupContributionRequestHandler queryDayGroupContributionRequestHandler;
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
        gameFinishListener.updateUserBetRecord(tester3.getId(),600);
        gameFinishListener.updateUserBetRecord(tester4.getId(),700);
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester1.getId(), System.currentTimeMillis(), 500));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester3.getId(), System.currentTimeMillis(), 600));
        dailyBetInfoDaoService.save(new DailyBetInfo(liveUser.getId(), tester4.getId(), System.currentTimeMillis(), 700));
    }

    @Test
    public void queryDayGroupContributionTest(){
        bindInviteUserAndBet();
        Jinli.JinliMessageRequest request = Jinli.JinliMessageRequest.newBuilder()
                .setQueryDayGroupContributionRequest(Jinli.QueryDayGroupContributionRequest.newBuilder()).build();
        User tester2 = userDaoService.findById("tester2");
        userAgentProcessService.totalDayUserAgentIncome(tester2.getId(), TimeUtil.getCurrentDayStartTime(), System.currentTimeMillis());
        Jinli.QueryDayGroupContributionReply reply = queryDayGroupContributionRequestHandler.doHandle(context, request, tester2).getQueryDayGroupContributionReply();
        List<Jinli.DayAgentIncomeDetail> detailsList = reply.getDayIncomeDetailsList();
        Assert.assertEquals(1, detailsList.size());
        Jinli.DayAgentIncomeDetail dayAgentIncomeDetail = detailsList.get(0);
        Assert.assertEquals(1100,dayAgentIncomeDetail.getFirstAgentFlow());
        Assert.assertEquals(700,dayAgentIncomeDetail.getSecondAgentFlow());
        Assert.assertEquals(4.7,dayAgentIncomeDetail.getCommission(),0);
    }

}
