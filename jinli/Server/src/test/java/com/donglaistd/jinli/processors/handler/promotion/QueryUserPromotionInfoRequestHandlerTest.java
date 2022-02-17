package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import com.donglaistd.jinli.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QueryUserPromotionInfoRequestHandlerTest extends InviteTest {
    @Autowired
    QueryUserPromotionInfoRequestHandler queryUserPromotionInfoRequestHandler;
    @Autowired
    UserDaoService userDaoService;

    public void bindInviteUser(){
        User tester1 = createUser(60000, "tester1");
        User tester2 = createUser(60000, "tester2");
        User tester3 = createUser(60000, "tester3");
        User tester4 = createUser(60000, "tester4");
        userAgentProcessService.bindUserAgent(tester1,tester2.getId());
        userAgentProcessService.bindUserAgent(tester3,tester2.getId());
        userAgentProcessService.bindUserAgent(tester4,tester3.getId());
        // tester2  first:tester1、 tester3  second：tester4
    }

    @Test
    public void testQueryUserPromotionInfoRequest(){
        bindInviteUser();
        Jinli.JinliMessageRequest request = Jinli.JinliMessageRequest.newBuilder().setQueryUserPromotionInfoRequest(Jinli.QueryUserPromotionInfoRequest.newBuilder()).build();
        User tester2 = userDaoService.findById("tester2");
        Jinli.JinliMessageReply reply = queryUserPromotionInfoRequestHandler.doHandle(context, request, tester2);
        Jinli.UserPromotionInfo promotionInfo = reply.getQueryUserPromotionInfoReply().getUserPromotionInfo();
        Assert.assertEquals(20, promotionInfo.getCoin(),0);
        Assert.assertEquals(20, promotionInfo.getTotalIncome(),0);
        Assert.assertFalse(promotionInfo.getCanBeInvite());
        Assert.assertFalse(StringUtils.isNullOrBlank(promotionInfo.getShareUrl()));
        List<UserInviteRecord> record = userInviteRecordDaoService.findUserInviteAndBeInviteRecordForToday(tester2.getId());
        Assert.assertEquals(2, record.size());
    }
}
