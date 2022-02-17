package com.donglaistd.jinli.database.dao.platform;

import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.plant.PlatformRechargeRecord;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PlatformRechargeRecordDaoService {
    @Autowired
    PlatformRechargeRecordRepository platformRechargeRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserDaoService userDaoService;

    public PlatformRechargeRecord save(PlatformRechargeRecord platformRechargeRecord){
        return platformRechargeRecordRepository.save(platformRechargeRecord);
    }

    public long totalRechargeCoinByUserId(String userId){
        Aggregation aggregation = Aggregation.newAggregation(PlatformRechargeRecord.class,
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.group().sum("rechargeGameCoin").as("rechargeNum"),
                Aggregation.project("rechargeNum").andExclude("_id"));
        Document document = mongoTemplate.aggregate(aggregation, PlatformRechargeRecord.class, Document.class).getUniqueMappedResult();
        return document == null ? 0 : document.getLong("rechargeNum");
    }

    public PageInfo<PlatformRechargeRecord> pageQuery(PageRequest pageRequest,Long startTime, Long endTime, String userId, String displayName) {
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(Objects.nonNull(startTime)){
            criteriaList.add(Criteria.where("rechargeTime").gte(startTime));
        }
        if(Objects.nonNull(endTime)){
            criteriaList.add(Criteria.where("rechargeTime").lte(endTime));
        }
        if(!StringUtils.isNullOrBlank(userId)) {
            User user = userDaoService.findUserByPlatformUserIdOrUserId(userId);
            if(Objects.isNull(user)) return new PageInfo<>(new ArrayList<>(), 0);
            criteriaList.add(Criteria.where("userId").is(user.getId()));
        }else if(!StringUtils.isNullOrBlank(displayName)){
            User user = userDaoService.findByDisplayName(displayName);
            if(Objects.isNull(user)) return new PageInfo<>(new ArrayList<>(), 0);
            criteriaList.add(Criteria.where("userId").is(user.getId()));
        }
        if(!criteriaList.isEmpty()){
            criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        }
        Query query = Query.query(criteria);
        long total = mongoTemplate.count(query, PlatformRechargeRecord.class);
        List<PlatformRechargeRecord> platformRechargeRecords = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC,"rechargeTime")), PlatformRechargeRecord.class);
        return new PageInfo<>(platformRechargeRecords, total);
    }

    public List<PlatformRechargeRecord> queryUserRechargeRecord(String userId,int num){
        Query query = Query.query(Criteria.where("userId").is(userId));
        return mongoTemplate.find(query.with(PageRequest.of(0, num))
                .with(Sort.by(Sort.Direction.DESC,"rechargeTime")), PlatformRechargeRecord.class);
    }

    public long totalRechargeCoinByTimes(long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(PlatformRechargeRecord.class,
                Aggregation.match(Criteria.where("rechargeTime").gte(startTime).lte(endTime)),
                Aggregation.group().sum("rechargeGameCoin").as("rechargeNum"),
                Aggregation.project("rechargeNum").andExclude("_id"));
        Document document = mongoTemplate.aggregate(aggregation, PlatformRechargeRecord.class, Document.class).getUniqueMappedResult();
        return document == null ? 0 : document.getLong("rechargeNum");
    }
}
