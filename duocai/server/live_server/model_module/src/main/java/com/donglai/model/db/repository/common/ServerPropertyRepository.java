package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.ServerProperty;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ServerPropertyRepository extends MongoRepository<ServerProperty,Long> {
}
