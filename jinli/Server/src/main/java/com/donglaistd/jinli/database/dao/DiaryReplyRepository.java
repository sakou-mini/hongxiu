package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.DiaryReply;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DiaryReplyRepository extends MongoRepository<DiaryReply, ObjectId> {
    DiaryReply findById(String id);

    void deleteAllByDiaryId(String diaryId);

    List<DiaryReply> findByDiaryIdAndParentReplyIdIsNull(String diaryId);
}
