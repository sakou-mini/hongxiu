package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.GoldenFlower;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GoldenFlowerRepository extends MongoRepository<GoldenFlower,String> {
    GoldenFlower findByGameId(ObjectId gameId);
}
