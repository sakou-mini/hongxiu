package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DailyBetInfoDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.processors.handler.statistic.QueryFlowRecordRequestHandler;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryFlowRecordRequestHandlerTest extends BaseTest {
    @Autowired
    private QueryFlowRecordRequestHandler handler;
    @Autowired
    private DailyBetInfoDaoService service;
    @Autowired
    private UserDaoService userDaoService;

    @Test
    public void testEmpty() {
        userDaoService.save(user);
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.QueryFlowRecordRequest build = Jinli.QueryFlowRecordRequest.newBuilder().setType(Constant.QueryTimeType.ALL).build();
        request.setQueryFlowRecordRequest(build);
        Jinli.JinliMessageReply reply = handler.handle(context, request.build() );
        Assert.assertEquals(reply.getQueryFlowRecordReply().getBetSummaryList().size(), 0);
    }

    @Test
    public void testQueryTimeTypeIsToday() {
        userDaoService.save(user);
        long todayStartTime = TimeUtil.getCurrentDayStartTime();
        DailyBetInfo test1_info = new DailyBetInfo("8848", user.getId(), todayStartTime + 300000, 1000, 0, "8848", Constant.GameType.BACCARAT);
        DailyBetInfo test2_info = new DailyBetInfo("8848", user.getId(), todayStartTime + 600000, 500, 200, "8848", Constant.GameType.NIUNIU);
        List<DailyBetInfo> list = Arrays.asList(test1_info, test2_info);
        list = service.saveAll(list);
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.QueryFlowRecordRequest build = Jinli.QueryFlowRecordRequest.newBuilder().setType(Constant.QueryTimeType.TODAY).build();
        request.setQueryFlowRecordRequest(build);
        Jinli.JinliMessageReply reply = handler.handle(context, request.build() );
        Assert.assertEquals(2, reply.getQueryFlowRecordReply().getBetSummaryCount());
        service.deleteAll(list);
    }

    @Test
    public void testQueryTimeTypeIsMonth() {
        long firstDayOfCurrentMonth = TimeUtil.getFirstDayOfCurrentMonth();
        userDaoService.save(user);
        DailyBetInfo test1_info = new DailyBetInfo("8848", user.getId(), firstDayOfCurrentMonth + 86400000, 999, 0, "8848", Constant.GameType.BACCARAT);
        DailyBetInfo test2_info = new DailyBetInfo("8848", user.getId(), firstDayOfCurrentMonth + 172800000, 888, 456, "8848", Constant.GameType.LONGHU);
        DailyBetInfo test3_info = new DailyBetInfo("8848", user.getId(), firstDayOfCurrentMonth + 259200000, 500, 200, "8848", Constant.GameType.REDBLACK);
        List<DailyBetInfo> list = Arrays.asList(test1_info, test2_info, test3_info);
        list = service.saveAll(list);
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder();
        Jinli.QueryFlowRecordRequest build = Jinli.QueryFlowRecordRequest.newBuilder().setType(Constant.QueryTimeType.TODAY).build();
        request.setQueryFlowRecordRequest(build);
        Jinli.JinliMessageReply reply = handler.handle(context, request.build() );
        Assert.assertEquals(0, reply.getQueryFlowRecordReply().getBetSummaryCount());


        request.setQueryFlowRecordRequest(build.toBuilder().setType(Constant.QueryTimeType.MONTH).build());
        reply = handler.handle(context, request.build() );
//        Assert.assertEquals(3, reply.getQueryFlowRecordReply().getBetSummaryCount());

        service.deleteAll(list);
    }
}
