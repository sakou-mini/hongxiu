package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.CoinFlow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CoinFlowRepository extends MongoRepository<CoinFlow, String> {
    CoinFlow findByUserId(String userId);

    CoinFlow findById(ObjectId id);
}
