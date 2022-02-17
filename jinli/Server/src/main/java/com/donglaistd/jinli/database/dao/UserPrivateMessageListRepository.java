package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.UserPrivateMessageList;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPrivateMessageListRepository extends MongoRepository<UserPrivateMessageList,String> {
    UserPrivateMessageList findById(ObjectId id);
}
