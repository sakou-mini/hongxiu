package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.account.TouristLoginLog;
import com.donglai.model.db.repository.live.TouristLoginLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Moon
 * @date 2021-12-22 14:03
 */
@Service
public class TouristLoginLogService {

    @Autowired
    private TouristLoginLogRepository touristLoginLogRepository;

    public TouristLoginLog save(TouristLoginLog touristLoginLog) {
        return touristLoginLogRepository.save(touristLoginLog);
    }

    public Long findByUserIdLoginCount(String id) {
        return touristLoginLogRepository.countAllByUserId(id);
    }
}
