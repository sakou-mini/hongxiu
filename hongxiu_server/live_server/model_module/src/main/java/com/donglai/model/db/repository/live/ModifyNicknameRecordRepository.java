package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.ModifyNicknameRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModifyNicknameRecordRepository extends MongoRepository<ModifyNicknameRecord, String> {
    long countByUserIdAndRecordTimeBetween(String userId, long startTime, long endTime);
}
