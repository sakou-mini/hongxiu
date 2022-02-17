package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.GlobalBlackList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GlobalBlackListRepository extends MongoRepository<GlobalBlackList,String> {
}
