package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.DailyLiveRecordInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DailyLiveRecordInfoRepository extends MongoRepository<DailyLiveRecordInfo,String> {

}
