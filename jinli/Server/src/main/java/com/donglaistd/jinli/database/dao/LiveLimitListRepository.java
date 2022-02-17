package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveLimitList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LiveLimitListRepository extends MongoRepository<LiveLimitList,String> {
    LiveLimitList findByPlatformAndLimitDateIs(Constant.PlatformType platform, long date);

    void deleteByPlatformAndLimitDateIs(Constant.PlatformType platform, long date);

    List<LiveLimitList> findAllByPlatformAndLimitDateGreaterThanEqualOrderByLimitDateAsc(Constant.PlatformType platform, long limitDate);
}
