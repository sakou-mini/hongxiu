package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import com.donglaistd.jinli.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserInviteRecordDaoService {

    @Autowired
    UserInviteRecordRepository userInviteRecordRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    public UserInviteRecord save(UserInviteRecord userInviteRecord) {
        return userInviteRecordRepository.save(userInviteRecord);
    }

    public UserInviteRecord findByBeInviteUserId(String beInviteUserId){
        return userInviteRecordRepository.findByBeInviteUserId(beInviteUserId);
    }

    public List<UserInviteRecord> findAllByInviteUserId(String inviteUserId){
        return userInviteRecordRepository.findAllByInviteUserId(inviteUserId);
    }

    //查询玩家自己代理的1级代理（直接代理）
    public List<UserInviteRecord>  findUserInviteRecordForDirect(String userId){
        return findAllByInviteUserId(userId);
    }

    //查询玩家自己所有的代理的2级代理(间接代理  ，被邀请人的 被邀请人)
    public List<UserInviteRecord>  findUserInviteRecordForInDirect(String userId){
        List<UserInviteRecord> inDirectAgents = new ArrayList<>();
        List<UserInviteRecord> firstAgent = findAllByInviteUserId(userId);
        for (UserInviteRecord userInviteRecord : firstAgent) {      //1级 代理的 被邀请人 的 邀请记录
            inDirectAgents.addAll(findAllByInviteUserId(userInviteRecord.getBeInviteUserId()));
        }
        return inDirectAgents;
    }

    public boolean isBeInvite(String userId){
        return Objects.nonNull(findByBeInviteUserId(userId));
    }

    public boolean isInviteOthers(String userId){
        return !findAllByInviteUserId(userId).isEmpty();
    }

    public List<UserInviteRecord> findUserInviteAndBeInviteRecordForToday(String userId){
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = System.currentTimeMillis();
        Criteria criteria1 = new Criteria();
        criteria1.orOperator(Criteria.where("inviteUserId").is(userId), Criteria.where("beInviteUserId").is(userId)).and("time").gte(startTime).lte(endTime);
        return mongoTemplate.find(Query.query(criteria1), UserInviteRecord.class);
    }

    public List<String> findAllInviteUser(){
        List<String> ids = mongoTemplate.findDistinct(new Query(), "inviteUserId", "userInviteRecord", UserInviteRecord.class, String.class);
        return ids;
    }
}
