package com.donglai.model.db.repository.back;

import com.donglai.model.db.entity.back.RecommendVideo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-05 16:24
 */
public interface RecommendVideoRepository extends MongoRepository<RecommendVideo, Long> {
    List<RecommendVideo> findByIdIn(List<Long> ids);

    List<RecommendVideo> deleteByIdIn(List<Long> ids);
}
