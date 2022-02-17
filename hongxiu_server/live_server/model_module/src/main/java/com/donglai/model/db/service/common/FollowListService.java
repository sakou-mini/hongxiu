package com.donglai.model.db.service.common;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.repository.common.FollowListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class FollowListService {
    @Autowired
    FollowListRepository repository;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    MongoOperations mongoOperations;

    public FollowList save(FollowList likeList) {
        return repository.save(likeList);
    }

    //查询玩家的粉丝
    public List<FollowList> findUserFollowers(String leaderId) {
        return repository.findByLeaderIdOrderByFollowTimeAsc(leaderId);
    }

    //查询玩家关注的人
    public List<FollowList> findUserLeaders(String userId) {
        return repository.findByFollowerIdOrderByFollowTimeDesc(userId);
    }

    public long countFollowerNumByUserId(String leaderId) {
        return repository.countByLeaderId(leaderId);
    }

    public long countLeaderNumByUserId(String followerId) {
        return repository.countByFollowerId(followerId);
    }

    public FollowList findByLeaderIdAndFollowerId(String leaderId, String followerId) {
        return repository.findByLeaderIdAndFollowerId(leaderId, followerId);
    }

    public boolean isUserFollower(String leaderId, String followerId) {
        return Objects.nonNull(findByLeaderIdAndFollowerId(leaderId, followerId));
    }

    public void deleteFollowListByLeaderIdAndFollowerId(String leaderId, String followerId) {
        repository.deleteByLeaderIdAndFollowerId(leaderId, followerId);
    }

    public List<FollowList> findByFollowerAndLikeName(String queryUserId, String name) {
        return repository.findByLeaderIdAndAliasLikeOrderByFollowTimeDesc(queryUserId, name);
    }

    public List<FollowList> findUserFollowerAndFollowerName(String leaderId, String followerName) {
        LookupOperation userLookup = LookupOperation.newLookup().from("user").localField("followerId").foreignField("_id").as("user");
        Criteria criteria = Criteria.where("leaderId").is(leaderId);
        if (!StringUtils.isNullOrBlank(followerName)) {
            Pattern pattern = Pattern.compile(String.format("^%s.*$", StringUtils.escapeExprSpecialWord(followerName)), Pattern.CASE_INSENSITIVE);
            criteria.and("user.nickname").regex(pattern);
        }
        Aggregation agg = Aggregation.newAggregation(userLookup, Aggregation.match(criteria), Aggregation.sort(Sort.by(Sort.Direction.DESC, "followTime")));
        return mongoTemplate.aggregate(agg, FollowList.class, FollowList.class).getMappedResults();
    }

    public List<FollowList> findByFollowerIdAndLeaderName(String userId, String name) {
        LookupOperation userLookup = LookupOperation.newLookup().from("user").localField("leaderId").foreignField("_id").as("user");
        Criteria criteria = Criteria.where("followerId").is(userId);
        if (!StringUtils.isNullOrBlank(name)) {
            Pattern pattern = Pattern.compile(String.format("^%s.*$", StringUtils.escapeExprSpecialWord(name)), Pattern.CASE_INSENSITIVE);
            criteria.orOperator(Criteria.where("alias").regex(pattern), Criteria.where("user.nickname").regex(pattern));
        }
        Aggregation agg = Aggregation.newAggregation(userLookup, Aggregation.match(criteria), Aggregation.sort(Sort.by(Sort.Direction.DESC, "followTime")));
        return mongoTemplate.aggregate(agg, FollowList.class, FollowList.class).getMappedResults();
    }

    public void updateUserFollowersNewTagByLeaderId(String userId) {
        mongoOperations.updateMulti(new Query(Criteria.where("leaderId").is(userId)), new Update().set("isNew", true), FollowList.class);
    }

}

