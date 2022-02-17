package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.UserCoinOperationRecord;
import net.bytebuddy.agent.builder.AgentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserCoinOperationRecordDaoService {
    @Autowired
    UserCoinOperationRecordRepository userCoinOperationRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public UserCoinOperationRecord save(UserCoinOperationRecord userCoinOperationRecord){
        return userCoinOperationRecordRepository.save(userCoinOperationRecord);
    }

    public PageImpl<UserCoinOperationRecord> findAllByPage (int page , int size){
        Criteria criteria = new Criteria();
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"time"));
        Pageable thePage = PageRequest.of(page,size);
        long count = mongoTemplate.count(query, UserCoinOperationRecord.class);
        PageImpl<UserCoinOperationRecord> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), UserCoinOperationRecord.class),PageRequest.of(page,size),count);
        return personData;
    }

    public PageImpl<UserCoinOperationRecord> findByBackOfficeIdAndPage(String backOfficeId,int page,int size){
        Criteria criteria = Criteria.where("backOfficeId").is(backOfficeId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"time"));
        Pageable thePage = PageRequest.of(page,size);
        long count = mongoTemplate.count(query, UserCoinOperationRecord.class);
        PageImpl<UserCoinOperationRecord> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), UserCoinOperationRecord.class),PageRequest.of(page,size),count);
        return personData;
    }

    public PageImpl<UserCoinOperationRecord> findByUserIdAndPage(String userId ,int page ,int size){
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"time"));
        Pageable thePage = PageRequest.of(page,size);
        long count = mongoTemplate.count(query, UserCoinOperationRecord.class);
        PageImpl<UserCoinOperationRecord> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), UserCoinOperationRecord.class),PageRequest.of(page,size),count);
        return personData;
    }

    public PageImpl<UserCoinOperationRecord> findByBackOfficeIdAndUserIdAndPage(String backOfficeId,String userId ,int page ,int size){
        Criteria criteria = Criteria.where("backOfficeId").is(backOfficeId);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("userId").is(userId));
        criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"time"));
        Pageable thePage = PageRequest.of(page,size);
        long count = mongoTemplate.count(query, UserCoinOperationRecord.class);
        PageImpl<UserCoinOperationRecord> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), UserCoinOperationRecord.class),PageRequest.of(page,size),count);
        return personData;
    }

}
