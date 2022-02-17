package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.account.AwardLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2022-02-14 18:06
 */
public interface AwardLogRepository extends MongoRepository<AwardLog, String> {
}
