package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.repository.live.LiveRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class LiveRecordService {
    @Autowired
    LiveRecordRepository repository;
    @Autowired
    MongoTemplate mongoTemplate;

    public LiveRecord save(LiveRecord record){
        return repository.save(record);
    }

    public LiveRecord findByLiveUserIdAndEvent(String liveUserId,String eventId) {
        return repository.findByLiveUserIdAndEventId(liveUserId, eventId);
    }

    public void deleteByLiveUserIdAndEvent(String liveUserId,String eventId){
        repository.deleteByLiveUserIdAndEventId(liveUserId,eventId);
    }

    public void delete(LiveRecord record){
        repository.delete(record);
    }

    public List<LiveRecord> findByUserIdAndTimes(String userId, Long startTime, Long endTime){
        Criteria criteria = Criteria.where("userId").is(userId);
        List<Criteria> timeQuery = new ArrayList<>();
        if(Objects.nonNull(startTime)){
            timeQuery.add(Criteria.where("startTime").gte(startTime));
        }
        if(Objects.nonNull(endTime)){
            timeQuery.add(Criteria.where("startTime").lte(endTime));
        }
        if(!CollectionUtils.isEmpty(timeQuery)) criteria.andOperator(timeQuery.toArray(new Criteria[0]));
        return mongoTemplate.find(Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"startTime")),LiveRecord.class);
    }

    public void deleteByEventId(String eventId){
        repository.deleteAllByEventId(eventId);
    }

    public List<LiveRecord> findByEventId(String eventId){
        return repository.findAllByEventId(eventId);
    }
}
