package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.LiveUserApproveStatue;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.LiveUserApproveRecord;
import com.donglaistd.jinli.http.dto.request.LiveUserApproveRecordRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class LiveUserApproveRecordDaoService {
    private final LiveUserApproveRecordRepository repository;

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserDaoService userDaoService;

    public LiveUserApproveRecordDaoService(LiveUserApproveRecordRepository userRepository) {
        this.repository = userRepository;
    }

    @CachePut(cacheNames = "c1")
    public LiveUserApproveRecord findById(String id) {
        return repository.findById(new ObjectId(id));
    }

    @CachePut(cacheNames = "c1")
    public List<LiveUserApproveRecord> findByApprovedUserId(String id,Long startTime,Long endTime){
        Criteria criteria = Criteria.where("approveLiveUserId").is(id);
        List<Criteria> criteriaList = new ArrayList<>();
        if(startTime!=null)criteriaList.add(Criteria.where("approveDate").gt(new Date(startTime)));
        if (endTime!=null)criteriaList.add(Criteria.where("approveDate").lte(new Date(endTime)));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query,LiveUserApproveRecord.class);
    }

    public PageImpl<LiveUserApproveRecord> findByApproveState(boolean ApproveState,int page,int size,Long startTime,Long endTime){
        Criteria criteria = Criteria.where("approveState").is(ApproveState);
        List<Criteria> criteriaList = new ArrayList<>();
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("approveDate").gte(new Date(startTime)));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("approveDate").lte(new Date(endTime)));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Pageable thePage = PageRequest.of(page,size);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, LiveUserApproveRecord.class);
        return new PageImpl<>(mongoTemplate.find(query.with(thePage), LiveUserApproveRecord.class),PageRequest.of(page,size),count);
    }

    @CachePut(cacheNames = "c1", key = "#result.id")
    public LiveUserApproveRecord save(LiveUserApproveRecord liveUserApproveRecord) {
        Assert.notNull(liveUserApproveRecord, "liveUserApproveRecord must not be null");
       return this.repository.save(liveUserApproveRecord);
    }

    public void saveAll(List<LiveUserApproveRecord> records) {
        this.repository.saveAll(records);
    }

    public PageInfo<LiveUserApproveRecord> findPageInfoApproveRecordByConditionAndTime(PageRequest pageInfo, String userId, boolean approveState, Long start, Long end, Constant.PlatformType platform) {
        Criteria criteria = Criteria.where("approveState").is(approveState).and("platform").is(platform);
        List<Criteria> criteriaList = new ArrayList<>();
        if(!StringUtils.isNullOrBlank(userId)){
            User user = userDaoService.findById(userId);
            if(user == null) return new PageInfo<>(new ArrayList<>(0),0);
            criteriaList.add(Criteria.where("approveLiveUserId").is(user.getLiveUserId()));
        }
        if(Objects.nonNull(start)) criteriaList.add(Criteria.where("approveDate").gte(new Date(start)));
        if(Objects.nonNull(end))  criteriaList.add(Criteria.where("approveDate").lte(new Date(end)));
        if (!criteriaList.isEmpty()){
            criteria = criteria.andOperator(criteriaList.toArray(criteriaList.toArray(new Criteria[0])));
        }
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, LiveUserApproveRecord.class);
        return new PageInfo<>(mongoTemplate.find(query.with(pageInfo).with(Sort.by(Sort.Direction.DESC,"approveDate")), LiveUserApproveRecord.class),count);
    }

    public PageInfo<LiveUserApproveRecord> pageQueryApproveRecord(LiveUserApproveRecordRequest request){
        LookupOperation liveUser = LookupOperation.newLookup().from("liveUser").localField("approveLiveUserId").foreignField("_id").as("liveUser");
        LookupOperation user = LookupOperation.newLookup().from("user").localField("userId").foreignField("_id").as("user");
        Criteria criteria = Criteria.where("platform").is(request.getPlatformType());
        if(!StringUtils.isNullOrBlank(request.getCondition()) && request.getQueryType() != 0){
            switch (request.getQueryType()){
                case 2:
                    criteria = criteria.and("userId").is(request.getCondition()); break;
                case 3:
                    criteria = criteria.and("liveUser.realName").is(request.getCondition());  break;
                case 4:
                    criteria = criteria.and("user.phoneNumber").is(request.getCondition());  break;
            }
        }
        switch (request.getStatueType()){
            case UNAPPROVE:
                criteria = criteria.and("statue").is(LiveUserApproveStatue.UNAPPROVE); break;
            case PASS:
                criteria = criteria.and("statue").is(LiveUserApproveStatue.PASS); break;
            case NO_PASS:
                criteria = criteria.and("statue").is(LiveUserApproveStatue.NO_PASS); break;
        }
        int page = request.getPage();
        int size = request.getLimit();
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(liveUser,user, Aggregation.match(criteria)), LiveUserApproveRecord.class, LiveUserApproveRecord.class).getMappedResults().size();
        Aggregation agg = Aggregation.newAggregation(liveUser,user, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,"applyDate")), Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        return new PageInfo<>( mongoTemplate.aggregate(agg, LiveUserApproveRecord.class, LiveUserApproveRecord.class).getMappedResults(), totalNum);
    }

    public LiveUserApproveRecord findRecentApproveRecordByStatue(String liveUserId,LiveUserApproveStatue statue){
        Criteria criteria = Criteria.where("approveLiveUserId").is(liveUserId).and("statue").is(statue);
        Query query = Query.query(criteria);
        List<LiveUserApproveRecord> approveRecord = mongoTemplate.find(query.with(Sort.by(Sort.Direction.DESC, "applyDate")), LiveUserApproveRecord.class);
        if(approveRecord.size() > 1){
            repository.deleteAll(approveRecord.subList(1,approveRecord.size()));
        }
        if(approveRecord != null && !approveRecord.isEmpty()) return approveRecord.get(0);
        return null;
    }
}
