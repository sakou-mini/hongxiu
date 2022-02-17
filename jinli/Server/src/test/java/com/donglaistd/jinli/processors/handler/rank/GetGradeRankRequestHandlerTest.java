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

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetGradeRankRequestHandlerTest extends BaseTest {
    @Autowired
    private GetGradeRankRequestHandler handler;

    @Test
    public void testEmptyGradeRank() {
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveUserRankRequest.Builder req = Jinli.GetLiveUserRankRequest.newBuilder();
        Jinli.JinliMessageReply reply = handler.handle(context, builder.setGetLiveUserRankRequest(req.build()).build() );
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        Assert.assertEquals(0, reply.getGetGradeRankReply().getGradeList().size());
    }

    @Test
    public void testNotEmptyGradeRank() {
        Jinli.Grade test1 = Jinli.Grade.newBuilder().setUserId("1").setLevel(5).setDisplayName("test1").setAvatarUrl("///1").setArrow(Constant.Arrow.UP).build();
        Jinli.Grade test3 = Jinli.Grade.newBuilder().setUserId("3").setLevel(3).setDisplayName("test3").setAvatarUrl("///3").setArrow(Constant.Arrow.UP).build();
        Jinli.Grade test2 = Jinli.Grade.newBuilder().setUserId("2").setLevel(2).setDisplayName("test2").setAvatarUrl("///2").setArrow(Constant.Arrow.UP).build();
        DataManager.gradeRank.addAll(Arrays.asList(test1, test3, test2));
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveUserRankRequest.Builder req = Jinli.GetLiveUserRankRequest.newBuilder();
        Jinli.JinliMessageReply reply = handler.handle(context, builder.setGetLiveUserRankRequest(req.build()).build() );
        List<Jinli.Grade> gradeList = reply.getGetGradeRankReply().getGradeList();
        Assert.assertEquals(3, gradeList.size());
    }
}
