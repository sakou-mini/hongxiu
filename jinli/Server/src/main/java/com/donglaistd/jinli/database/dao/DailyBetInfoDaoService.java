package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.http.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class DailyBetInfoDaoService {
    @Autowired
    private DailyBetInfoRepository dailyBetInfoRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public DailyBetInfo save(DailyBetInfo info) {
        return dailyBetInfoRepository.save(info);
    }

    public List<DailyBetInfo> saveAll(List<DailyBetInfo> infos) {
        return dailyBetInfoRepository.saveAll(infos);
    }

    public List<DailyBetInfo> findByLiveUserId(String liveUserId) {
        return dailyBetInfoRepository.findByLiveUserId(liveUserId);
    }

    public DailyBetInfo findTotalBetAmountGroupByLiveUserIdAndTimeBetween(String liveUserId,long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("liveUserId").is(liveUserId).and("time").gte(startTime).lte(endTime)),
                Aggregation.group("$liveUserId")
                        .first("liveUserId").as("liveUserId").sum("betAmount").as("betAmount"),
                Aggregation.project("liveUserId", "betAmount")
        );
        AggregationResults<DailyBetInfo> incomes = mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class);
        return incomes.getUniqueMappedResult();
    }

    public List<DailyBetInfo> findTotalBetAmountGroupByLiveUserIdAndTimeBetween(long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime)),
                Aggregation.group("$liveUserId")
                        .first("liveUserId").as("liveUserId").sum("betAmount").as("betAmount"),
                Aggregation.project("liveUserId", "betAmount")
        );
        AggregationResults<DailyBetInfo> incomes = mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class);
        return incomes.getMappedResults();
    }

    public List<DailyBetInfo> findTotalBetPopulationGroupByLiveUserIdAndTimeBetween(long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime)),
                Aggregation.group(Fields.fields("liveUserId")).first("liveUserId").as("liveUserId").addToSet("$betUserId").as("betUserId"),
                Aggregation.project("liveUserId").and("betUserId").size().as("betUserId")
        );
        AggregationResults<DailyBetInfo> incomes = mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class);
        return incomes.getMappedResults();
    }
    public void deleteAll(List<DailyBetInfo> infos) {
         dailyBetInfoRepository.deleteAll(infos);
    }

    public  List<DailyBetInfo> findByBetUserIdIsAndTimeBetween(String betUserId,long startTime,long endTime) {
        return dailyBetInfoRepository.findByBetUserIdIsAndTimeBetween(betUserId, startTime, endTime);
    }

    public List<DailyBetInfo> findByTimeBetweenAndSortByWin(int size, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime)),
                Aggregation.limit(size),
                Aggregation.group("$betUserId")
                        .first("betUserId").as("betUserId").sum("win").as("win"),
                Aggregation.project("betUserId", "win"),
                Aggregation.sort(Sort.Direction.DESC, "win")
        );
        AggregationResults<DailyBetInfo> incomes = mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class);
        return incomes.getMappedResults();
    }

    public  List<DailyBetInfo> findByBetUserIdInAndTimeBetween(Set<String> ids, long startTime, long endTime) {
        return dailyBetInfoRepository.findByBetUserIdInAndTimeBetween(ids, startTime, endTime);
    }

    public DailyBetInfo findByUserIdAndTimeTimeBetween(String userId, long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime).and("betUserId").is(userId)),
                Aggregation.group("$betUserId")
                        .first("betUserId").as("betUserId").sum("betAmount").as("betAmount"),
                Aggregation.project("betUserId","betAmount").andExclude("_id")
        );
        AggregationResults<DailyBetInfo> betInfos = mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class);
        return betInfos.getUniqueMappedResult();
    }

    public PageInfo<DailyBetInfo> findUserBetInfoPageInfo(String userId,int page, int pageSize){
        Criteria criteria = Criteria.where("betUserId").is(userId);
        Pageable thePage = PageRequest.of(page,pageSize);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.ASC, "time"));
        long count = mongoTemplate.count(query, DailyBetInfo.class);
        List<DailyBetInfo> dailyBetInfos = mongoTemplate.find(query.with(thePage), DailyBetInfo.class);
        return new PageInfo<>(dailyBetInfos, thePage, count);
    }

    public PageInfo<DailyBetInfo> findBetPageInfo(int page, int pageSize){
        Pageable thePage = PageRequest.of(page,pageSize);
        Query query = new Query().with(Sort.by(Sort.Direction.ASC, "time"));
        long count = mongoTemplate.count(query, DailyBetInfo.class);
        List<DailyBetInfo> dailyBetInfos = mongoTemplate.find(query.with(thePage), DailyBetInfo.class);
        return new PageInfo<>(dailyBetInfos, thePage, count);
    }

    public DailyBetInfo findTotalBetInfoByTimes(long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime)),
                Aggregation.group().count().as("win").sum("betAmount").as("betAmount"),
                Aggregation.project("win","betAmount").andExclude("_id")
        );
        return mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class).getUniqueMappedResult();
    }

    public DailyBetInfo findTotalAllBetInfo(){
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.group().count().as("win").sum("betAmount").as("betAmount"),
                Aggregation.project("win","betAmount").andExclude("_id")
        );
        return mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class).getUniqueMappedResult();
    }

    public Map<Constant.GameType,Long> totalGameIncomeByTimes(long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.match(Criteria.where("time").gte(startTime).lte(endTime)),
                Aggregation.group("$gameType").first("gameType").as("gameType")
                       .sum("betAmount").as("betAmount"),
                Aggregation.project("gameType","betAmount").andExclude("_id")
        );
        return mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class).getMappedResults()
                .stream().collect(Collectors.toMap(DailyBetInfo::getGameType,DailyBetInfo::getBetAmount));
    }

    public Map<Constant.GameType,Long> totalAllGameIncome(){
        Aggregation aggregation = Aggregation.newAggregation(DailyBetInfo.class,
                Aggregation.group("$gameType").first("gameType").as("gameType")
                        .sum("betAmount").as("betAmount"),
                Aggregation.project("gameType","betAmount").andExclude("_id")
        );
        return mongoTemplate.aggregate(aggregation, DailyBetInfo.class, DailyBetInfo.class).getMappedResults()
                .stream().collect(Collectors.toMap(DailyBetInfo::getGameType,DailyBetInfo::getBetAmount));
    }

}
