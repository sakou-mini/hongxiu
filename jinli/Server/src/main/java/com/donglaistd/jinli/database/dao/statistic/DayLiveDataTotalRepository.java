package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.DayLiveDataTotal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DayLiveDataTotalRepository extends MongoRepository<DayLiveDataTotal,String> {
    DayLiveDataTotal findByPlatformAndRecordTime(Constant.PlatformType platform, long time);
}
