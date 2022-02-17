package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.LiveRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveRecordRepository extends MongoRepository<LiveRecord, Long> {

}
