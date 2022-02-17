package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.CommonStatisticInfo;
import com.donglaistd.jinli.constant.StatisticEnum;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommonStatisticInfoRepository extends MongoRepository<CommonStatisticInfo, ObjectId> {
    CommonStatisticInfo findByStatisticTimeAndStatisticType(long time, StatisticEnum statisticType);
}
