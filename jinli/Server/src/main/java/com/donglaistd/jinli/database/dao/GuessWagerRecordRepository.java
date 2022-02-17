package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.GuessWagerRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GuessWagerRecordRepository extends MongoRepository<GuessWagerRecord, ObjectId> {
    GuessWagerRecord findById(String id);
    List<GuessWagerRecord> findByGuessId(String guessId);
    List<GuessWagerRecord> findByUserId(String userId);
    long countByGuessId(String guessId);
}
