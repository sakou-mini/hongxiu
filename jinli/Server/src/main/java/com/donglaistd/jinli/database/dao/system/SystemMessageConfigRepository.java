package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemMessageConfigRepository extends MongoRepository<SystemMessageConfig,String> {

}
