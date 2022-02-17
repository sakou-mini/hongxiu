package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.ChangePasswordRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangePasswordRecordRepository extends MongoRepository<ChangePasswordRecord,String> {
}
