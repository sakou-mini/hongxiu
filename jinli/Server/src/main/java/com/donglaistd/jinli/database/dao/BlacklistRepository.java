package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.Blacklist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistRepository extends MongoRepository<Blacklist,String> {
    Blacklist findByRoomId(String roomId);
}
