package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.database.dao.CoinFlowDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.CoinFlow;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.GiftRankService;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoinFlowService {
    @Autowired
    CoinFlowDaoService coinFlowDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    GiftRankService giftRankService;

    public  void addGiftCoinFlow(String userId, long giftCoin,boolean isCost) {
        if(isCost){
            CoinFlow coinFlow = coinFlowDaoService.addUserCoinFlow(userId, giftCoin, 0, 0, giftCoin);
            User user = userDaoService.findById(userId);
            giftRankService.updateUserContributeRank(user.getPlatformType(), userId, coinFlow.getGiftCost());
        }else{
            coinFlowDaoService.addUserCoinFlow(userId, giftCoin,0,giftCoin,0);
        }
    }

    public  void setUserCoinFlow(String userId, long flowNum) {
        coinFlowDaoService.addUserCoinFlow(userId, flowNum,0,0,0);
    }

    public synchronized void setUserCoinFlow(String userId, long flowNum,long serviceFee) {
        coinFlowDaoService.addUserCoinFlow(userId, flowNum,serviceFee,0,0);
    }

    public CoinFlow setUserRecharge(String userId, long rechargeNum) {
       return coinFlowDaoService.addUserRechargeCoin(userId, rechargeNum);
    }
}
