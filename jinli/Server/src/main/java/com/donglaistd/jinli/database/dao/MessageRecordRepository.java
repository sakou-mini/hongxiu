package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRecordRepository extends MongoRepository<MessageRecord,String> {
    MessageRecord findById(ObjectId messageId);

    void deleteById(ObjectId messageId);

    List<MessageRecord> findAllById(List<String> ids);

    List<MessageRecord> findByToUidIsAndRead(String userId, boolean isRead);
}
