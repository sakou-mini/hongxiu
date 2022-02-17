package com.donglai.model.db.repository.live;


import com.donglai.model.db.entity.live.LiveUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveUserRepository extends MongoRepository<LiveUser, String> {
    LiveUser findByUserId(String userId);
}

