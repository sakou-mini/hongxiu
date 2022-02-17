package com.donglai.model.db.repository.back;

import com.donglai.model.db.entity.back.RecommendUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-06 11:02
 */
public interface RecommendUserRepository extends MongoRepository<RecommendUser, Long> {
    List<RecommendUser> findByIdIn(List<Long> ids);

    List<RecommendUser> deleteByIdIn(List<Long> ids);
}
