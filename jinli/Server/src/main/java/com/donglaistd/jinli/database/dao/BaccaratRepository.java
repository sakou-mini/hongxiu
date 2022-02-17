package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.Baccarat;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaccaratRepository extends MongoRepository<Baccarat, String> {
    Baccarat getByGameId(ObjectId id);
}
