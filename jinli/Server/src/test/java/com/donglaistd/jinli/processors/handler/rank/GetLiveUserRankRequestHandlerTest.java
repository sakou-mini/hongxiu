package com.donglaistd.jinli.processors.handler.rank;


import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.util.DataManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GetLiveUserRankRequestHandlerTest extends BaseTest {
    @Autowired
    private GetLiveUserRankRequestHandler handler;
    @Autowired
    private UserDaoService userDaoService;
    @Test
    public void testEmpty() {
        userDaoService.save(user);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveUserRankRequest.Builder build = Jinli.GetLiveUserRankRequest.newBuilder();
        Jinli.JinliMessageReply reply = handler.handle(context, builder.setGetLiveUserRankRequest(build.build()).build() );
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());
        Assert.assertEquals(0, reply.getGetLiveUserRankReply().getLiveUserRankInfoList().size());
    }

    @Test
    public void testHandle() {
        userDaoService.save(user);
        Jinli.LiveUserRankInfo test1 = Jinli.LiveUserRankInfo.newBuilder().setHotValue(100).setStatus(Constant.LiveStatus.ONLINE).setAvatarUrl("///test1").setUserId("1").setArrowValue(0).setDisplayName("test1").build();
        Jinli.LiveUserRankInfo test2 = Jinli.LiveUserRankInfo.newBuilder().setHotValue(50).setStatus(Constant.LiveStatus.ONLINE).setAvatarUrl("///test2").setUserId("1").setArrowValue(0).setDisplayName("test2").build();
        Jinli.LiveUserRankInfo test3 = Jinli.LiveUserRankInfo.newBuilder().setHotValue(30).setStatus(Constant.LiveStatus.ONLINE).setAvatarUrl("///test3").setUserId("1").setArrowValue(0).setDisplayName("test3").build();
        DataManager.liveRank.addAll(Arrays.asList(test1, test2, test3));
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.GetLiveUserRankRequest.Builder build = Jinli.GetLiveUserRankRequest.newBuilder();
        Jinli.JinliMessageReply reply = handler.handle(context, builder.setGetLiveUserRankRequest(build.build()).build() );
        Assert.assertEquals(3, reply.getGetLiveUserRankReply().getLiveUserRankInfoList().size());
    }


}
