package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.WithdrawalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WithdrawalRecordRepository extends MongoRepository<WithdrawalRecord,String> {
}
