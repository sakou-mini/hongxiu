package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface OfficialLiveRecordRepository extends MongoRepository<OfficialLiveRecord, ObjectId>{
    OfficialLiveRecord findById(String id);

    List<OfficialLiveRecord> findByOpenIs(boolean statue);

    List<OfficialLiveRecord> findByLiveUserId(String liveUserId);

    List<OfficialLiveRecord> findByLiveUserIdAndOpen(String liveUserId, boolean open);
}
