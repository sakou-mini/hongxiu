package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.BackofficeOperandEnum;
import com.donglaistd.jinli.database.dao.backoffice.BackofficeUserOperationRecordDaoService;
import com.donglaistd.jinli.database.entity.LiveWatchRecord;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.UserAttribute;
import com.donglaistd.jinli.database.entity.backoffice.BackofficeUserOperationRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserAttributeDaoService {
    @Autowired
    UserAttributeRepository userAttributeRepository;
    @Autowired
    BackofficeUserOperationRecordDaoService backofficeUserOperationRecordDaoService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Autowired
    UserDaoService userDaoService;

    public UserAttribute save(UserAttribute userAttribute){
        return userAttributeRepository.save(userAttribute);
    }

    public UserAttribute findByUserId(String userId){
        return userAttributeRepository.findByUserId(userId);
    }

    public UserAttribute findByUserIdOrSaveIfNotExit(String userId){
        UserAttribute userAttribute = userAttributeRepository.findByUserId(userId);
        if(userAttribute == null)
            userAttribute = userAttributeRepository.save( UserAttribute.newInstance(userId, Constant.AccountStatue.ACCOUNT_NORMAL));
        return userAttribute;
    }

    @Transactional
    public boolean updateUserAccountStatue(List<String> userIds,String backOfficeName,Constant.AccountStatue statue){
        List<BackofficeUserOperationRecord> backofficeUserOperationRecords = new ArrayList<>(userIds.size());
        List<UserAttribute> userAttributes = new ArrayList<>(userIds.size());
        BackofficeOperandEnum operandEnum = statue.equals(Constant.AccountStatue.ACCOUNT_BAN) ? BackofficeOperandEnum.BAN_USER : BackofficeOperandEnum.UNSEALED_USER;
        long time = System.currentTimeMillis();
        for (String userId : userIds) {
            UserAttribute userattribute = findByUserIdOrSaveIfNotExit(userId);
            userattribute.setStatue(statue);
            userattribute.setTime(time);
            backofficeUserOperationRecords.add(BackofficeUserOperationRecord.newInstance(backOfficeName, operandEnum, time, userId));
            userAttributes.add(userattribute);
        }
        userAttributeRepository.saveAll(userAttributes);
        backofficeUserOperationRecordDaoService.saveAll(backofficeUserOperationRecords);
        return true;
    }

    public void updateAllUserWatchLiveTimeInfo(){
        List<UserAttribute> userAttributes = new ArrayList<>();
        List<LiveWatchRecord> liveWatchRecords = liveWatchRecordDaoService.groupUserWatchRecord();
        for (LiveWatchRecord liveWatchRecord : liveWatchRecords) {
            UserAttribute userAttribute = findByUserIdOrSaveIfNotExit(liveWatchRecord.getUserId());
            userAttribute.setWatchLiveCount(liveWatchRecord.getConnectedLiveCount());
            userAttribute.setWatchLiveTime(liveWatchRecord.getWatchTime());
            userAttributes.add(userAttribute);
        }
        userAttributeRepository.saveAll(userAttributes);
        for (User user : userDaoService.findAll()) {
            findByUserIdOrSaveIfNotExit(user.getId());
        }
    }

    public void updateUserWatchLiveInfo(String userId, long watchLiveTime){
        Query query = new Query(Criteria.where("userId").is(userId));
        Update update = new Update();
        update.inc("watchLiveTime", watchLiveTime).inc("watchLiveCount",1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        mongoTemplate.findAndModify(query, update, options, UserAttribute.class);
    }
}
