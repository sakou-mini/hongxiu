package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.MessageContact;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageContactRepository extends MongoRepository<MessageContact, ObjectId> {

    MessageContact findBySenderIdAndReceiverId(String senderId, String receiverId);
}
