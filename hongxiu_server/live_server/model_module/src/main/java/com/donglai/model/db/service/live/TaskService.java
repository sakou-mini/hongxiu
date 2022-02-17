package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.Task;
import com.donglai.model.db.repository.live.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2022-02-14 16:02
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Task> findByUserLevel(int userLevel) {
        return taskRepository.findByUserLevel(userLevel);
    }
}
