package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.ModifyNicknameRecord;
import com.donglai.model.db.repository.live.ModifyNicknameRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModifyNicknameRecordService {
    @Autowired
    ModifyNicknameRecordRepository recordRepository;

    public ModifyNicknameRecord save(ModifyNicknameRecord record) {
        return recordRepository.save(record);
    }

    public long countModifyPasswordNumByTimes(String userId, long startTime, long endTime) {
        return recordRepository.countByUserIdAndRecordTimeBetween(userId, startTime, endTime);
    }
}
