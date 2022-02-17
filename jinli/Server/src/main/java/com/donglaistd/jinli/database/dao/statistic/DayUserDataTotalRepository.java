package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.statistic.DayUserDataTotal;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DayUserDataTotalRepository extends MongoRepository<DayUserDataTotal,String> {

}
