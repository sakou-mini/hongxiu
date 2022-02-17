package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.account.UserLevel;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Moon
 * @date 2022-02-14 18:18
 */
public interface UserLevelRepository extends MongoRepository<UserLevel, String> {
    UserLevel findByLevel(int level);
}
