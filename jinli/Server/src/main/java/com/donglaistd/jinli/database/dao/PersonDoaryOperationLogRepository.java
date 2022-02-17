package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.PersonDiaryOperationlog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PersonDoaryOperationLogRepository extends MongoRepository<PersonDiaryOperationlog,String> {
    PersonDiaryOperationlog findById(ObjectId id);

    List<PersonDiaryOperationlog> findByIsApproval(boolean isApproval);

    PersonDiaryOperationlog findByPersonDiaryIdAndIsApproval(String id, boolean isApproval);

    PersonDiaryOperationlog findByPersonDiaryId(String id);
}
