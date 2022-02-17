package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.UserCoinOperationRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserCoinOperationRecordRepository extends MongoRepository<UserCoinOperationRecord, ObjectId> {
    UserCoinOperationRecord findById(String id);
}
