package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyUserActiveRecordRepository extends MongoRepository<DailyUserActiveRecord,Long> {
    DailyUserActiveRecord findByDailyTimeAndPlatform(long time, Constant.PlatformType platform);

}
