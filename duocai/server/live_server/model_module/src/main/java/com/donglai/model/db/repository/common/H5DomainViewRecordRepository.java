package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.H5DomainViewRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface H5DomainViewRecordRepository extends MongoRepository<H5DomainViewRecord,Long> {
    long countByDomainAndTimeBetween(String domainName, Long startTime, long endTime);
}
