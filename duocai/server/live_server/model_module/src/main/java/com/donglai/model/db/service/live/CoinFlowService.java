package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.CoinFlow;
import com.donglai.model.db.repository.live.CoinFlowRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CoinFlowService {
    @Autowired
    CoinFlowRepository coinFlowRepository;
    @Autowired
    MongoOperations mongoOperations;

    public CoinFlow findByUserId (String userId){
        return coinFlowRepository.findById(userId).orElse(null);
    }

    public void addUserCoinFlow(String userId, long coinFlow,long giftIncome,long giftCost) {
        UpdateResult result = mongoOperations.upsert(new Query(Criteria.where("id").is(userId)),
                new Update().inc("coinFlow", coinFlow).inc("giftIncome", giftIncome).inc("giftCost",giftCost).set("_id",userId), CoinFlow.class);
    }

    public void addGiftFlow(String userId, long giftCoin,boolean isCost){
        if(isCost){
            addUserCoinFlow(userId, 0, 0, giftCoin);
        }else{
            addUserCoinFlow(userId, 0, giftCoin,0 );
        }
    }

    public List<CoinFlow> findAll(){
        return coinFlowRepository.findAll();
    }


}
