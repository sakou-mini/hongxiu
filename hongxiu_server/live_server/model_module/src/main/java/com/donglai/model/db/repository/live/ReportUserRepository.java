package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.ReportUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 17:13
 */
public interface ReportUserRepository extends MongoRepository<ReportUser, Long> {
    List<ReportUser> findByIdIn(List<Long> ids);
}
