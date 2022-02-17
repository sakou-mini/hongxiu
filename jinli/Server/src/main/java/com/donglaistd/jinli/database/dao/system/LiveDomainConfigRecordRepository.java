package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.database.entity.system.LiveDomainConfigRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveDomainConfigRecordRepository extends MongoRepository<LiveDomainConfigRecord,String> {
}
