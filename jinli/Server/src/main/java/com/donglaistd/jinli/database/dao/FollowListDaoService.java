package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.mongodb.DBRef;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class FollowListDaoService {
    @Autowired
    private final FollowListRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    public FollowListDaoService(FollowListRepository repository) {
        this.repository = repository;
    }

    public FollowList save(FollowList likeList) {
        Assert.notNull(likeList, "User must not be null");
        Assert.notNull(likeList.getFollower(), "follower must not be null");
        Assert.notNull(likeList.getFollowee(), "followee must not be null");
        return repository.save(likeList);
    }

    public List<FollowList> findAllByFollower(User user) {
        return repository.findByFollower(user);
    }

    public int fetchHotValueByLiveUser(LiveUser followee) {
        var followerCount = repository.findByFollowee(followee);
        return followerCount.size();
    }

    protected void deleteAll() {
        this.repository.deleteAll();
    }

    public List<String> findFolloweeIdsByFollowerId(String followerId) {
        Aggregation aggregation = Aggregation.newAggregation(FollowList.class, Aggregation.match(Criteria.where("followee.$id").is(followerId)), Aggregation.project("follower"));
        AggregationResults<FollowList> aggregate = mongoTemplate.aggregate(aggregation, FollowList.class, FollowList.class);
        return aggregate.getMappedResults().stream().map(followList -> followList.getFollower().getId()).collect(Collectors.toList());
    }

    public void delete(FollowList followList) {
        repository.delete(followList);
    }

    public FollowList findByFollowerIdAndFollower(String liveUserId,String userId){
        Criteria criteria = new Criteria().andOperator(Criteria.where("followee.$id").is(liveUserId), Criteria.where("follower.$id").is(userId));
        Aggregation aggregation = Aggregation.newAggregation(FollowList.class, Aggregation.match(criteria), Aggregation.project("follower").andExclude("_id"));
        AggregationResults<FollowList> results = mongoTemplate.aggregate(aggregation, FollowList.class, FollowList.class);
        return results.getUniqueMappedResult();
    }

    public Map<String,Integer> countLiveUserFansNumByTimes(long startTime, long endTime) {
        Criteria criteria = Criteria.where("followTime").gte(new Date(startTime)).lte(new Date(endTime));
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria),
                Aggregation.group("$followee").first("followee").as("followee").count().as("fansNum"),
                Aggregation.project("fansNum").and("followee").as("liveUser").andExclude("_id"));
        List<Document> mappedResults = mongoTemplate.aggregate(aggregation, FollowList.class, Document.class).getMappedResults();
        return mappedResults.stream().collect(Collectors.toMap(document -> document.get("liveUser",DBRef.class).getId().toString(),document -> document.getInteger("fansNum")));
    }

    public Map<Constant.PlatformType, List<FollowList>>  groupFolloweeByPlatform(LiveUser liveUser){
        var followerList = repository.findByFollowee(liveUser);
        return followerList.stream().collect(Collectors.groupingBy(follower -> follower.getFollower().getPlatformType()));
    }
}