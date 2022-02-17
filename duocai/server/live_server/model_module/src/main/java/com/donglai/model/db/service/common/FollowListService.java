package com.donglai.model.db.service.common;

import com.donglai.model.db.entity.common.FollowList;
import com.donglai.model.db.repository.common.FollowListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class FollowListService {
    @Autowired
    FollowListRepository repository;

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

}

