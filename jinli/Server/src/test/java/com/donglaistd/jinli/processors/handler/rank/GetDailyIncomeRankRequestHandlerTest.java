package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetDailyIncomeRankRequestHandlerTest extends BaseTest {
    @Autowired
    private GetDailyIncomeRankRequestHandler handler;

    @Test
    public void testEmptyDailyIncomeRank() {
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveUserRankRequest.Builder req = Jinli.GetLiveUserRankRequest.newBuilder();
        Jinli.JinliMessageReply reply = handler.handle(context, builder.setGetLiveUserRankRequest(req.build()).build() );
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        Assert.assertEquals(0, reply.getGetDailyIncomeRankReply().getDailyIncomeList().size());
    }

    @Test
    public void testNotEmptyDaiyIncomeRank() {
        Jinli.DailyIncome test2 = Jinli.DailyIncome.newBuilder().setUserId("2").setAmount(200).setDisplayName("test2").setArrow(Constant.Arrow.UP).setAvatarUrl("///2").build();
        Jinli.DailyIncome test1 = Jinli.DailyIncome.newBuilder().setUserId("1").setAmount(100).setDisplayName("test1").setArrow(Constant.Arrow.DOWNWARD).setAvatarUrl("///1").build();
        Jinli.DailyIncome test3 = Jinli.DailyIncome.newBuilder().setUserId("3").setAmount(50).setDisplayName("test3").setArrow(Constant.Arrow.UP).setAvatarUrl("///3").build();
        DataManager.incomeRank.addAll(Arrays.asList(test2, test1, test3));
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveUserRankRequest.Builder req = Jinli.GetLiveUserRankRequest.newBuilder();
        Jinli.JinliMessageReply reply = handler.handle(context, builder.setGetLiveUserRankRequest(req.build()).build() );
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        List<Jinli.DailyIncome> list = reply.getGetDailyIncomeRankReply().getDailyIncomeList();
        Assert.assertEquals(3, list.size());
        List<Constant.Arrow> arrows = list.stream().map(Jinli.DailyIncome::getArrow).collect(Collectors.toList());

    }
}
