package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.BackOfficeLog;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2021-12-30 13:48
 */
public interface BackOfficeLogRepository extends MongoRepository<BackOfficeLog, Long> {
}
