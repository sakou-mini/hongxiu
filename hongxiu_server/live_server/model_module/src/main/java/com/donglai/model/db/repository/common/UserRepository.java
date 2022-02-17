package com.donglai.model.db.repository.common;

import com.donglai.model.db.entity.common.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;


public interface UserRepository extends MongoRepository<User, String> {
    User findByAccountId(String accountName);

    User findByLiveUserId(String liveUserId);

    List<User> findByNickname(String nickname);

    List<User> findAllByIdIn(List<String> ids);

    User findByOtherId(String otherId);

    User findByUuid(String uuid);

    User findByPhoneNumber(String phoneNumber);

    List<User> findByNicknameLike(String keyword);

    List<User> findByIdInAndNicknameLike(Set<String> leaderIds, String name);

    long countByCreateTimeLessThanEqualAndTouristIs(long time, boolean tourist);

    List<User> findByTouristIs(boolean b);
}
