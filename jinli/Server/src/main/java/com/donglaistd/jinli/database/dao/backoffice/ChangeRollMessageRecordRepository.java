package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.ChangeRollMessageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeRollMessageRecordRepository extends MongoRepository<ChangeRollMessageRecord, String> {
    Page<ChangeRollMessageRecord> findAllByPlatformOrderByRecordTimeDesc(Constant.PlatformType platformType, Pageable pageable);
}
