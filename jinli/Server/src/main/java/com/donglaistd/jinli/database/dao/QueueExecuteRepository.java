package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.QueueExecute;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QueueExecuteRepository extends MongoRepository<QueueExecute, ObjectId> {
    void deleteByRefIdAndTriggerType(String refId, int queueType);
}