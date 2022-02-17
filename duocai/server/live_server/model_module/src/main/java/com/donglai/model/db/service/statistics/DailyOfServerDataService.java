package com.donglai.model.db.service.statistics;

import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.model.db.repository.statistics.DailyOfServerDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyOfServerDataService {
    @Autowired
    private DailyOfServerDataRepository repository;

    public DailyOfServerData save(DailyOfServerData dailyOfServerData) {
        return repository.save(dailyOfServerData);
    }

    public List<DailyOfServerData> findByTimeBetween(long startTime, long endTime) {
        return repository.findByRecordTimeBetween(startTime, endTime);
    }

    public DailyOfServerData findByTime(long time) {
        return repository.findByRecordTime(time);
    }


}
