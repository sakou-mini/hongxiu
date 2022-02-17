package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.UserAttribute;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAttributeRepository extends MongoRepository<UserAttribute,String> {
    UserAttribute findByUserId(String userId);
}
