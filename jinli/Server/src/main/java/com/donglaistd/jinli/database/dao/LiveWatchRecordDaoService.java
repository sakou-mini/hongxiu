package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveWatchRecord;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class LiveWatchRecordDaoService {
    @Autowired
    LiveWatchRecordRepository liveWatchRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserDaoService userDaoService;

    public LiveWatchRecord totalLiveWatchRecordByTimesAndPlatform(long startTime , long endTime, Constant.PlatformType platform) {
        Aggregation aggregation = Aggregation.newAggregation(LiveWatchRecord.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime).and("platform").is(platform)),
                Aggregation.group().sum("watchTime").as("watchTime").addToSet("userId").as("userId"),
                Aggregation.project("watchTime").and("userId").size().as("connectedLiveCount").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, LiveWatchRecord.class, LiveWatchRecord.class).getUniqueMappedResult();
    }

    public LiveWatchRecord save(LiveWatchRecord liveWatchRecord){
        return liveWatchRecordRepository.save(liveWatchRecord);
    }

    public List<LiveWatchRecord> saveAll(List<LiveWatchRecord> liveWatchRecords){
        return liveWatchRecordRepository.saveAll(liveWatchRecords);
    }

    public void deleteAll(){
        liveWatchRecordRepository.deleteAll();
    }

    public List<LiveWatchRecord> findByUserId(String id) {
        return liveWatchRecordRepository.findByUserId(id);
    }

    public PageInfo<LiveWatchRecord> findWatchLiveRecordByRequest(UserListRequest request) {
        LookupOperation userOperation = LookupOperation.newLookup().from("user").localField("userId").foreignField("_id").as("user");
        Criteria criteria = Criteria.where("platform").is(request.getPlatformType());
        List<Criteria> criteriaList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(request.getUserId())) {
            User user = userDaoService.findUserByPlatformUserIdOrUserId(request.getUserId());
            if(Objects.isNull(user)) return new PageInfo<>(new ArrayList<>(), 0);
            criteria.and("userId").is(user.getId());
        }
        if (!StringUtil.isNullOrEmpty(request.getDisplayName())) criteria.and("user.displayName").is(request.getDisplayName());
        if(Objects.nonNull(request.getStartTime())) criteriaList.add(Criteria.where("recordTime").gte(request.getStartTime()));
        if(Objects.nonNull(request.getEndTime())) criteriaList.add(Criteria.where("recordTime").lte(request.getEndTime()));
        if(!criteriaList.isEmpty())
            criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        int page = request.getPage();
        int size = request.getSize();
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(userOperation, Aggregation.match(criteria)), LiveWatchRecord.class, LiveWatchRecord.class).getMappedResults().size();
        Aggregation agg = Aggregation.newAggregation(userOperation, Aggregation.match(criteria),
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"recordTime")), Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        return new PageInfo<>( mongoTemplate.aggregate(agg, LiveWatchRecord.class, LiveWatchRecord.class).getMappedResults(), totalNum);
    }

    public void cleanErrorData(){
        liveWatchRecordRepository.removeByRoomLiveUserIdIsNull();
    }

    public List<LiveWatchRecord> findByTimesAndPlatform(long startTime, long endTime, Constant.PlatformType platform) {
        Criteria criteria = Criteria.where("recordTime").gte(startTime).lte(endTime).and("platform").is(platform);
        return mongoTemplate.find(Query.query(criteria), LiveWatchRecord.class);
    }

    public List<LiveWatchRecord> groupUserWatchRecord(){
        Aggregation aggregation = Aggregation.newAggregation(LiveWatchRecord.class,
                Aggregation.group("userId").first("userId").as("userId").sum("watchTime").as("watchTime")
                        .count().as("connectedLiveCount")
                        .addToSet("roomId").as("roomId"),
                Aggregation.project("watchTime","userId","connectedLiveCount").and("roomId").size().as("bulletMessageCount").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, LiveWatchRecord.class, LiveWatchRecord.class).getMappedResults();
    }

    public LiveWatchRecordResult totalUserWatchRecord(String userId){
        Aggregation aggregation = Aggregation.newAggregation(LiveWatchRecord.class,
                Aggregation.match(Criteria.where("userId").is(userId)),
                Aggregation.group("userId").first("userId").as("userId")
                        .sum("watchTime").as("watchTime")
                        .sum("connectedLiveCount").as("connectedLiveCount")
                        .sum("bulletMessageCount").as("bulletMessageCount")
                        .count().as("watchCount")
                        .addToSet("roomId").as("roomId"),
                Aggregation.project("watchTime","userId","connectedLiveCount","bulletMessageCount","watchCount").and("roomId").size().as("watchNum").andExclude("_id"));
        LiveWatchRecordResult recordResult = mongoTemplate.aggregate(aggregation, LiveWatchRecord.class, LiveWatchRecordResult.class).getUniqueMappedResult();

        return Optional.ofNullable(recordResult).orElse(new LiveWatchRecordResult());
    }

    public List<LiveWatchRecordResult> totalAllUserWatchRecordByTimes(long startTime, long endTime){
        Criteria criteria = Criteria.where("recordTime").gte(startTime).lte(endTime).and("userId").exists(true);
        Aggregation aggregation = Aggregation.newAggregation(LiveWatchRecord.class, Aggregation.match(criteria),
                Aggregation.group("userId").first("userId").as("userId")
                        .first("platform").as("platform")
                        .sum("watchTime").as("watchTime")
                        .addToSet("roomId").as("roomId")
                        .count().as("watchCount")
                        .sum("giftCount").as("giftCount")
                        .sum("giftFlow").as("giftFlow")
                        .sum("connectedLiveCount").as("connectedLiveCount")
                        .sum("bulletMessageCount").as("bulletMessageCount"),
                Aggregation.project("userId", "platform", "watchTime", "watchCount", "giftCount", "giftFlow", "connectedLiveCount", "bulletMessageCount")
                        .and("roomId").size().as("watchNum").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, LiveWatchRecord.class, LiveWatchRecordResult.class).getMappedResults();
    }
}
