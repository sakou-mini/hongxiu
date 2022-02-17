package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.ServerRunningRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerRunningRecordRepository extends MongoRepository<ServerRunningRecord,Object> {
}
