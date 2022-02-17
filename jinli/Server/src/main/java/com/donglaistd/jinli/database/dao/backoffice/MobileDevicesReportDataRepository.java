package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.MobileDevicesReportData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MobileDevicesReportDataRepository extends MongoRepository<MobileDevicesReportData, ObjectId> {
    MobileDevicesReportData findById(String id);
}
