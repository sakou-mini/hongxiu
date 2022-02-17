package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.FeedBack;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-28 17:52
 */
public interface FeedBackRepository extends MongoRepository<FeedBack, Long> {
    List<FeedBack> deleteByIdIn(List<Long> ids);
}
