package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.ReportComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 17:14
 */
public interface ReportCommentRepository extends MongoRepository<ReportComment, Long> {
    List<ReportComment> findByIdIn(List<Long> ids);
}
