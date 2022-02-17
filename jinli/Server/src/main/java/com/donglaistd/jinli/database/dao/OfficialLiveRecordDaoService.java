package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class OfficialLiveRecordDaoService {
    @Autowired
    private OfficialLiveRecordRepository officialLiveRecordRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public OfficialLiveRecord findById(String id){
        return officialLiveRecordRepository.findById(id);
    }

    public OfficialLiveRecord findRecentOpenOfficialLiveByLiveUserId(String liveUserId){
        return officialLiveRecordRepository.findByLiveUserIdAndOpen(liveUserId, true).stream().max(Comparator.comparing(OfficialLiveRecord::getRoomCreateDate)).orElse(null);
    }

    public PageImpl<OfficialLiveRecord> findByIsClose(boolean statue, int page, int size){
        Pageable thePage = PageRequest.of(page,size);
        Criteria criteria = Criteria.where("open").is(statue);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, OfficialLiveRecord.class);
        return  new PageImpl<>(mongoTemplate.find(query.with(thePage), OfficialLiveRecord.class),PageRequest.of(page,size),count);
    }

    public PageImpl<OfficialLiveRecord>findCloseRoom(int page, int size, Long startTime, Long endTime, Constant.GameType gameType){
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria criteria = new Criteria().and("open").is(false);
        if(Objects.nonNull(startTime))criteriaList.add(Criteria.where("roomCloseDate").gte(startTime));
        if(Objects.nonNull(endTime))criteriaList.add(Criteria.where("roomCloseDate").lte(endTime));
        if(!gameType.equals(Constant.GameType.EMPTY))criteriaList.add(Criteria.where("gameType").is(gameType));
        if(!criteriaList.isEmpty()) criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long count =  mongoTemplate.count(query, OfficialLiveRecord.class);
        PageImpl<OfficialLiveRecord> personData = new PageImpl<>(mongoTemplate.find(query.with(thePage), OfficialLiveRecord.class),PageRequest.of(page,size),count);
        return personData;
    }

    public List<OfficialLiveRecord> findByIsClose(boolean close){
        return officialLiveRecordRepository.findByOpenIs(close);
    }

    public void save(OfficialLiveRecord officialLiveRecord){
        officialLiveRecordRepository.save(officialLiveRecord);
    }

    public void saveAll(List<OfficialLiveRecord> records) {
        officialLiveRecordRepository.saveAll(records);
    }
}
