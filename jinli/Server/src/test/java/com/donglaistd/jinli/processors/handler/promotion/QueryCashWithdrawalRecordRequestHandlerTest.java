package com.donglaistd.jinli.processors.handler.promotion;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.invite.UserAgentDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.invite.UserAgent;
import com.donglaistd.jinli.service.EventPublisher;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class QueryCashWithdrawalRecordRequestHandlerTest extends InviteTest{
    @Autowired
    QueryCashWithdrawalRecordRequestHandler queryCashWithdrawalRecordRequestHandler;
    @Autowired
    UserAgentDaoService userAgentDaoService;
    @Autowired
    WithdrawalCoinRequestHandler withdrawalCoinRequestHandler;
    @Autowired
    UserDaoService userDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    public User initUserAgent(String name,double amount,long gameCoin){
        User tester1 = createUser(100, name);
        UserAgent userAgent = userAgentDaoService.findOrCreateUserAgent(tester1.getId());
        userAgent.setLeftIncome(amount);
        userAgent.setTotalIncome(amount);
        userAgentDaoService.save(userAgent);
        return tester1;
    }



    @Test
    public void withdrawalCoinAndQueryCashWithdrawalRecordRequestHandlerTest(){
        User tester1 = initUserAgent("tester1",5000.5,100);
        //1.WithdrawalCoin Request
        Jinli.JinliMessageRequest request = Jinli.JinliMessageRequest.newBuilder().setWithdrawalCoinRequest(Jinli.WithdrawalCoinRequest.newBuilder().setCoin(10)).build();
        Jinli.JinliMessageReply reply = withdrawalCoinRequestHandler.doHandle(context, request, tester1);
        Assert.assertEquals(Constant.ResultCode.SUCCESS,reply.getResultCode());
        tester1 = userDaoService.findById(tester1.getId());
        Assert.assertEquals(5100,tester1.getGameCoin());


        request = Jinli.JinliMessageRequest.newBuilder().setWithdrawalCoinRequest(Jinli.WithdrawalCoinRequest.newBuilder().setCoin(5000)).build();
        reply = withdrawalCoinRequestHandler.doHandle(context, request, tester1);
        Assert.assertEquals(Constant.ResultCode.NOT_ENOUGH_GAMECOIN,reply.getResultCode());

        request = Jinli.JinliMessageRequest.newBuilder().setWithdrawalCoinRequest(Jinli.WithdrawalCoinRequest.newBuilder().setCoin(20)).build();

        for (int i = 0; i < 10; i++) {
            reply = withdrawalCoinRequestHandler.doHandle(context, request, tester1);
            Assert.assertEquals(Constant.ResultCode.NOT_ENOUGH_GAMECOIN,reply.getResultCode());
        }

        request = Jinli.JinliMessageRequest.newBuilder().setQueryCashWithdrawalRecordRequest(Jinli.QueryCashWithdrawalRecordRequest.newBuilder()).build();
        reply = queryCashWithdrawalRecordRequestHandler.doHandle(context, request, tester1);
        List<Jinli.WithdrawalRecord> records = reply.getQueryCashWithdrawalRecordReply().getWithdrawalRecordsList();
        Assert.assertEquals(1,records.size());
    }
}
