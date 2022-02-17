package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.BulletChatMessageRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BulletChatMessageRecordRepository extends MongoRepository<BulletChatMessageRecord,String> {
    long countByFromUid(String userId);
}
