package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.LiveLimitRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveLimitRecordRepository extends MongoRepository<LiveLimitRecord,String> {

}
