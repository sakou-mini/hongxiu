package com.donglaistd.jinli.database.dao.statistic;

import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveSourceLineConfigRepository extends MongoRepository<LiveSourceLineConfig,String> {

}
