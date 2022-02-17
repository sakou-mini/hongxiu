package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.NewUserReportData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewUserReportDataRepository extends MongoRepository<NewUserReportData, ObjectId> {
    NewUserReportData findById(String id);
}
