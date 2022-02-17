package com.donglai.model.db.service.sport;

import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.sport.SportLiveSchedule;
import com.donglai.model.db.repository.sport.SportLiveScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SportLiveScheduleService {
    @Autowired
    SportLiveScheduleRepository repository;
    @Autowired
    MongoTemplate mongoTemplate;

    public SportLiveSchedule save(SportLiveSchedule sportLiveSchedule){
        return repository.save(sportLiveSchedule);
    }

    public void del(SportLiveSchedule sportLiveSchedule){
        repository.delete(sportLiveSchedule);
    }

    public void delByEventId(String eventId){
        repository.deleteByEventId(eventId);
    }

    public List<SportLiveSchedule> findByEventId(String eventId) {
        return repository.findByEventIdOrderByLiveBeginTimeDesc(eventId);
    }

    public SportLiveSchedule findByUserIdAndEventId(String userId, String eventId){
        return repository.findByUserIdAndEventId(userId, eventId);
    }

    public List<SportLiveSchedule> findByUserId(String userId){
        return repository.findByUserId(userId);
    }

    public List<SportLiveSchedule> findByLiveUserId(String liveUser){
        return repository.findByLiveUserId(liveUser);
    }

    public List<SportLiveSchedule> findAllSportLiveAfterToday() {
        //查询今日后所有的赛事安排
        long today = TimeUtil.getCurrentDayStartTime();
        return repository.findByLiveBeginTimeGreaterThanEqual(today);
    }
}
