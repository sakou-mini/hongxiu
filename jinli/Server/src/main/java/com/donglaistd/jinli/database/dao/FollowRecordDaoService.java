package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.FollowRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowRecordDaoService {
    @Autowired
    FollowRecordRepository followRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public long countFollowNumByTimeBetween(long startTime , long endTime, Constant.PlatformType platform){
        Criteria criteria = Criteria.where("recordTime").gte(startTime).lte(endTime).and("platform").is(platform);
        Query query = Query.query(criteria);
        List<FollowRecord> followerId = mongoTemplate.findDistinct(query, "followerId", FollowRecord.class, FollowRecord.class);
        return followerId.size();
    }

    public FollowRecord save(FollowRecord record) {
        return followRecordRepository.save(record);
    }
}
