package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.TaskReward;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.TaskProcess;
import com.donglaistd.jinli.util.TimeUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
@Component
public class SignWeekTaskRequestHandler extends MessageHandler {

    @Autowired
    private TaskDaoService taskDaoService;
    @Autowired
    TaskProcess taskProcess;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SignWeekTaskReply.Builder signReply = Jinli.SignWeekTaskReply.newBuilder();
        List<Task> tasks = taskDaoService.findWeeklyTasksByOwnerThisWeek(user.getId());
        if(user.isPlatformUser())  return buildReply(signReply, FUNCTION_NOT_OPEN);
        if(tasks.stream().allMatch(Task::isFinish)){
            return buildReply(signReply, ALREADY_SIGN_ALL_DAY);
        }
        if(tasks.stream().anyMatch(task -> TimeUtil.getTimeDayStartTime(task.getFinishTime()) == TimeUtil.getCurrentDayStartTime()))
            return buildReply(signReply, ALREADY_SIGN_TODAY);
        Task task = tasks.stream().filter(tempTask -> !tempTask.isFinish()).findFirst().orElse(null);
        task.setTaskStatus(Constant.TaskStatus.TASK_STATUS_FINISHED);
        task.setFinishTime(System.currentTimeMillis());
        task.setCollected(true);
        taskProcess.getTaskAward(task.getRewards(),user.getId());
        taskDaoService.save(task);
        for (TaskReward reward : task.getRewards()) {
            signReply.addReward(reward.toProto());
        }
        taskProcess.broadTaskFinish(user.getId(),List.of(task.toProto()));
        return buildReply(signReply, SUCCESS);
    }

}
