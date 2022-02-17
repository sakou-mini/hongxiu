package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.DailyDownloadRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyDownloadRecordRepository extends MongoRepository<DailyDownloadRecord, ObjectId> {
    DailyDownloadRecord findByTime(long time);
}
