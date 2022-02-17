package com.donglaistd.jinli.database.dao.system.domainConfig;

import com.donglaistd.jinli.database.entity.system.domainConfig.DomainViewRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DomainViewRecordRepository extends MongoRepository<DomainViewRecord, ObjectId> {
    long countByDomainAndTimeBetween(String domain, long startTime, long endTime);
}
