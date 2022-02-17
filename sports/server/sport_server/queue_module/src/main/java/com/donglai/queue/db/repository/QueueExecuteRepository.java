package com.donglai.queue.db.repository;

import com.donglai.model.db.entity.common.QueueExecute;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QueueExecuteRepository extends MongoRepository<QueueExecute, ObjectId> {
}
