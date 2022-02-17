package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveRecordRepository extends MongoRepository<LiveRecord, String> {
    long countByLiveUserId(String liveUserId);

    void deleteByPlatform(Constant.PlatformType platformType);
}
