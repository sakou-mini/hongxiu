package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.DayGroupContributionRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DayGroupContributionRecordRepository extends MongoRepository<DayGroupContributionRecord, String> {
    List<DayGroupContributionRecord> findByUserIdAndTimeBetween(String userId, long startTime, long endTime);
}
