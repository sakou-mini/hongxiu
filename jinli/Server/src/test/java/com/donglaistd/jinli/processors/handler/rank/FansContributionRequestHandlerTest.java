package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FansContributionRequestHandlerTest extends BaseTest {
    @Autowired
    private FansContributionRequestHandler fansContributionRequestHandler;
    @Autowired
    private GiftLogDaoService giftLogDaoService;

    @Test
    public void testQueryIsEmpty() {
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.FansContributionRequest.Builder request = Jinli.FansContributionRequest.newBuilder();
        request.setType(Constant.QueryTimeType.TODAY);
        Jinli.JinliMessageReply handle = fansContributionRequestHandler.handle(context, builder.setFansContributionRequest(request.build()).build());
        Jinli.FansContributionReply contributionReply = handle.getFansContributionReply();
        Assert.assertTrue(contributionReply.getFansContributionList().isEmpty());
    }

    @Test
    public void testQuery() {
        user.setDisplayName("user");
        user.setToken("user");
        user.setAccountName("account_user");
        long now = System.currentTimeMillis();
        User sendUser1 = userBuilder.createUser("account_sendUser1", "sendUser1", "sendUser1_url", "sendUser1", true);

        User sendUser2 = userBuilder.createUser("account_sendUser2", "sendUser2", "sendUser2_url", "sendUser2", true);

        User sendUser3 = userBuilder.createUser("account_sendUser3", "sendUser3", "sendUser3_url", "sendUser3", true);

        GiftLog log1 = giftLogDaoService.save(GiftLog.newInstance(sendUser1.getId(),user.getId() , 80,"10008",2));
        log1.setCreateTime(now - 1000 * 60);
        GiftLog log2 = giftLogDaoService.save(GiftLog.newInstance(sendUser2.getId(),user.getId() , 20,"10008",2));
        log2.setCreateTime(now - 1000 * 60*2);
        GiftLog log3 = giftLogDaoService.save(GiftLog.newInstance(sendUser3.getId(),user.getId() , 50,"10008",2));
        log3.setCreateTime(now - 1000 * 60*3);
        giftLogDaoService.saveAll(Arrays.asList(log1, log2, log3));
        dataManager.saveUser(user);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.FansContributionRequest.Builder request = Jinli.FansContributionRequest.newBuilder();
        request.setType(Constant.QueryTimeType.TODAY);
        Jinli.JinliMessageReply handle = fansContributionRequestHandler.handle(context, builder.setFansContributionRequest(request.build()).build());
        Jinli.FansContributionReply contributionReply = handle.getFansContributionReply();
        Assert.assertFalse(contributionReply.getFansContributionList().isEmpty());
        Assert.assertEquals(13,contributionReply.getFansContributionList().size());
    }
}
