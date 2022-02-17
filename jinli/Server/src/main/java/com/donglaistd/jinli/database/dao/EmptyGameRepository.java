package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.game.EmptyGame;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmptyGameRepository extends MongoRepository<EmptyGame,String> {
    EmptyGame findByGameId(String gameId);
}