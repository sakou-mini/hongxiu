package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.UserBetRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserBetRecordDaoService {
    @Autowired
    UserBetRecordRepository userBetRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public UserBetRecord findByUserId(String userId){
        return userBetRecordRepository.findByUserId(userId);
    }

    public UserBetRecord save(UserBetRecord userBetRecord){
        return userBetRecordRepository.save(userBetRecord);
    }

    public List<UserBetRecord> findBetRecordsByIds(Set<String> ids) {
        return userBetRecordRepository.findAllByUserIdIn(ids);
    }

}
