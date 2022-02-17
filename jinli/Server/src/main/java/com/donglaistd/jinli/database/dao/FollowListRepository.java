package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FollowListRepository extends MongoRepository<FollowList, String> {
    List<FollowList> findByFollower(User user);

    List<FollowList> findByFollowee(LiveUser followee);

}
