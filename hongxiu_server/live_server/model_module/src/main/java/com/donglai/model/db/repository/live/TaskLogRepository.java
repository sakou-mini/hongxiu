package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.entity.live.TaskLog;
import com.donglai.protocol.Constant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2022-02-14 16:01
 */
public interface TaskLogRepository extends MongoRepository<TaskLog, String> {

    TaskLog findByTaskEnumAndTaskDate(Constant.TaskType taskEnum, long currentDayStartTime);

    List<TaskLog> findByUserIdAndTaskDate(String userId, long time);
}
