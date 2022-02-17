package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2022-02-14 16:00
 */
public interface TaskRepository extends MongoRepository<Task, String> {
    /**
     *
     * @return 返回等级任务
     */
    List<Task> findByUserLevel(int userLevel);
}
