package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.CoinFlow;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CoinFlowRepository extends MongoRepository<CoinFlow, String> {
}
