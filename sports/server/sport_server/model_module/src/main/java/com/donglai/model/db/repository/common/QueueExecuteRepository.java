package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.QueueExecute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface QueueExecuteRepository extends MongoRepository<QueueExecute, String> {
    QueueExecute findByRefIdAndTriggerType(String refId, int queueType);

    QueueExecute deleteByRefIdAndTriggerType(String refId, int queueType);
}
