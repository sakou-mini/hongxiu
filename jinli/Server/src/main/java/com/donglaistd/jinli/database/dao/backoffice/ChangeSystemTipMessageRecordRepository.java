package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.ChangeSystemTipMessageRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeSystemTipMessageRecordRepository extends MongoRepository<ChangeSystemTipMessageRecord,String> {

    Page<ChangeSystemTipMessageRecord> findAllByPlatformIsOrderByRecordTimeDesc(Constant.PlatformType platformType, Pageable pageable);
}
