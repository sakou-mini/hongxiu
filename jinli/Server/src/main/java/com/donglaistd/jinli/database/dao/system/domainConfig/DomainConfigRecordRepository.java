package com.donglaistd.jinli.database.dao.system.domainConfig;

import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfigRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DomainConfigRecordRepository extends MongoRepository<DomainConfigRecord, ObjectId> {
}
