package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.UserBetRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface UserBetRecordRepository extends MongoRepository<UserBetRecord, String> {

    UserBetRecord findByUserId(String userId);

    List<UserBetRecord> findAllByUserIdIn(Collection<String> ids);
}
