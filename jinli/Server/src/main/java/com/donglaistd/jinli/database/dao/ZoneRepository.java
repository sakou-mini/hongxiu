package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.Zone;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ZoneRepository extends MongoRepository<Zone,String> {
    Zone findByUserId(String userId);
}
