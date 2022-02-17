package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GiftLogDaoService {
    @Autowired
    private GiftLogRepository giftLogRepository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;

    public GiftLog save(GiftLog giftLog) {
        Assert.notNull(giftLog, "giftLog must not be null");
        return giftLogRepository.save(giftLog);
    }

    public List<GiftLog> findBySenderId(String senderId) {
        return giftLogRepository.findBySenderId(senderId);
    }

    public List<GiftLog> saveAll(List<GiftLog> logs) {
        return giftLogRepository.saveAll(logs);
    }

    public List<GiftLog> findByCreateTimeBetweenAndGroupBySenderId(int size, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)),
                Aggregation.group("$senderId")
                        .first("senderId").as("senderId").sum("sendAmount").as("sendAmount"),
                Aggregation.project("senderId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount"),
                Aggregation.limit(size)
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }

    public List<GiftLog> findByCreateTimeBetweenAndGroupByReceiveId(int size, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)),
                Aggregation.group("$receiveId")
                        .first("receiveId").as("receiveId").sum("sendAmount").as("sendAmount"),
                Aggregation.project("receiveId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount"),
                Aggregation.limit(size)
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }

    public List<GiftLog> findByCreateTimeBetweenAndGroupByReceiveId(long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)),
                Aggregation.group("$receiveId")
                        .first("receiveId").as("receiveId").sum("sendAmount").as("sendAmount"),
                Aggregation.project("receiveId", "sendAmount").andExclude("_id")
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }

    public List<GiftLog> findByLiveUserIdAndGroupSenderId(String receiveId, int size, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime).and("receiveId").is(receiveId)),
                Aggregation.group("$senderId")
                        .first("senderId").as("senderId").sum("sendAmount").as("sendAmount").first("receiveId").as("receiveId"),
                Aggregation.project("receiveId", "senderId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount"),
                Aggregation.limit(size)
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }

    public List<GiftLog> findByLiveUserIdAndGroupSenderIdNotLimit(String receiveId, long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime).and("receiveId").is(receiveId)),
                Aggregation.group("$senderId")
                        .first("senderId").as("senderId").sum("sendAmount").as("sendAmount").first("receiveId").as("receiveId"),
                Aggregation.project("receiveId", "senderId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount")
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }


    public List<GiftLog> findByCreateTimeBetweenAndGroupBySenderId(int size) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.group("$senderId")
                        .first("senderId").as("senderId").sum("sendAmount").as("sendAmount"),
                Aggregation.project("senderId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount"),
                Aggregation.limit(size)
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }

    public List<GiftLog> findByCreateTimeBetweenAndGroupByReceiveId(int size) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.group("$receiveId")
                        .first("receiveId").as("receiveId").sum("sendAmount").as("sendAmount"),
                Aggregation.project("receiveId", "sendAmount").andExclude("_id"),
                Aggregation.sort(Sort.Direction.DESC, "sendAmount"),
                Aggregation.limit(size)
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getMappedResults();
    }

    public List<GiftLog> findByReceiverId(String receiverId) {
        return giftLogRepository.findByReceiveId(receiverId);
    }

    public GiftLog findByReceiveIdAndTimeBetween(String receiveId,long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime).and("receiveId").is(receiveId)),
                Aggregation.group("$receiveId").sum("sendAmount").as("sendAmount").first("receiveId").as("receiveId"),
                Aggregation.project("receiveId", "sendAmount").andExclude("_id")
        );
        AggregationResults<GiftLog> results = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class);
        return results.getUniqueMappedResult();
    }

    public GiftLog totalGiftUserNumAndGiftAmountByTimeBetween(long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)),
                Aggregation.group().sum("sendAmount").as("sendAmount").addToSet("senderId").as("senderId"),
                Aggregation.project("sendAmount").and("senderId").size().as("createTime").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class).getUniqueMappedResult();
    }

    public GiftLog totalGiftAmountByTimeBetweenAndPlatform(long startTime, long endTime, Constant.PlatformType platform){
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime).and("platformType").is(platform)),
                Aggregation.group().sum("sendAmount").as("sendAmount").addToSet("senderId").as("senderId"),
                Aggregation.project("sendAmount").and("senderId").size().as("createTime").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class).getUniqueMappedResult();
    }

    public GiftLog totalGiftAmountAndReceiverNumByTimeBetweenAndPlatform(long startTime, long endTime, Constant.PlatformType platform){
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime).and("platformType").is(platform)),
                Aggregation.group().sum("sendAmount").as("sendAmount").addToSet("receiveId").as("receiveId"),
                Aggregation.project("sendAmount").and("receiveId").size().as("sendNum").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class).getUniqueMappedResult();
    }

    private List<GiftLog> totalPageUserGiftInfoByTimeBetweenAndGroup(long startTime, long endTime, int page , int size){
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)),
                Aggregation.group("$senderId", "$receiveId", "$giftId").first("senderId").as("senderId")
                        .first("giftId").as("giftId")
                        .sum("sendAmount").as("sendAmount").sum("sendNum").as("sendNum"),
                Aggregation.project("senderId","receiveId", "giftId", "sendAmount", "sendNum").andExclude("_id"),
                Aggregation.sort(Sort.Direction.ASC, "sendAmount","senderId"),
                Aggregation.skip(page > 1 ? (page - 1) * size : 0L),
                Aggregation.limit(size));
        return mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class).getMappedResults();
    }

    private int countPageUserGiftInfoByTimeBetween(long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)), Aggregation.group("$senderId", "$receiveId", "$giftId"),
                Aggregation.project("senderId").andExclude("_id"));
        return mongoTemplate.aggregate(aggregation,GiftLog.class,GiftLog.class).getMappedResults().size();
    }

    //TODO  此方法可以优化
    public PageInfo<GiftLog> totalPageUserGiftInfoByTimeBetween(long startTime, long endTime, int page , int size){
        Pageable thePage = PageRequest.of(page,size);
        List<GiftLog> giftLogs = totalPageUserGiftInfoByTimeBetweenAndGroup(startTime, endTime, page, size);
        int totalNum = countPageUserGiftInfoByTimeBetween(startTime, endTime);
        return new PageInfo<>(giftLogs,thePage,totalNum);
    }

    public PageInfo<GiftLog> findGiftLogByTimesAndSendUserIdAndName(long startTime, long endTime ,String senderId,String senderName ,String roomId,Pageable pageable, Constant.PlatformType platform){
        Criteria criteria = Criteria.where("createTime").gte(startTime).lte(endTime).and("platformType").is(platform);
        if(!StringUtils.isNullOrBlank(roomId)){
            Room room = roomDaoService.findByDisplayId(roomId);
            if(room == null) return new PageInfo<>(Lists.newArrayList(),pageable,0);
            criteria.and("receiveId").is(liveUserDaoService.findById(room.getLiveUserId()).getUserId());
        }
        if(!StringUtils.isNullOrBlank(senderId)){
            User user = Optional.of(userDaoService.findUserByPlatformUserIdOrUserId(senderId)).orElse(new User());
            criteria.and("senderId").is(user.getId());
        }
        if(StringUtils.isNullOrBlank(senderId) && !StringUtils.isNullOrBlank(senderName) ){
            User user = userDaoService.findByDisplayName(senderName);
            if(user == null){
                return new PageInfo<>(Lists.newArrayList(),pageable,0);
            }
            criteria.and("senderId").is(user.getId());
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"createTime"));
        return pageQuery(query, pageable);
    }

    public long getUserSendGiftAmount(String userId){
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("senderId").is(userId)), Aggregation.group().first("senderId").as("senderId")
                        .sum("sendAmount").as("sendAmount"),
                Aggregation.project("sendAmount").andExclude("_id"));
        GiftLog totalGiftLog = mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class).getUniqueMappedResult();
        return totalGiftLog == null ? 0 : totalGiftLog.getSendAmount();
    }


    public PageInfo<GiftLog> findGiftLogByTimesAndReceiveUserIdAndName(PageRequest pageable, long startTime, long endTime, String liveUserId,String liveUserName,
                                                                       String roomId,Constant.PlatformType platform) {
        Criteria criteria = Criteria.where("createTime").gte(startTime).lte(endTime).and("platformType").is(platform);
        if(!StringUtils.isNullOrBlank(roomId)){
            Room room = roomDaoService.findByDisplayId(roomId);
            if(room == null) return new PageInfo<>(new ArrayList<>(0),pageable,0);
            criteria.and("receiveId").is(liveUserDaoService.findById(room.getLiveUserId()).getUserId());
        }
        if(!StringUtils.isNullOrBlank(liveUserId)){
            LiveUser liveUser = liveUserDaoService.findById(liveUserId);
            if(liveUser == null) return new PageInfo<>(new ArrayList<>(0),pageable,0);
            criteria.and("receiveId").is(liveUser.getUserId());
        }
        if(StringUtils.isNullOrBlank(liveUserId) && !StringUtils.isNullOrBlank(liveUserName) ){
            User user = userDaoService.findByDisplayName(liveUserName);
            if(user == null){
                return new PageInfo<>(new ArrayList<>(0),pageable,0);
            }
            criteria.and("receiveId").is(user.getId());
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"createTime"));
        return pageQuery(query, pageable);
    }

    private PageInfo<GiftLog> pageQuery(Query query,Pageable pageable ){
        long totalNum = mongoTemplate.count(query, GiftLog.class);
        List<GiftLog> giftLogs = mongoTemplate.find(query.with(pageable), GiftLog.class);
        return new PageInfo<>(giftLogs,pageable,totalNum);
    }

    public List<GiftLog> findBySenderIdAndPlatformType(String senderId, Constant.PlatformType platform,int size){
        Criteria criteria = Criteria.where("senderId").is(senderId).and("platformType").is(platform);
        Query query = Query.query(criteria).with(PageRequest.of(0, size)).with(Sort.by(Sort.Direction.DESC, "createTime"));
        return mongoTemplate.find(query, GiftLog.class);
    }

    public List<GiftLog> findByReceiverIdAndGroupByGift(String receiveId,long startTime, long endTime){
        Aggregation aggregation = Aggregation.newAggregation(GiftLog.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime).and("receiveId").is(receiveId)),
                Aggregation.group("$senderId", "$giftId").first("senderId").as("senderId")
                        .first("giftId").as("giftId")
                        .sum("sendAmount").as("sendAmount")
                        .sum("sendNum").as("sendNum"),
                Aggregation.project("senderId","giftId","sendAmount","sendNum").andExclude("_id")
        );
        return mongoTemplate.aggregate(aggregation, GiftLog.class, GiftLog.class).getMappedResults();
    }

    public List<GiftLog> findByPlatform(Constant.PlatformType platform){
        return giftLogRepository.findByPlatformType(platform);
    }

    public void deleteGiftLog(GiftLog giftLog){
        giftLogRepository.delete(giftLog);
    }

    public long countByPlatformAndTimes(Constant.PlatformType platform, long startTime, long endTime) {
        Criteria criteria = Criteria.where("platformType").is(platform).and("createTime").gte(startTime).lte(endTime);
        return mongoTemplate.count(Query.query(criteria), GiftLog.class);
    }

    public long countUserSendGift(String userId) {
        return giftLogRepository.countBySenderId(userId);
    }
}
