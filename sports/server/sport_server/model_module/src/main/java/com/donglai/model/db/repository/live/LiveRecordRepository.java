package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.LiveRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LiveRecordRepository extends MongoRepository<LiveRecord, Long> {

    LiveRecord findByLiveUserIdAndEventId(String liveUserId, String eventId);

    void deleteByLiveUserIdAndEventId(String liveUserId, String eventId);

    void deleteAllByEventId(String eventId);

    List<LiveRecord> findAllByEventId(String eventId);
}
