package com.donglai.model.db.repository.statistics;

import com.donglai.model.db.entity.statistics.DailyOfServerData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface DailyOfServerDataRepository extends MongoRepository<DailyOfServerData, Long> {
    @Query(value = "{'recordTime':{$gte : ?0, $lte : ?1}}")
    List<DailyOfServerData> findByRecordTimeBetween(long startTime, long endTime);

    DailyOfServerData findByRecordTime(long time);
}
