package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.Keyword;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-28 13:48
 */
public interface KeywordRepository extends MongoRepository<Keyword, Long> {
    List<Keyword> findByIdIn(List<Long> ids);

    List<Keyword> deleteByIdIn(List<Long> ids);
}
