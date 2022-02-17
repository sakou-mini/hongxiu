package com.donglai.model.db.repository.account;

import com.donglai.model.db.entity.account.UserFeedback;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserFeedbackRepository extends MongoRepository<UserFeedback, Long> {
}
