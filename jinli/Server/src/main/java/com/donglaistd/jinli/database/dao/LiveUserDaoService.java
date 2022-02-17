package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.LiveUserInfo;
import com.donglaistd.jinli.http.dto.request.LiveUserPageListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.util.internal.StringUtil;
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

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.LiveStatus.*;

@Service
@Transactional
public class LiveUserDaoService {
    @Autowired
    private LiveUserRepository repository;
    @Autowired
    private MongoTemplate template;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;

    @CachePut(cacheNames = "c1", key = "#result.id")
    public LiveUser save(LiveUser liveUser) {
        Assert.notNull(liveUser, "liveUser must not be null");
        return this.repository.save(liveUser);
    }

    protected void deleteAll() {
        this.repository.deleteAll();
    }

    public List<LiveUser> findLiveUsersByStatus(Constant.LiveStatus status) {
        return this.repository.findByLiveStatus(status);
    }

    public LiveUser findById(String liverId) {
        if(StringUtil.isNullOrEmpty(liverId)){
            return null;
        }
        return repository.findById(liverId).orElse(null);
    }

    public LiveUser findByIdAndStatus(String id , Constant.LiveStatus status){
        return repository.findByIdAndLiveStatus(id,status);
    }

    public LiveUser findByIdAndTime(String liveUserId, Constant.LiveStatus status,Long startTime,Long endTime){
        Criteria criteria = Criteria.where("id").is(liveUserId);
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("liveStatus").is(status));
        if(startTime!=null)criteriaList.add(Criteria.where("applyTime").gt(startTime));
        if (endTime!=null)criteriaList.add(Criteria.where("applyTime").lte(endTime));

        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query,LiveUser.class);

    }

    public PageImpl<LiveUser> findByStatusPage(int page ,int size ,Constant.LiveStatus status,Long startTime ,Long endTime){
        Criteria criteria = Criteria.where("liveStatus").is(status);
        List<Criteria> criteriaList = new ArrayList<>();
        if(startTime!=null)criteriaList.add(Criteria.where("applyTime").gte(startTime));
        if(endTime!=null)criteriaList.add(Criteria.where("applyTime").lte(endTime));
        if(!criteriaList.isEmpty()) criteria=criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        return pageQuery(page, size, Query.query(criteria));
    }

    public List<LiveUser> findAll() {
        return (List<LiveUser>) repository.findAll();
    }

    public List<LiveUserInfo> findByPageAndSortByFollowSize(long page, int size) {
        var agg = Aggregation.newAggregation(
                Aggregation.lookup("FollowList", "id", "followee.id", "LiveFollowList"),
                Aggregation.group("LiveFollowList.id").sum("LiveFollowList.").as("total"),
                Aggregation.skip(page > 1 ? (page - 1) * size : 0),
                Aggregation.limit(size),
                Aggregation.sort(Sort.Direction.DESC, "LiveFollowList.total")
        );
        var result = template.aggregate(agg, LiveUser.class, LiveUserInfo.class);
        return result.getMappedResults();
    }

    private PageImpl<LiveUser> pageQuery(int page , int size,Query query){
        Pageable thePage = PageRequest.of(page,size);
        long count = mongoTemplate.count(query, LiveUser.class);
        return new PageImpl<>(mongoTemplate.find(query.with(thePage), LiveUser.class),PageRequest.of(page,size),count);
    }


    public PageImpl<LiveUser> findPassLiveUser(int page ,int size){
        Criteria criteria = Criteria.where("liveStatus").in(OFFLINE,ONLINE,LIVE_BAN).and("scriptLiveUser").is(false);
        Query query = Query.query(criteria);
        Pageable thePage = PageRequest.of(page,size);
        long count = mongoTemplate.count(query, LiveUser.class);
        return new PageImpl<>(mongoTemplate.find(query.with(thePage), LiveUser.class),PageRequest.of(page,size),count);
    }


    public synchronized long countLiveUser(){
        return repository.countByUserIdIsNotNull();
    }

    public LiveUser findByUserId(String userId){
        return repository.findByUserId(userId);
    }

    public Map<Constant.GenderType,Long> groupLiveUserByGender(Constant.PlatformType platform){
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("liveStatus").in(OFFLINE, ONLINE,LIVE_BAN).and("scriptLiveUser").is(false).and("platformType").is(platform)),
                Aggregation.group("$gender").first("gender").as("gender").count().as("level"),
                Aggregation.project("gender","level").andExclude("_id")
        );
        List<LiveUser> mappedResults = mongoTemplate.aggregate(agg, LiveUser.class, LiveUser.class).getMappedResults();
        return mappedResults.stream().collect(Collectors.groupingBy(LiveUser::getGender, Collectors.summingLong(LiveUser::getLevel)));
    }

    public Map<Constant.PlatformType,Long> groupLiveUserByPlatform(){
        var agg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("liveStatus").in(OFFLINE, ONLINE,LIVE_BAN).and("scriptLiveUser").is(false)),
                Aggregation.group("$platformType").first("platformType").as("platformType").count().as("level"),
                Aggregation.project("platformType","level").andExclude("_id")
        );
        List<LiveUser> mappedResults = mongoTemplate.aggregate(agg, LiveUser.class, LiveUser.class).getMappedResults();
        return mappedResults.stream().collect(Collectors.groupingBy(LiveUser::getPlatformType, Collectors.summingLong(LiveUser::getLevel)));
    }

    public List<LiveUser> findAllPassLiveUser(){
        Criteria criteria = Criteria.where("liveStatus").in(OFFLINE, ONLINE,LIVE_BAN).and("scriptLiveUser").is(false);
        return mongoTemplate.find(Query.query(criteria), LiveUser.class);
    }

    public List<LiveUser> findAllPassLiveUserByPlatform(Constant.PlatformType platform){
        Criteria criteria = Criteria.where("liveStatus").in(OFFLINE, ONLINE,LIVE_BAN).and("scriptLiveUser").is(false).and("platformType").is(platform);
        return mongoTemplate.find(Query.query(criteria), LiveUser.class);
    }

    public PageInfo<LiveUser> queryLiveUserByPageInfoAndCondition(Constant.PlatformType platform, String liveUserId, String userId, String roomId ,
                                                                  LiveUserPageListRequest.QueryLiveUserStatue status, int page, int size){
        Criteria criteria = Criteria.where("scriptLiveUser").is(false);
        List<Criteria> condition = new ArrayList<>(3);
        if(platform != null)
            criteria.and("platformType").is(platform);
        if(!StringUtils.isNullOrBlank(liveUserId))
            condition.add(Criteria.where("id").is(liveUserId));
        if(!StringUtils.isNullOrBlank(userId))
            condition.add(Criteria.where("userId").is(userId));
        if(!StringUtil.isNullOrEmpty(roomId)) {
            Room room = roomDaoService.findByDisplayId(roomId);
            roomId = room == null ? "" : room.getId();
            condition.add(Criteria.where("roomId").is(roomId));
        }
        switch (status) {
            case ALL:criteria.and("liveStatus").in(OFFLINE, ONLINE,LIVE_BAN);break;
            case BAN:criteria.and("liveStatus").is(LIVE_BAN);break;
            case NORMAL:criteria.and("liveStatus").in(OFFLINE, ONLINE);break;
        }
        if(!condition.isEmpty()){
            criteria.orOperator(condition.toArray(new Criteria[0]));
        }
        Pageable thePage = PageRequest.of(page,size);
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, LiveUser.class);
        return new PageInfo<>(mongoTemplate.find(query.with(thePage).with(Sort.by(Sort.Direction.DESC,"auditTime")), LiveUser.class), thePage, count);
    }

    private  PageInfo<LiveUser> commonPageQuery(PageRequest pageInfo,Criteria criteria,LookupOperation lookupOperation,String sortedFiled){
        int totalNum = mongoTemplate.aggregate(Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria)), LiveUser.class, LiveUser.class).getMappedResults().size();
        int page = pageInfo.getPageNumber();
        int size = pageInfo.getPageSize();
        Aggregation agg = Aggregation.newAggregation(lookupOperation, Aggregation.match(criteria),Aggregation.sort(Sort.by(Sort.Direction.DESC,sortedFiled)), Aggregation.skip(page > 1 ? (page - 1) * size : 0L), Aggregation.limit(size));
        return new PageInfo<>( mongoTemplate.aggregate(agg, LiveUser.class, LiveUser.class).getMappedResults(), pageInfo, totalNum);
    }

    public PageInfo<LiveUser> findLiveUserByStatueAndPageInfoAndQueryCondition(PageRequest pageInfo,Constant.LiveStatus liveStatus, int queryType, String condition, Constant.PlatformType platform) {
        LookupOperation user = LookupOperation.newLookup().from("user").localField("userId").foreignField("_id").as("user");
        Criteria criteria = Criteria.where("platformType").is(platform).and("liveStatus").is(liveStatus);
        if(!StringUtils.isNullOrBlank(condition) && queryType != 0){
           switch (queryType){
               case 2:
                   criteria = criteria.and("userId").is(condition);
                   break;
               case 3:
                   criteria = criteria.and("realName").is(condition);
                   break;
               case 4:
                   criteria = criteria.and("user.phoneNumber").is(condition);
                   break;
           }
        }
        return commonPageQuery(pageInfo, criteria, user,"applyTime");
    }

    public PageInfo<LiveUser> findBanLiveUser(Constant.PlatformType platformType, String liveUserId, String liveUserName, String roomId, PageRequest pageRequest, Long startTime, Long endTime) {
        LookupOperation user = LookupOperation.newLookup().from("user").localField("userId").foreignField("_id").as("user");
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("platformType").is(platformType));
        criteriaList.add(Criteria.where("liveStatus").is(LIVE_BAN));
        if(!StringUtil.isNullOrEmpty(liveUserId)) {
            criteriaList.add(Criteria.where("_id").is(liveUserId));
        }
        if(!StringUtil.isNullOrEmpty(roomId)){
            Room room = roomDaoService.findByDisplayId(roomId);
            roomId = room == null ? "" : room.getId();
            criteriaList.add(Criteria.where("roomId").is(roomId));
        }
        if(Objects.nonNull(startTime)) {
            criteriaList.add(Criteria.where("banTime").gte(startTime));
        }
        if(Objects.nonNull(endTime)) {
            criteriaList.add(Criteria.where("banTime").lte(endTime));
        }
        if(!StringUtil.isNullOrEmpty(liveUserName)) {
            criteriaList.add(Criteria.where("user.displayName").is(liveUserName));
        }
        Criteria criteria = new Criteria();
        criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        return commonPageQuery(pageRequest, criteria, user ,"auditTime");
    }

    public void resetPlatformUserCoin(Constant.PlatformType platform) {
        List<LiveUser> liveUsers = repository.findByPlatformType(platform);
        for (LiveUser liveUser : liveUsers) {
            User user = dataManager.findUser(liveUser.getUserId());
            if(user != null) {
                user.setGameCoin(0);
                dataManager.saveUser(user);
            }
        }
    }

    public long countBanLiveUserByTimeAndPlatform(Constant.PlatformType platform, long startTime, long endTime) {
        Criteria criteria = Criteria.where("platformType").is(platform).and("liveStatus").is(LIVE_BAN).and("banTime").gte(startTime).lte(endTime);
        return mongoTemplate.count(Query.query(criteria), LiveUser.class);
    }
    
    public long countPassLiveUserByIdsAndPlatform(Set<String> liveUserIds, Constant.PlatformType platformType){
        Criteria criteria = Criteria.where("liveStatus").in(OFFLINE, ONLINE, LIVE_BAN).and("id").in(liveUserIds).and("platformType").is(platformType);
        return mongoTemplate.count(Query.query(criteria), LiveUser.class);
    }
}