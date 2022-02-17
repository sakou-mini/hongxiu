package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.account.TouristLoginLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2021-12-22 14:01
 */
public interface TouristLoginLogRepository extends MongoRepository<TouristLoginLog, String> {
    Long countAllByUserId(String id);
}
