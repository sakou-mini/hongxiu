package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.LiveWatchRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LiveWatchRecordRepository extends MongoRepository<LiveWatchRecord,String> {
    List<LiveWatchRecord> findByUserId(String userId);

    void removeByRoomLiveUserIdIsNull();
}
