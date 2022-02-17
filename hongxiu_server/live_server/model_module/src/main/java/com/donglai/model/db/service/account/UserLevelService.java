package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.UserLevel;
import com.donglai.model.db.repository.live.UserLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Moon
 * @date 2022-02-14 18:17
 */
@Service
public class UserLevelService {
    @Autowired
    private UserLevelRepository UserLevelRepository;

    public UserLevel findByLevel(int level) {
        return UserLevelRepository.findByLevel(level);
    }
}
