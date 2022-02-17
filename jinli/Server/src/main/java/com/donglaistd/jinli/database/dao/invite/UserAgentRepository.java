package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.UserAgent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAgentRepository extends MongoRepository<UserAgent, String> {
    UserAgent findByUserId(String userId);
}
