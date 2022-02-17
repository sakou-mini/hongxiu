package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.GuessWagerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GuessWagerRecordDaoService {
    @Autowired
    GuessWagerRecordRepository guessWagerRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public void save(GuessWagerRecord guessWagerRecord) {
        guessWagerRecordRepository.save(guessWagerRecord);
    }

    public GuessWagerRecord findById(String id){
        return guessWagerRecordRepository.findById(id);
    }

    public List<GuessWagerRecord> findByUserId(String id){
        return guessWagerRecordRepository.findByUserId(id);
    }

    public List<GuessWagerRecord> findByGuessId(String id){
        return guessWagerRecordRepository.findByGuessId(id);
    }

    public long countByGuessId(String guessId){
        return guessWagerRecordRepository.countByGuessId(guessId);
    }

    public List<GuessWagerRecord> findByWagerTimeAndByUserId(String userId,Long queryTime,Long nowTime){
        Criteria criteria = Criteria.where("userId").is(userId);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("wagerTime").lte(nowTime));
        criteriaList.add(Criteria.where("wagerTime").gt(queryTime));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, GuessWagerRecord.class);
    }

    public PageImpl<GuessWagerRecord> findByGuessIdAndPage(String id ,int page,int size){
        Pageable thePage = PageRequest.of(page,size);
        Criteria criteria = Criteria.where("GuessId").is(id);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, GuessWagerRecord.class);
        PageImpl<GuessWagerRecord> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), GuessWagerRecord.class),PageRequest.of(page,size),count);
        return personData;
    }

}
