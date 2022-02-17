package com.donglai.model.db.service.common.statistics;

import com.donglai.model.db.entity.common.statistics.UserOperationRecord;
import com.donglai.model.db.repository.common.statistics.UserOperationRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserOperationRecordService {
    @Autowired
    UserOperationRecordRepository userOperationRecordRepository;

    public UserOperationRecord findByUserId(String userId) {
        return userOperationRecordRepository.findById(userId).orElse(UserOperationRecord.newInstance(userId));
    }

    public UserOperationRecord save(UserOperationRecord userOperationRecord) {
        return userOperationRecordRepository.save(userOperationRecord);
    }

    public List<UserOperationRecord> findUserOperationRecordOrderByPublishBlogsTime(List<String> userIds) {
        return userOperationRecordRepository.findByUserIdInOrderByLastPublishBlogsTimeDesc(userIds);
    }
}
