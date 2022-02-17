package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.RecommendDiary;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecommendDiaryRepository extends MongoRepository<RecommendDiary,String> {
    int deleteAllByEndTimeLessThanEqual(long time);
}
