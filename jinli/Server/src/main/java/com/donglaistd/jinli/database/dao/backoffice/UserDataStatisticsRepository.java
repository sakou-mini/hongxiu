package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserDataStatisticsRepository extends MongoRepository<UserDataStatistics,String> {
    UserDataStatistics findByUserIdAndRecordTimeAndNewUser(String userId, long time,boolean newUser);

    long countByRecordTimeIsAndActiveDaysGreaterThanEqualAndUserIdIn(long time, int activeDay, List<String> userIds);

    List<UserDataStatistics> findByRecordTimeIsAndNewUserIs(long time, boolean newUser);

    void deleteByUserIdAndRecordTime(String userId,long time);
}
