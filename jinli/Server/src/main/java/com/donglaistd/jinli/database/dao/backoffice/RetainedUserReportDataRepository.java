package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.RetainedUserReportData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RetainedUserReportDataRepository extends MongoRepository<RetainedUserReportData, ObjectId> {
    RetainedUserReportData findById(String id0);

    RetainedUserReportData findByDate(long data);
}
