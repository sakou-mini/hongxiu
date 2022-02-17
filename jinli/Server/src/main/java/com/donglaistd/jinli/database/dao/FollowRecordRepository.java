package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.FollowRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FollowRecordRepository extends MongoRepository<FollowRecord,String> {
}
