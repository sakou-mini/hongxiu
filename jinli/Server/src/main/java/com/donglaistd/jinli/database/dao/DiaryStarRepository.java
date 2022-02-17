package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.DiaryStar;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DiaryStarRepository extends MongoRepository<DiaryStar, ObjectId> {

    DiaryStar findByDiaryIdAndUserId(String diaryId, String userId);

    void deleteAllByDiaryId(String diaryId);

    Integer countByDiaryId(String diaryId);
}
