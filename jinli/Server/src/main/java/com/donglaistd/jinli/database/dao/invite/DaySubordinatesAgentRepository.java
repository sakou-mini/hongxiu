package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.DaySubordinatesAgent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface DaySubordinatesAgentRepository extends MongoRepository<DaySubordinatesAgent, String> {
    List<DaySubordinatesAgent> findAllByUserIdIn(Collection<String> ids);
}
