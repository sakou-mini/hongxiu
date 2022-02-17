package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.RedBlack;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedBlackRepository extends MongoRepository<RedBlack, String> {
    RedBlack findByGameId(String id);
}
