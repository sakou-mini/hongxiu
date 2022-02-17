package com.donglai.model.db.repository.sport;

import com.donglai.model.db.entity.sport.SportEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SportEventRepository extends MongoRepository<SportEvent,String> {
}
