package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.CoinFlow;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CoinFlowDaoService {
    @Autowired
    CoinFlowRepository coinFlowRepository;
    @Autowired
    MongoOperations mongoOperations;
    @Autowired
    MongoTemplate mongoTemplate;

    public CoinFlow findByUserId (String userId){
        return coinFlowRepository.findByUserId(userId);
    }

    public CoinFlow findById(ObjectId id){
        return coinFlowRepository.findById(id);
    }

    public void save(CoinFlow coinFlow){
        coinFlowRepository.save(coinFlow);
    }

    public CoinFlow addUserCoinFlow(String userId, long flowNum,long serviceFee,long giftIncome,long giftCost) {
        UpdateResult updateResult = mongoOperations.updateFirst(new Query(Criteria.where("userId").is(userId)), new Update().inc("flow", flowNum)
                .inc("serviceFlow", serviceFee).inc("giftIncome",giftIncome).inc("giftCost",giftCost), CoinFlow.class);
        if(updateResult.getModifiedCount() <= 0){
            synchronized (this){
                if(Objects.isNull(findByUserId(userId))){
                    CoinFlow coinFlow = new CoinFlow(userId, System.currentTimeMillis(), flowNum, serviceFee, giftIncome, 0);
                    coinFlow.setGiftCost(giftCost);
                    save(coinFlow);
                }else{
                     mongoOperations.updateFirst(new Query(Criteria.where("userId").is(userId)), new Update().inc("flow", flowNum)
                            .inc("serviceFlow", serviceFee).inc("giftIncome",giftIncome).inc("giftCost",giftCost), CoinFlow.class);
                }
            }
        }
        return findByUserId(userId);
    }

    public CoinFlow addUserRechargeCoin(String userId,long recharge) {
        UpdateResult updateResult = mongoOperations.updateFirst(new Query(Criteria.where("userId").is(userId)), new Update().inc("recharge", recharge), CoinFlow.class);
        if(updateResult.getModifiedCount() <= 0){
            synchronized (this){
                if(Objects.isNull(findByUserId(userId))){
                    save(new CoinFlow(userId,System.currentTimeMillis(),0,0,0,recharge));
                }else{
                    mongoOperations.updateFirst(new Query(Criteria.where("userId").is(userId)), new Update().inc("recharge", recharge), CoinFlow.class);
                }
            }
        }
        return findByUserId(userId);
    }

    public List<CoinFlow> getGiftIncomeRank(int size){
        Query query = new Query(Criteria.where("giftIncome").gt(0));
        return mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "giftIncome"))
                .with(PageRequest.of(0, size)), CoinFlow.class);
    }

    public List<CoinFlow> getCoinFlowByUserIds(List<String> userId){
        Criteria criteria = Criteria.where("userId").in(userId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, CoinFlow.class);
    }

    public List<CoinFlow> getCoinFlowByUserIds(List<String> userId,int size){
        Criteria criteria = Criteria.where("userId").in(userId);
        Query query = new Query(criteria);
        return mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "giftIncome")).with(PageRequest.of(0, size)), CoinFlow.class);
    }

    public List<CoinFlow> findAll(){
        return coinFlowRepository.findAll();
    }
}
