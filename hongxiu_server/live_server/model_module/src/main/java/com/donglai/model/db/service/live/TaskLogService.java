package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.TaskLog;
import com.donglai.model.db.repository.live.TaskLogRepository;
import com.donglai.protocol.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2022-02-14 16:02
 */
@Service
public class TaskLogService {
    @Autowired
    private TaskLogRepository taskLogRepository;


    public TaskLog findByTaskEnumAndTaskDate(Constant.TaskType taskEnum, long currentDayStartTime) {
        return taskLogRepository.findByTaskEnumAndTaskDate(taskEnum, currentDayStartTime);
    }

    public List<TaskLog> findByUserIdAndTaskDate(String userId, long time) {
        return taskLogRepository.findByUserIdAndTaskDate(userId,time);
    }
}
