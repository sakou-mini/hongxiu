package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.UserPrivateMessageList;
import com.donglaistd.jinli.util.DataManager;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserPrivateMessageListDaoService {
    @Autowired
    UserPrivateMessageListRepository userPrivateMessageListRepository;

    @Autowired
    MessageRecordDaoService messageRecordDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DataManager dataManager;

    public UserPrivateMessageList save(UserPrivateMessageList userPrivateMessageList) {
        return userPrivateMessageListRepository.save(userPrivateMessageList);
    }

    public UserPrivateMessageList findByUid(String uid){
        return userPrivateMessageListRepository.findById(new ObjectId(uid));
    }

    public UserPrivateMessageList findByUidOrCreate(String uid){
        UserPrivateMessageList messageList = findByUid(uid);
        if(Objects.isNull(messageList))
            messageList = UserPrivateMessageList.getInstance(uid);
        return messageList;
    }
}
