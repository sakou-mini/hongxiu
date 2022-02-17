package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.UserFeedback;
import com.donglai.model.db.repository.account.UserFeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFeedbackService {
    @Autowired
    UserFeedbackRepository userFeedbackRepository;

    public UserFeedback save(UserFeedback userFeedback) {
        return userFeedbackRepository.save(userFeedback);
    }
}
