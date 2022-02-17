package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.LiveDomainConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveDomainConfigRepository extends MongoRepository<LiveDomainConfig,String> {
    LiveDomainConfig findByLine(Constant.LiveSourceLine line);
}
