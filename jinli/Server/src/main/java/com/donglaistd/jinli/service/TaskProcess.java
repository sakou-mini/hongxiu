package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.TaskBuilder;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.config.TaskConfig;
import com.donglaistd.jinli.config.TaskReward;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import com.donglaistd.jinli.event.ModifyUserResourceEvent;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

@Component
public class TaskProcess {
    private final Logger logger = Logger.getLogger(TaskProcess.class.getName());
    @Autowired
    TaskBuilder taskBuilder;
    @Autowired
    FollowListDaoService followListDaoService;
    @Autowired
    TaskDaoService taskDaoService;
    @Autowired
    LiveProcess liveProcess;

    @Transactional
    public void getTaskAward( Collection<TaskReward> rewards, String userId){
        for (TaskReward reward : rewards) {
            if(reward.getAmount() <= 0) continue;
            if(reward.getRewardType().equals(Constant.TaskRewardType.TASK_REWARD_EXPERIENCE)) {
                EventPublisher.publish(ModifyUserResourceEvent.newInstance(userId,reward.getAmount(), ModifyUserResourceEvent.ModifyType.exp));
            }
            if(reward.getRewardType().equals(Constant.TaskRewardType.TASK_REWARD_GOLDBEAN)) {
                EventPublisher.publish(ModifyUserResourceEvent.newInstance(userId,reward.getAmount(), ModifyUserResourceEvent.ModifyType.goldBean));
            }
            if(reward.getRewardType().equals(Constant.TaskRewardType.TASK_REWARD_GAMECOIN)){
                EventPublisher.publish(ModifyUserResourceEvent.newInstance(userId,reward.getAmount(), ModifyUserResourceEvent.ModifyType.gameCoin));
            }
        }
    }

    public void totalWatchLiveTime(User user, Room room){
        String userId = user.getId();
        UserRoomRecord userRoomRecord = DataManager.getUserRoomRecord(userId);
        long enterRoomRecordTime = userRoomRecord.getEnterRoomTime();
        if(enterRoomRecordTime <= 0) return;
        long quitRoomTime = System.currentTimeMillis();
        liveProcess.recordWatchLiveIfNotLiveUser(user,room,userRoomRecord);
        //watLive task
        long watchLiveMinutes = TimeUnit.MILLISECONDS.toMinutes(quitRoomTime - enterRoomRecordTime);
        EventPublisher.publish(TaskEvent.newInstance(userId, ConditionType.watchLive,watchLiveMinutes));
        DataManager.cleanUserRoomRecord(userId);
        //followe and watchLive task
        if(Objects.nonNull(followListDaoService.findByFollowerIdAndFollower(room.getLiveUserId(), userId))){
            EventPublisher.publish(TaskEvent.newInstance(userId, ConditionType.followAndWatchLive,watchLiveMinutes));
        }
    }

    //update UserTask statue
    public boolean updateUserTask(String userId){
        //1. updateSignWeekTask
        var weekTasks = taskDaoService.findWeeklyTasksByOwnerThisWeekAgo(userId);
        for (Task task : weekTasks) {
            task.setTaskStatus(Constant.TaskStatus.TASK_STATUS_CLOSED);
        }
        //2.updateDayTask
        List<Task> dailyTasks = taskDaoService.findDailyTasksByOwnerTodayAgo(userId);
        for (Task task : dailyTasks) {
            task.setTaskStatus(Constant.TaskStatus.TASK_STATUS_CLOSED);
        }
        dailyTasks.addAll(weekTasks);
        if(dailyTasks.isEmpty())
            return false;
        taskDaoService.saveAll(dailyTasks);
        return true;
    }

    public List<Task> getOrInitTask(String uerId){
        var dailyTasks = taskDaoService.findDailyTasksByOwnerToday(uerId);
        //1.dayTask
        if (dailyTasks == null || dailyTasks.isEmpty()) {
            dailyTasks = initTask(uerId, Constant.TaskType.TASK_DAILY, taskBuilder.getDailyTaskConfigMap().values());
            dailyTasks = taskDaoService.saveAll(dailyTasks);
        }
        List<Task> taskList = new ArrayList<>(dailyTasks);
        //2.signTask
        var weeklyTasks = taskDaoService.findWeeklyTasksByOwnerThisWeek(uerId);
        if (weeklyTasks == null || weeklyTasks.isEmpty()) {
            weeklyTasks = initTask(uerId, Constant.TaskType.TASK_SIGN_IN, taskBuilder.getSignInTaskConfigMap().values());
            weeklyTasks = taskDaoService.saveAll(weeklyTasks);
        }
        taskList.addAll(weeklyTasks);
        //3.permanentTask
        List<Task> permanentTasks= taskDaoService.findAllPermanentTasksByOwner(uerId);
        if (permanentTasks == null || permanentTasks.isEmpty()) {
            permanentTasks = initTask(uerId, Constant.TaskType.TASK_PERMANENT, taskBuilder.getPermanentTaskConfigMap().values());
            permanentTasks = taskDaoService.saveAll(permanentTasks);
        }
        taskList.addAll(permanentTasks);
        return taskList;
    }

    private List<Task> initTask(String userId, Constant.TaskType taskType, Collection<TaskConfig> taskConfigs){
        List<Task> initTasks = new ArrayList<>();
        taskConfigs.forEach(taskConfig -> initTasks.add( Task.newInstance(userId, taskType,taskConfig)));
        return initTasks;
    }

    public void broadTaskFinish(String userId, Collection<Jinli.TaskInfo> finishTasks) {
        if(finishTasks.isEmpty()) return;
        logger.info("task is finish" + finishTasks);
        Jinli.TaskFinishBroadcast.Builder builder = Jinli.TaskFinishBroadcast.newBuilder().addAllTaskInfo(finishTasks);
        sendMessage(userId,buildReply(builder));
    }
}
