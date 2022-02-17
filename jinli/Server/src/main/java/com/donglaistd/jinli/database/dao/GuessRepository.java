package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.Guess;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuessRepository extends MongoRepository<Guess, Long> {
    Guess findById(long id);

}
