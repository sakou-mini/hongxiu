package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.Task;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.TaskStatus.*;

public class TaskDaoServiceTest extends BaseTest {

    @Autowired
    private TaskDaoService taskDaoService;

    @Test
    public void QueryDailyTaskTest() {
        LocalDateTime now = LocalDateTime.now();
        var task1 = createTask("1", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_DAILY);
        var task2 = createTask("2", TASK_STATUS_FINISHED, now, Constant.TaskType.TASK_DAILY);
        var task3 = createTask("3", TASK_STATUS_ACTIVE, now.minusDays(1), Constant.TaskType.TASK_DAILY);
        var task4 = createTask("4", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_SIGN_IN);
        var task5 = createTask("5", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_PERMANENT);
        var task6 = createTask("6", TASK_STATUS_CLOSED, now, Constant.TaskType.TASK_PERMANENT);
        taskDaoService.saveAll(Arrays.asList(task1, task2, task3, task4, task5, task6));
        var tasks = taskDaoService.findDailyTasksByOwnerToday(user.getId());
        Assert.assertEquals(2, tasks.size());
        var taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
        Assert.assertTrue(taskIds.contains("1"));
        Assert.assertTrue(taskIds.contains("2"));
    }

    @Test
    public void QueryWeeklyTaskTest() {
        LocalDateTime now = LocalDateTime.now().withHour(1);
        var task1 = createTask("1", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_SIGN_IN);
        var task2 = createTask("2", TASK_STATUS_FINISHED, now, Constant.TaskType.TASK_SIGN_IN);
        var task3 = createTask("3", TASK_STATUS_ACTIVE, now.minusDays(7), Constant.TaskType.TASK_SIGN_IN);
        var task4 = createTask("4", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_DAILY);
        var task5 = createTask("5", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_PERMANENT);
        var task6 = createTask("6", TASK_STATUS_ACTIVE, now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(0), Constant.TaskType.TASK_SIGN_IN);
        var task7 = createTask("7", TASK_STATUS_ACTIVE, now.minusDays(now.getDayOfWeek().getValue() - 1).withHour(1), Constant.TaskType.TASK_SIGN_IN);
        taskDaoService.saveAll(Arrays.asList(task1, task2, task3, task4, task5, task6, task7));
        var tasks = taskDaoService.findWeeklyTasksByOwnerThisWeek(user.getId());
        Assert.assertEquals(3, tasks.size());
        var taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
        Assert.assertTrue(taskIds.contains("1"));
        Assert.assertTrue(taskIds.contains("2"));
        Assert.assertTrue(taskIds.contains("7"));
    }

    @Test
    public void QueryPermanentTaskTest() {
        LocalDateTime now = LocalDateTime.now().withHour(1);
        var task1 = createTask("1", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_PERMANENT);
        var task2 = createTask("2", TASK_STATUS_FINISHED, now, Constant.TaskType.TASK_PERMANENT);
        var task3 = createTask("3", TASK_STATUS_CLOSED, now.minusDays(7), Constant.TaskType.TASK_PERMANENT);
        var task4 = createTask("4", TASK_STATUS_ACTIVE, now, Constant.TaskType.TASK_DAILY);
        taskDaoService.saveAll(Arrays.asList(task1, task2, task3, task4));
        var tasks = taskDaoService.findAllPermanentTasksByOwner(user.getId());
        Assert.assertEquals(2, tasks.size());
    }

    private Task createTask(String id, Constant.TaskStatus isActive, LocalDateTime date, Constant.TaskType taskType) {
        var task = new Task();
        task.setId(id);
        task.setTaskStatus(isActive);
        task.setCreateDate(date);
        task.setOwnerId(user.getId());
        task.setTaskType(taskType);
        return task;
    }
}
