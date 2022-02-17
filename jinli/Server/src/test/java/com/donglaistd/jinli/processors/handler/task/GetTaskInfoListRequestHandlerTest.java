package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.TaskBuilder;
import com.donglaistd.jinli.config.TaskConfig;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.service.TaskProcess;
import com.donglaistd.jinli.util.TimeUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetTaskInfoListRequestHandlerTest extends BaseTest {

    @Autowired
    GetTaskInfoListRequestHandler getTaskInfoListRequestHandler;
    @Autowired
    TaskBuilder taskBuilder;
    @Autowired
    TaskDaoService taskDaoService;
    @Autowired
    TaskProcess taskProcess;

    @Test
    public void testGetTaskList(){
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.GetTaskInfoListRequest.newBuilder();
        requestWrapper.setGetTaskInfoListRequest(request);
        Jinli.JinliMessageReply reply = getTaskInfoListRequestHandler.doHandle(context, requestWrapper.build(), user);
        Jinli.GetTaskInfoListReply taskInfoListReply = reply.getGetTaskInfoListReply();
        long allTaskSize = taskBuilder.getPermanentTaskConfigMap().size() + taskBuilder.getDailyTaskConfigMap().size() + taskBuilder.getSignInTaskConfigMap().size();
        Assert.assertEquals(allTaskSize,taskInfoListReply.getTaskInfoCount());
        List<Task> tasks = taskDaoService.findWeeklyTasksByOwnerThisWeek(user.getId());
        Assert.assertEquals(7,tasks.size());
    }


    @Test
    public void updateExpiredTest(){
        //insert invalid task
        long l = TimeUtil.getFirstDayOfCurrentWeeks() - 1;
        LocalDateTime yesterdayTime = Instant.ofEpochMilli(TimeUtil.getBeforeDayStartTime(1)).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        LocalDateTime lastWeekTime = Instant.ofEpochMilli(TimeUtil.getFirstDayOfCurrentWeeks()).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        List<Task> tasks = new ArrayList<>();
        Task task;
        // dayTask
        Collection<TaskConfig> dayTaskConfigs = taskBuilder.getDailyTaskConfigMap().values();
        for (TaskConfig dayTaskConfig : dayTaskConfigs) {
            task = Task.newInstance(user.getId(), Constant.TaskType.TASK_DAILY, dayTaskConfig);
            task.setCreateDate(yesterdayTime);
            tasks.add(task);
        }
        //weekTask
        Collection<TaskConfig> signTaskConfigs = taskBuilder.getSignInTaskConfigMap().values();
        for (TaskConfig config : signTaskConfigs) {
            task = Task.newInstance(user.getId(), Constant.TaskType.TASK_SIGN_IN, config);
            task.setCreateDate(lastWeekTime);
            tasks.add(task);
        }
        Assert.assertEquals(12,tasks.size());
        taskDaoService.saveAll(tasks);

        //updateTask
        Assert.assertTrue(taskProcess.updateUserTask(user.getId()));

    }
}
