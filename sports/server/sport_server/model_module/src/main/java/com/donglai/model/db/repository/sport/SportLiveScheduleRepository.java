package com.donglai.model.db.repository.sport;

import com.donglai.model.db.entity.sport.SportLiveSchedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SportLiveScheduleRepository extends MongoRepository<SportLiveSchedule,Long> {
    List<SportLiveSchedule> findByEventIdOrderByLiveBeginTimeDesc(String eventId);

    List<SportLiveSchedule> findByLiveBeginTimeGreaterThanEqual(long liveTime);

    SportLiveSchedule findByUserIdAndEventId(String userId, String eventId);

    List<SportLiveSchedule> findByUserId(String userId);

    List<SportLiveSchedule> findByLiveUserId(String liveUserId);

    void deleteByEventId(String eventId);
}
