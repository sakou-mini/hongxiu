package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.BullBull;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BullBullRepository extends MongoRepository<BullBull,String> {
    BullBull findByGameId(ObjectId gameId);
}
