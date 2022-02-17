package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.Longhu;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LonghuRepository extends MongoRepository<Longhu, String> {
    Longhu getByGameId(ObjectId id);
}
