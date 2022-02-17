package com.donglaistd.jinli.database.dao.system;

import com.donglaistd.jinli.database.entity.system.GameServerConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GameServerConfigRepository extends MongoRepository<GameServerConfig,String> {

}
