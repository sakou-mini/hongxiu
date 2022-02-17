package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.UserAgent;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserAgentDaoService {
    @Autowired
    UserAgentRepository userAgentRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    MongoTemplate mongoTemplate;

    public UserAgent save(UserAgent userAgent){
        return userAgentRepository.save(userAgent);
    }

    public UpdateResult addUserAgentCoin(String userId, BigDecimal coin){
        return mongoOperations.updateMulti(new Query(Criteria.where("userId").is(userId)), new Update().inc("totalIncome",coin.doubleValue()).inc("leftIncome",coin.doubleValue()), UserAgent.class);
    }

    public UpdateResult decUserAgentCoin(String userId,double coin){
        return mongoOperations.updateMulti(new Query(Criteria.where("userId").is(userId)), new Update().inc("leftIncome",-coin), UserAgent.class);
    }

    public UserAgent findByUserId(String userId){
        return userAgentRepository.findByUserId(userId);
    }

    public UserAgent findOrCreateUserAgent(String userId){
        UserAgent userAgent = userAgentRepository.findByUserId(userId);
        if(userAgent == null){
            userAgent = userAgentRepository.save(UserAgent.newInstance(userId));
        }
        return userAgent;
    }

}
