package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.util.TimeUtil;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class UserDataStatisticsDaoService {
    @Autowired
    UserDataStatisticsRepository userDataStatisticsRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    UserDaoService userDaoService;


    public UserDataStatistics findUserDataByTime(String userId,long time,boolean newUser){
        return userDataStatisticsRepository.findByUserIdAndRecordTimeAndNewUser(userId, time,newUser);
    }

    public UserDataStatistics save(UserDataStatistics userDataStatistics){
        return userDataStatisticsRepository.save(userDataStatistics);
    }
    //only User Is liveUser to deleteData
    public void deleteTodayUserDataStatistics(String userId){
        userDataStatisticsRepository.deleteByUserIdAndRecordTime(userId, TimeUtil.getCurrentDayStartTime());
    }

    public long countRegisterUserNumByTimeBetween(long startTime, long endTime){
        return countRegisterUserNumByTimeBetweenAndPlatform(startTime, endTime, null);
    }

    public long countRegisterUserNumByTimeBetweenAndPlatform(long startTime, long endTime,Constant.PlatformType platformType){
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("recordTime").gte(startTime),Criteria.where("recordTime").lte(endTime),Criteria.where("newUser").is(true));
        if(Objects.nonNull(platformType)){
            criteria = criteria.and("platformType").is(platformType);
        }
        Query query = Query.query(criteria);
        Set<String> registerIds = mongoTemplate.find(query, UserDataStatistics.class).stream().map(UserDataStatistics::getUserId).collect(Collectors.toSet());
        criteria =  new Criteria().andOperator(Criteria.where("recordTime").gte(startTime), Criteria.where("recordTime").lte(endTime), Criteria.where("newUser").is(false));
        if(Objects.nonNull(platformType)){
            criteria = criteria.and("platformType").is(platformType);
        }
        Set<String> loginIds = mongoTemplate.find(Query.query(criteria), UserDataStatistics.class).stream().map(UserDataStatistics::getUserId).collect(Collectors.toSet());
        registerIds.retainAll(loginIds);
        return registerIds.stream().filter(id -> !verifyUtil.checkIsLiveUser(userDaoService.findById(id))).count();
    }

    public long countUserNumByTimesAndPlatformAndNewUser(long startTime, long endTime, Constant.PlatformType platform, boolean newUser){
        List<String> userIds = liveUserDaoService.findAllPassLiveUserByPlatform(platform).stream().map(LiveUser::getUserId).collect(Collectors.toList());
        Criteria criteria = Criteria.where("recordTime").gte(startTime).lte(endTime).and("newUser").is(newUser).and("platformType").is(platform).and("userId").nin(userIds);
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, UserDataStatistics.class).stream()
                .filter(userDataStatistics -> !verifyUtil.checkIsLiveUser(userDaoService.findById(userDataStatistics.getUserId()))).count();
    }

    public List<UserDataStatistics> groupIsNewUserDataByTimeBetween(long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(UserDataStatistics.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime)),
                Aggregation.group("$newUser").first("newUser").as("newUser").count().as("activeDays"),
                Aggregation.project("newUser","activeDays").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, UserDataStatistics.class,UserDataStatistics.class).getMappedResults();
    }

    public int countUserActiveNumByActiveDays(long startTime,long endTime,int activeDays){
        Aggregation aggregation = Aggregation.newAggregation(UserDataStatistics.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime).and("activeDays").gte(activeDays).and("newUser").is(false)),
                Aggregation.group("$userId").first("userId").as("userId"),
                Aggregation.project("userId").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation,UserDataStatistics.class,UserDataStatistics.class).getMappedResults().size();
    }


    private int totalIpNumByList( List<UserDataStatistics> dataStatisticsList){
        Set<String> ips = new HashSet<>();
        dataStatisticsList.forEach(userData -> ips.addAll(userData.getIpList()));
        return ips.size();
    }

    public long countIpNumByTimeBetween(long startTime,long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(UserDataStatistics.class, Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime)));
        List<UserDataStatistics> results = mongoTemplate.aggregate(aggregation, UserDataStatistics.class, UserDataStatistics.class).getMappedResults();
        return totalIpNumByList(results);
    }

    public long countIpNumByTimeBetweenAndPlatform(long startTime, long endTime, Constant.PlatformType platform) {
        Aggregation aggregation = Aggregation.newAggregation(UserDataStatistics.class, Aggregation.match(Criteria.where("recordTime").gte(startTime)
                .lte(endTime).and("platformType").is(platform)));
        List<UserDataStatistics> results = mongoTemplate.aggregate(aggregation, UserDataStatistics.class, UserDataStatistics.class).getMappedResults();
        return totalIpNumByList(results);
    }

    public List<UserDataStatistics> totalMaxActiveDaysGroupByUserIdAndTimeBetween(long startTime,long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(UserDataStatistics.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime).and("newUser").is(false).and("activeDays").gte(2)),
                Aggregation.group("$userId").first("userId").as("userId").max("activeDays").as("activeDays"),
                Aggregation.project("userId","activeDays").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, UserDataStatistics.class, UserDataStatistics.class).getMappedResults();
    }

    public BigDecimal totalRetainedRate(long time,int day) {
        long registerTime = TimeUtil.getTimeDayStartTime(time) - TimeUnit.DAYS.toMillis(day);
        List<String> registerUserIds = userDataStatisticsRepository.findByRecordTimeIsAndNewUserIs(registerTime, true)
                .stream().map(UserDataStatistics::getUserId).collect(Collectors.toList());
        if(registerUserIds.isEmpty()) return  BigDecimal.valueOf(0);
        long activeNum = userDataStatisticsRepository.countByRecordTimeIsAndActiveDaysGreaterThanEqualAndUserIdIn(TimeUtil.getTimeDayStartTime(time), day, registerUserIds);
        return BigDecimal.valueOf(activeNum).divide(BigDecimal.valueOf(registerUserIds.size()),4, RoundingMode.HALF_UP);
    }

    public List<UserDataStatistics> findByTimeBetween(long startTime,long endTime){
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("recordTime").gte(startTime),Criteria.where("recordTime").lte(endTime));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, UserDataStatistics.class);
    }

    public List<UserDataStatistics> findByUserIdAndTimeBetween(String userId,long startTime,long endTime){
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("userId").is(userId),Criteria.where("recordTime").gte(startTime),Criteria.where("recordTime").lte(endTime));
        Query query = Query.query(criteria);
        return mongoTemplate.find(query, UserDataStatistics.class);
    }

    public Map<Long,Integer> groupDayLoginNumByUserIdsAndTimeBetween(long startTime, long endTime, List<String> userIds){
        Aggregation aggregation = Aggregation.newAggregation(UserDataStatistics.class,
                Aggregation.match(Criteria.where("recordTime").gte(startTime).lte(endTime).and("userId").in(userIds)),
                Aggregation.group("$recordTime").first("recordTime").as("recordTime").addToSet("userId").as("userId"),
                Aggregation.project("recordTime").and("userId").size().as("activeDays").andExclude("_id"));
        List<UserDataStatistics> results = mongoTemplate.aggregate(aggregation, UserDataStatistics.class, UserDataStatistics.class).getMappedResults();
        return results.stream().collect(Collectors.toMap(UserDataStatistics::getRecordTime, UserDataStatistics::getActiveDays));
    }

    public UserDataStatistics findUserRecentLoginInfo(String userId){
        Criteria criteria = Criteria.where("userId").is(userId).and("newUser").is(false);
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "recordTime")).with(PageRequest.of(0, 1));
        UserDataStatistics statisticsInfo = mongoTemplate.findOne(query, UserDataStatistics.class);
        if(statisticsInfo == null) return UserDataStatistics.newInstance(userId, false);
        return statisticsInfo;
    }
}
