package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.chat.BulletChatMessageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class BulletChatMessageRecordDaoService {
    @Autowired
    BulletChatMessageRecordRepository bulletChatMessageRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public long countByTimesAndPlatform(long startTime, long endTime, Constant.PlatformType platform){
        Criteria criteria = Criteria.where("sendTime").gte(startTime).lte(endTime).and("platform").is(platform);
        return mongoTemplate.count(Query.query(criteria), BulletChatMessageRecord.class);
    }

    public BulletChatMessageRecord save(BulletChatMessageRecord bulletChatMessageRecord) {
        return bulletChatMessageRecordRepository.save(bulletChatMessageRecord);
    }

    public long countByUserId(String userId) {
        return bulletChatMessageRecordRepository.countByFromUid(userId);
    }
}
