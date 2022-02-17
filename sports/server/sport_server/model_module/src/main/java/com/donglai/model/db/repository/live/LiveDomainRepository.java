package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.LiveDomain;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveDomainRepository extends MongoRepository<LiveDomain,String> {
    LiveDomain findByLineCode(int lineCode);
}
