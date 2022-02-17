package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.database.entity.CoinFlow;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CoinFlowDaoServiceTest extends BaseTest {

    @Autowired
    CoinFlowDaoService coinFlowDaoService;

    @Test
    public void addFlowTest(){
        for (int i = 0; i < 30 ; i++) {
            new Thread(() -> {
                coinFlowDaoService.addUserCoinFlow("12344", 50, 100, 10,0);
            }).start();
        }
        try {
            Thread.sleep(5100);
            CoinFlow coinFlow = coinFlowDaoService.findByUserId("12344");
            Assert.assertEquals(1500, coinFlow.getFlow());
            Assert.assertEquals(3000, coinFlow.getServiceFlow());
            Assert.assertEquals(300, coinFlow.getGiftIncome());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
