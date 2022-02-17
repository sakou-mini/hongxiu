package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface PersonDiaryRepository extends MongoRepository<PersonDiary, ObjectId> {
    PersonDiary findById(String id);

    List<PersonDiary> findAllByIdIn(Collection<String> ids);

    List<PersonDiary> findByUserIdAndStatueEquals(String userId, Constant.DiaryStatue statue);

    long countByUserIdAndStatueIsNot(String userId,Constant.DiaryStatue statue);

    List<PersonDiary> findAllByUserIdAndStatueNotIn(String userId, List<Constant.DiaryStatue> statue);

    List<PersonDiary> findAllByStatue(Constant.DiaryStatue statue);
}
