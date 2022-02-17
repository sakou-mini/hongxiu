package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.BackofficeUserOperationRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BackofficeUserOperationRecordRepository extends MongoRepository<BackofficeUserOperationRecord, ObjectId> {

}
