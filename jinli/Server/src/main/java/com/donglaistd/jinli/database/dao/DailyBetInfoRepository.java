package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DailyBetInfoRepository extends MongoRepository<DailyBetInfo, String> {
    List<DailyBetInfo> findByLiveUserId(String id);

    List<DailyBetInfo> findByBetUserIdIsAndTimeBetween(String betUserId, long startTime, long endTime);
    List<DailyBetInfo> findByBetUserId(String betUserId);

    List<DailyBetInfo> findByBetUserIdInAndTimeBetween(Collection<String> betUserId, long startTime, long endTime);
}
