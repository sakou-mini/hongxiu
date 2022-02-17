package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.DiaryResource;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DiaryResourceRepository extends MongoRepository<DiaryResource, ObjectId> {

    List<DiaryResource> findAllByDiaryId(String diaryId);

    long countByDiaryId(String diaryId);

    void deleteByDiaryId(String diaryId);
}
