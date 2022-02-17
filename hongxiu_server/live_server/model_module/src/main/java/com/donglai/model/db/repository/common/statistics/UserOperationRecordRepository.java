package com.donglai.model.db.repository.common.statistics;

import com.donglai.model.db.entity.common.statistics.UserOperationRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface UserOperationRecordRepository extends MongoRepository<UserOperationRecord, String> {
    List<UserOperationRecord> findByUserIdInOrderByLastPublishBlogsTimeDesc(Collection<String> userId);
}
