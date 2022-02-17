package com.donglai.model.db.service.account;

import com.donglai.model.db.entity.account.AwardLog;
import com.donglai.model.db.repository.live.AwardLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Moon
 * @date 2022-02-14 18:05
 */
@Service
public class AwardLogService {

    @Autowired
    private AwardLogRepository awardLogRepository;


    public AwardLog save(AwardLog awardLog) {
        return awardLogRepository.save(awardLog);
    }
}
