package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.TaskBuilder;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.config.TaskConfig;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.TaskProcess;
import com.donglaistd.jinli.util.DataManager;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class TaskEventListener implements EventListener {
    Logger logger = Logger.getLogger(TaskEventListener.class.getName());
    @Autowired
    TaskBuilder taskBuilder;
    @Autowired
    TaskDaoService taskDaoService;
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    DataManager dataManager;

    @Override
    public boolean handle(BaseEvent event) {
        TaskEvent taskEvent = (TaskEvent) event;
        User user = dataManager.findUser(taskEvent.getUserId());
        //not handler other platform user
        //if(user == null || user.isPlatformUser()) return true;
        List<TaskConfig> dailyTasksConfigs = taskBuilder.getTaskConfigByCondition(taskEvent.getConditionType(), Constant.TaskType.TASK_DAILY);
        List<TaskConfig> permanentTaskConfigs = taskBuilder.getTaskConfigByCondition(taskEvent.getConditionType(), Constant.TaskType.TASK_PERMANENT);

        List<Task> finishTasks = updateAndGetFinishPermanentTasks(permanentTaskConfigs, taskEvent);
        finishTasks.addAll(updateAndGetFinishDailyTasks(dailyTasksConfigs, taskEvent));
        taskProcess.broadTaskFinish(taskEvent.getUserId(), taskBuilder.toTaskInfo(finishTasks));
        return true;
    }

    private List<Task> updateAndGetFinishPermanentTasks(List<TaskConfig> permanentTaskConfigs, TaskEvent taskEvent) {
        List<Long> taskConfigIds = permanentTaskConfigs.stream().map(TaskConfig::getTaskId).collect(Collectors.toList());
        List<Task> tasks = taskDaoService.findActivePermanentTasksByOwnerAndTaskId(taskEvent.getUserId(), taskConfigIds);
        if(tasks.isEmpty()) {
            return Lists.newArrayList();
        }
        tasks.forEach(task ->  task.addTaskProgram(taskEvent.getProcess()));
        tasks = taskDaoService.saveAll(tasks);
        List<Task> finishTask = tasks.stream().filter(Task::isFinish).collect(Collectors.toList());
        if(finishTask.size() > 0){
            Task finalTask = updateFinishPermanentTask(taskEvent.getUserId(),finishTask.size());
            if(finalTask!=null && finalTask.isFinish()) finishTask.add(finalTask);
        }
        return finishTask;
    }

    private Task updateFinishPermanentTask(String userId,long finishCount){
        Task finalTask;
        TaskConfig finalTaskConfig = taskBuilder.getPermanentTaskByCondition(ConditionType.finishPermanentTask);
        List<Task> tasks = taskDaoService.findActivePermanentTasksByOwnerAndTaskId(userId, List.of(finalTaskConfig.getTaskId()));
        if(tasks.isEmpty()) return null;
        finalTask = tasks.get(0);
        finalTask.addTaskProgram(finishCount);
        taskDaoService.save(finalTask);
        if(finalTask.isFinish()){
            logger.warning("Finish allPermanent task");
        }
        return finalTask;
    }

    public List<Task> updateAndGetFinishDailyTasks( List<TaskConfig> dailyTasks, TaskEvent taskEvent) {
        List<Long> taskConfigIds = dailyTasks.stream().map(TaskConfig::getTaskId).collect(Collectors.toList());
        List<Task> tasks = taskDaoService.findActiveDailyTasksByOwnerAndTaskId(taskEvent.getUserId(), taskConfigIds);
        if(tasks.isEmpty()) return Lists.newArrayList();
        for (Task task : tasks) {
            task.addTaskProgram(taskEvent.getProcess());
        }
        return taskDaoService.saveAll(tasks).stream().filter(Task::isFinish).collect(Collectors.toList());
    }

    @Override
    public boolean isDisposable() {
        return false;
    }
}
