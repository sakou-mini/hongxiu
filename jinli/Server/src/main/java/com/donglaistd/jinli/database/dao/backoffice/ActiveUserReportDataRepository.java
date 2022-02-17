package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.ActiveUserReportData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActiveUserReportDataRepository extends MongoRepository<ActiveUserReportData, ObjectId> {
    ActiveUserReportData findById(String id);
}
