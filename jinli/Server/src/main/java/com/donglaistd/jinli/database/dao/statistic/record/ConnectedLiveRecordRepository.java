package com.donglaistd.jinli.database.dao.statistic.record;

import com.donglaistd.jinli.database.entity.statistic.record.ConnectedLiveRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConnectedLiveRecordRepository extends MongoRepository<ConnectedLiveRecord, ObjectId> {
    long countByUserId(String userId);
}
