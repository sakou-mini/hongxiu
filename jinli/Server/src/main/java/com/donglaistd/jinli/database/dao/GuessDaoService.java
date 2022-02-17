package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.Guess;
import com.mongodb.client.result.UpdateResult;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class GuessDaoService {
    @Autowired
    GuessRepository guessRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    MongoOperations mongoOperations;

    public PageImpl<Guess> findStatueAndGuessTypeAndTime(
            Long startTime,
            Long endTime,
            Constant.GuessState state,
            Constant.GuessType guessType,
            String sortItem,
            int page, int size,
            Long nowTime){
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        String sort;
        Criteria criteria =new Criteria();

        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES_VALUE));
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("showStartTime").gte(startTime));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("showStartTime").lte(endTime));
        if(Strings.isNotBlank(sortItem)) sort = sortItem;
        else sort = "showStartTime";
        criteriaList.add(Criteria.where("showStartTime").lte(nowTime));
        criteriaList.add(Criteria.where("showEndTime").gt(nowTime));
        if(guessType!=null) criteriaList.add(Criteria.where("guessType").is(guessType));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,sort));
        long count = mongoTemplate.count(query, Guess.class);
        PageImpl<Guess> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Guess.class),PageRequest.of(page,size),count);
       return personData;
    }

    public Guess findById(String id){
        return guessRepository.findById(Long.parseLong(id));
    }

    @Transactional
    public Guess save(Guess guess) {
        return guessRepository.save(guess);
    }

    public PageImpl<Guess> findByIsShow(
            Long startTime,
            Long endTime,
            Constant.GuessShow isShow,
            Constant.GuessType guessType,
            String sortItem,
            int page, int size,
            Long nowTime){
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        String sort;
        Criteria criteria = new Criteria();
        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES));
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("showStartTime").gte(startTime));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("showStartTime").lte(endTime));
        if(Strings.isNotBlank(sortItem)) sort = sortItem;
        else sort = "showStartTime";
        criteriaList.add(Criteria.where("showStartTime").gte(nowTime));
        if(guessType!=null) criteriaList.add(Criteria.where("guessType").is(guessType));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,sort));
        long count = mongoTemplate.count(query, Guess.class);
        PageImpl<Guess> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Guess.class),PageRequest.of(page,size),count);
        return personData;
    }


    public PageImpl<Guess> findByShowOverGuess(
            Long startTime,
            Long endTime,
            Constant.GuessType guessType,
            String sortItem,
            int page, int size,
            Long nowTime){
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        String sort;
        Criteria criteria =new Criteria();
        criteriaList.add(Criteria.where("state").is(Constant.GuessState.LOTTERY));
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("showStartTime").gte(startTime));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("showStartTime").lte(endTime));
        if(Strings.isNotBlank(sortItem)) sort = sortItem;
        else sort = "showStartTime";
        criteriaList.add(Criteria.where("showEndTime").lte(nowTime));
        if(guessType!=null) criteriaList.add(Criteria.where("guessType").is(guessType));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,sort));
        long count = mongoTemplate.count(query, Guess.class);
        PageImpl<Guess> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Guess.class),PageRequest.of(page,size),count);
        return personData;
    }

    public PageImpl<Guess> findByWagerOverGuess(
            Long startTime,
            Long endTime,
            Constant.GuessType guessType,
            String sortItem,
            int page, int size,
            Long nowTime){
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        String sort;
        Criteria criteria =Criteria.where("wagerEndTime").lte(nowTime);
        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES));
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("showStartTime").gte(startTime));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("showStartTime").lte(endTime));
        if(Strings.isNotBlank(sortItem)) sort = sortItem;
        else sort = "showStartTime";
        if(guessType!=null) criteriaList.add(Criteria.where("guessType").is(guessType));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,sort));
        long count = mongoTemplate.count(query, Guess.class);
        PageImpl<Guess> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Guess.class),PageRequest.of(page,size),count);
        return personData;
    }

    public List<Guess>findByTypeAndShow(Constant.GuessType guessType, Long nowTime){
        Criteria criteria = Criteria.where("guessType").is(guessType);
        List<Criteria> criteriaList = new ArrayList<>();
        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES));
        criteriaList.add(Criteria.where("showStartTime").lte(nowTime));
        criteriaList.add(Criteria.where("showEndTime").gt(nowTime));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Guess.class);
    }

    public List<Guess>findAllShowGuess(Long nowTime){
        Criteria criteria = new Criteria();
        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES));
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("showStartTime").lte(nowTime));
        criteriaList.add(Criteria.where("showEndTime").gt(nowTime));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, Guess.class);
    }

    public PageImpl<Guess>findByTimeLteNowTime(Long nowTime ,int page,int size){
        Criteria criteria = Criteria.where("wagerEndTime").lte(nowTime);
        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES),Criteria.where("state").is(Constant.GuessState.LOTTERY));
        Pageable thePage = PageRequest.of(page,size);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Guess.class);
        PageImpl<Guess> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Guess.class),PageRequest.of(page,size),count);
        return personData;
    }

    public PageImpl<Guess>findByWagerStartTime(Long nowTime,int page,int size){
        Criteria criteria = Criteria.where("wagerStartTime").lte(nowTime);
        criteria.norOperator(Criteria.where("state").is(Constant.GuessState.PULL_OFF_SHELVES));
        Pageable thePage = PageRequest.of(page,size);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Guess.class);
        PageImpl<Guess> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Guess.class),PageRequest.of(page,size),count);
        return personData;
    }

    public synchronized UpdateResult increaseGuessTotalGameCoinAndTotal(Guess guess, long increaseAmount) {
        return mongoOperations.updateFirst(new Query(Criteria.where("_id").is(Long.parseLong(guess.getId()))), new Update().inc("totalCoin", increaseAmount).inc("total",1), Guess.class);
    }

}
