package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.GiftLogDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.processors.handler.gift.QueryGiftIncomeDetailRequestHandler;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class QueryGiftIncomeDetailRequestHandlerTest extends BaseTest {
    @Autowired
    QueryGiftIncomeDetailRequestHandler queryGiftIncomeDetailRequestHandler;
    @Autowired
    GiftLogDaoService giftLogDaoService;

    @Test
    public void test(){
        Jinli.JinliMessageRequest request = Jinli.JinliMessageRequest.newBuilder().setQueryGiftIncomeDetailRequest(Jinli.QueryGiftIncomeDetailRequest.newBuilder()).build();
        User sender = userBuilder.createUser("张三", "张三", "", "", true);
        GiftLog giftLog1 = GiftLog.newInstance(sender.getId(), user.getId(), 1000, "10008", 1);
        GiftLog giftLog2 = GiftLog.newInstance(sender.getId(), user.getId(), 1000, "10009", 1);
        GiftLog giftLog3 = GiftLog.newInstance(sender.getId(), user.getId(), 1000, "10008", 1);
        GiftLog giftLog4 = GiftLog.newInstance(sender.getId(), user.getId(), 1000, "10009", 1);
        User sender2 = userBuilder.createUser("里斯2", "里斯", "", "", true);
        GiftLog giftLog5 = GiftLog.newInstance(sender2.getId(), user.getId(), 3000, "10008", 3);
        GiftLog giftLog6 = GiftLog.newInstance(sender2.getId(), user.getId(), 6000, "10009", 2);
        giftLogDaoService.saveAll(Lists.newArrayList(giftLog1, giftLog2, giftLog3, giftLog4,giftLog5,giftLog6));
        Jinli.JinliMessageReply handle = queryGiftIncomeDetailRequestHandler.handle(context, request);
        Jinli.QueryGiftIncomeDetailReply reply = handle.getQueryGiftIncomeDetailReply();
        Assert.assertEquals(2,reply.getGiftIncomeDetailsCount());
        Assert.assertEquals(sender2.getId(),reply.getGiftIncomeDetails(0).getUseId());
        Assert.assertEquals(2,reply.getGiftIncomeDetails(0).getGiftRecordsCount());

        Assert.assertEquals(sender.getId(),reply.getGiftIncomeDetails(1).getUseId());
        Assert.assertEquals(2,reply.getGiftIncomeDetails(1).getGiftRecordsCount());

    }

}
