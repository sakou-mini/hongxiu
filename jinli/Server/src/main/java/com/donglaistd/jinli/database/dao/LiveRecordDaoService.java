package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveRecord;
import com.donglaistd.jinli.domain.LiveRecordResult;
import com.donglaistd.jinli.http.dto.request.LiveRecordRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.CommonCriteriaUtil;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class LiveRecordDaoService {
    @Autowired
    LiveRecordRepository liveRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RoomDaoService roomDaoService;

    public LiveRecord save(LiveRecord liveRecord){
        return liveRecordRepository.save(liveRecord);
    }

    public long countLiveRecordNumByLiveUserId(String liveUserId){
        return liveRecordRepository.countByLiveUserId(liveUserId);
    }

    public long totalLiveUserLiveTimeByTimeBetween(String liveUserId,long startTime,long endTime){
        Criteria criteria = Criteria.where("liveUserId").is(liveUserId).and("liveStartTime").gte(startTime).lte(endTime);
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class, Aggregation.match(criteria),
                Aggregation.group("$liveUserId").first("liveUserId").as("liveUserId").sum("liveTime").as("liveTime"),
                Aggregation.project("liveUserId","liveTime").andExclude("_id"));
        LiveRecord liveRecord = mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecord.class).getUniqueMappedResult();
        return liveRecord == null ? 0 : liveRecord.getLiveTime();
    }

    public LiveRecord totalLiveRecordInfo(String liveUserId) {
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class,
                Aggregation.match(Criteria.where("liveUserId").is(liveUserId)),
                Aggregation.group("$liveUserId").first("liveUserId").as("liveUserId").count().as("recordTime")
                        .first("platform").as("platform")
                        .sum("gameFlow").as("gameFlow")
                        .sum("giftFlow").as("giftFlow")
                        .sum("liveTime").as("liveTime")
                        .sum("audienceNum").as("audienceNum"),
                Aggregation.project("liveUserId","gameFlow","giftFlow","liveTime","recordTime","audienceNum").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecord.class).getUniqueMappedResult();
    }

    public LiveRecord totalLiveRecordInfoByRecordTimes(String liveUserId, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class,
                Aggregation.match(Criteria.where("liveUserId").is(liveUserId).and("recordTime").gte(startTime).lte(endTime)),
                Aggregation.group("$liveUserId").first("liveUserId").as("liveUserId").count().as("recordTime")
                        .first("platform").as("platform")
                        .sum("gameFlow").as("gameFlow")
                        .sum("giftFlow").as("giftFlow")
                        .sum("liveTime").as("liveTime")
                        .sum("audienceNum").as("audienceNum"),
                Aggregation.project("liveUserId","gameFlow","giftFlow","liveTime","recordTime","audienceNum").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecord.class).getUniqueMappedResult();
    }

    public LiveRecord findRecentLiveRecordByLiveUser(String liveUserId) {
        Criteria criteria = Criteria.where("liveUserId").is(liveUserId);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"liveStartTime"));
        return mongoTemplate.findOne(query.with(PageRequest.of(0, 1)), LiveRecord.class);
    }

    public List<LiveRecordResult> getTotalGroupLiveRecordByTimes(long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime)),
                Aggregation.group("$liveUserId")
                        .first("liveUserId").as("liveUserId")
                        .first("platform").as("platform")
                        .sum("gameFlow").as("gameFlow")
                        .sum("giftFlow").as("giftFlow")
                        .sum("liveTime").as("liveTime")
                        .addToSet("audienceHistory").as("audienceHistory"),
                Aggregation.project("liveUserId","gameFlow", "liveTime", "giftFlow","platform","audienceHistory").andExclude("_id"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"giftFlow")));
        return mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecordResult.class).getMappedResults();
    }

  /*  public Map<String, Integer> getAudienceNumByTimes(long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime)),
                Aggregation.unwind("audienceHistory"),
                Aggregation.group("$liveUserId")
                        .first("liveUserId").as("liveUserId")
                        .addToSet("audienceHistory").as("audienceHistory"),
                Aggregation.project("liveUserId").and("audienceHistory").size().as("audienceNum").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecord.class).getMappedResults().stream()
                .collect(Collectors.toMap(LiveRecord::getLiveUserId, LiveRecord::getAudienceNum));
    }*/

    public LiveRecord totalLiveRecordByLiveUserIdAndTimes(String liveUserId,long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class,
                Aggregation.match(Criteria.where("liveUserId").is(liveUserId).and("recordTime").gte(startTime).lte(endTime)),
                Aggregation.group("$liveUserId")
                        .first("liveUserId").as("liveUserId")
                        .first("platform").as("platform")
                        .sum("gameFlow").as("gameFlow")
                        .sum("giftFlow").as("giftFlow")
                        .sum("liveTime").as("liveTime"),
                Aggregation.project("liveUserId","gameFlow", "liveTime", "giftFlow","platform").andExclude("_id"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"giftFlow")));
        LiveRecord liveRecord = mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecord.class).getUniqueMappedResult();
        if(liveRecord == null) return null;
        int audienceNum = getAudienceNumByLiveUserIdAndTimes(liveUserId, startTime, endTime);
        liveRecord.setAudienceNum(audienceNum);
        return liveRecord;
    }

    private int getAudienceNumByLiveUserIdAndTimes(String liveUserId, long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(LiveRecord.class,
                Aggregation.match(Criteria.where("liveUserId").is(liveUserId).and("recordTime").gte(startTime).lte(endTime)),
                Aggregation.unwind("audienceHistory"),
                Aggregation.group("$liveUserId")
                        .first("liveUserId").as("liveUserId")
                        .addToSet("audienceHistory").as("audienceHistory"),
                Aggregation.project("liveUserId").and("audienceHistory").size().as("audienceNum").andExclude("_id"));
        LiveRecord liveRecord = mongoTemplate.aggregate(aggregation, LiveRecord.class, LiveRecord.class).getUniqueMappedResult();
        return liveRecord == null ? 0 : liveRecord.getAudienceNum();
    }

    public PageInfo<LiveRecord> pageQueryLiveRecord(int size, int page, String liveUserId, String roomId, Constant.PlatformType platform, Long startTime,Long endTime){
        Pageable pageable = PageRequest.of(page,size);
        Criteria criteria = Criteria.where("platform").is(platform);
        if(!StringUtils.isNullOrBlank(liveUserId)) {
            criteria.and("liveUserId").is(liveUserId);
        }
        if(!StringUtils.isNullOrBlank(roomId)){
            criteria.and("roomId").is(roomId);
        }
        List<Criteria> timeCriteriaList = new ArrayList<>();
        if(startTime != null){
            timeCriteriaList.add(Criteria.where("recordTime").gte(startTime));
        }
        if(endTime != null){
            timeCriteriaList.add(Criteria.where("recordTime").lte(endTime));
        }
        if(!timeCriteriaList.isEmpty()){
            criteria = criteria.andOperator(timeCriteriaList.toArray(new Criteria[0]));
        }
        long count = mongoTemplate.count(Query.query(criteria), LiveRecord.class);
        List<LiveRecord> liveRecords = mongoTemplate.find(Query.query(criteria).with(pageable).with(Sort.by(Sort.Direction.DESC,"recordTime")), LiveRecord.class);
        return new PageInfo<>(liveRecords,pageable,count);
    }

    public List<LiveRecord> findLiveRecordsByPlatformAndTimes(Constant.PlatformType platform, long startTime,long endTime) {
        Criteria criteria = Criteria.where("platform").is(platform).and("recordTime").gte(startTime).lte(endTime);
        return mongoTemplate.find(Query.query(criteria), LiveRecord.class);
    }

    public void deleteRecordByPlatform(Constant.PlatformType platformType) {
        liveRecordRepository.deleteByPlatform(platformType);
    }

    public PageInfo<LiveRecord> findByLiveRecordByLiveRecordRequest(LiveRecordRequest recordRequest) {
        LookupOperation userOperation = LookupOperation.newLookup().from("user").localField("userId").foreignField("_id").as("user");
        List<Criteria> criteriaList = CommonCriteriaUtil.platformTimeQueryCriteria(recordRequest.getPlatformType(), recordRequest.getStartTime(), recordRequest.getEndTime());
        if(!StringUtils.isNullOrBlank(recordRequest.getRoomId())){
            criteriaList.add(Criteria.where("roomDisplayId").is(recordRequest.getRoomId()));
        }
        if(!StringUtils.isNullOrBlank(recordRequest.getLiveUserId())){
            criteriaList.add(Criteria.where("liveUserId").is(recordRequest.getLiveUserId()));
        }
        if(!StringUtils.isNullOrBlank(recordRequest.getDisplayName())){
            criteriaList.add(Criteria.where("user.displayName").is(recordRequest.getDisplayName()));
        }
        if(!StringUtils.isNullOrBlank(recordRequest.getAccount())){
            criteriaList.add(Criteria.where("user.accountName").is(recordRequest.getAccount()));
        }
        Criteria criteria = new Criteria().andOperator(criteriaList.toArray(new Criteria[0]));
        PageRequest pageRequest = null;
        if(Objects.nonNull(recordRequest.getPage()) && Objects.nonNull(recordRequest.getSize())){
            pageRequest = PageRequest.of(recordRequest.getPage(), recordRequest.getSize());
        }
        return pageQuery(criteria, userOperation, pageRequest);
    }

    private PageInfo<LiveRecord> pageQuery(Criteria criteria , LookupOperation user, PageRequest pageRequest) {
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(user, Aggregation.match(criteria)), LiveRecord.class, LiveRecord.class).getMappedResults().size();
        Aggregation agg;
        if(Objects.nonNull(pageRequest)){
            int page = pageRequest.getPageNumber();
            int size = pageRequest.getPageSize();
            agg = Aggregation.newAggregation(user, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,"recordTime")), Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        }else {
            agg = Aggregation.newAggregation(user, Aggregation.match(criteria), Aggregation.sort(Sort.by(Sort.Direction.DESC, "recordTime")));
        }
        return new PageInfo<>( mongoTemplate.aggregate(agg, LiveRecord.class, LiveRecord.class).getMappedResults(), pageRequest, totalNum);
    }

    public void saveAll(List<LiveRecord> liveRecordList) {
        liveRecordRepository.saveAll(liveRecordList);
    }
}

