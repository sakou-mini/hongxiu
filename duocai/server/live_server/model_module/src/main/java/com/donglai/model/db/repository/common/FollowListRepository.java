package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.FollowList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FollowListRepository extends MongoRepository<FollowList, String> {
    List<FollowList> findByFollowerIdOrderByFollowTimeDesc(String userId);

    long countByLeaderId(String userId);

    long countByFollowerId(String userId);

    List<FollowList> findByLeaderIdOrderByFollowTimeAsc(String leaderId);

    FollowList findByLeaderIdAndFollowerId(String leaderId, String followerId);

    void deleteByLeaderIdAndFollowerId(String leaderId, String followerId);
}
