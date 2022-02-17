package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.LiveLimitRecord;
import com.donglaistd.jinli.http.dto.request.LiveLimitRecordRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LiveLimitRecordDaoService {
    @Autowired
    LiveLimitRecordRepository liveLimitRecordRepository;
    @Autowired
    MongoTemplate  mongoTemplate;

    public LiveLimitRecord save(LiveLimitRecord liveLimitRecord){
        return liveLimitRecordRepository.save(liveLimitRecord);
    }

    public PageInfo<LiveLimitRecord> pageQueryLiveLimitRecord(LiveLimitRecordRequest request){
        LookupOperation userOperation = LookupOperation.newLookup().from("user").localField("liveUserId").foreignField("liveUserId").as("user");
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("platform").is(request.getPlatformType()));
        if(request.getLimitDate()!=null){
            criteriaList.add(Criteria.where("limitDate").is(request.getLimitDate()));
        }
        if(request.getStartHour()!=null){
            criteriaList.add(Criteria.where("liveStartHour").is(request.getStartHour()));
        }
        if(!StringUtils.isNullOrBlank(request.getLiveUserId())){
            criteriaList.add(Criteria.where("liveUserId").is(request.getLiveUserId()));
        }
        if(!StringUtils.isNullOrBlank(request.getDisplayName())){
            criteriaList.add(Criteria.where("user.displayName").is(request.getDisplayName()));
        }
        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        return pageQuery(criteria, userOperation, request.getPage(), request.getSize());
    }

    private PageInfo<LiveLimitRecord> pageQuery(Criteria criteria , LookupOperation user, int page,int size ) {
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(user, Aggregation.match(criteria)), LiveLimitRecord.class, LiveLimitRecord.class).getMappedResults().size();
        Aggregation agg = Aggregation.newAggregation(user, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,"recordTime")), Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        return new PageInfo<>( mongoTemplate.aggregate(agg, LiveLimitRecord.class, LiveLimitRecord.class).getMappedResults(), totalNum);
    }
}
