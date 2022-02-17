package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.TaskReward;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.TaskProcess;
import com.google.protobuf.ProtocolStringList;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class CollectTaskRewardRequestHandler extends MessageHandler {
    @Autowired
    TaskDaoService taskDaoService;
    @Autowired
    TaskProcess taskProcess;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.CollectTaskRewardRequest request = messageRequest.getCollectTaskRewardRequest();
        Jinli.CollectTaskRewardReply.Builder reply = Jinli.CollectTaskRewardReply.newBuilder();
        if(user.isPlatformUser())  return buildReply(reply, FUNCTION_NOT_OPEN);
        ProtocolStringList ids = request.getIdList();
        List<Task> taskList = taskDaoService.findAllByIds(ids);
        Constant.ResultCode resultCode = checkTaskStatue(taskList);
        if(!SUCCESS.equals(resultCode)){
            return buildReply(reply, ALREADY_SIGN_ALL_DAY);
        }
        taskList.forEach(task -> {
            task.setCollected(true);
            task.setTaskStatus(Constant.TaskStatus.TASK_STATUS_FINISHED);
        });
        taskDaoService.saveAll(taskList);
        Collection<TaskReward> rewards = totalTaskReward(taskList);
        taskProcess.getTaskAward(rewards,user.getId());
        taskList.forEach(task -> reply.addTaskInfo(task.toProto()));
        return buildReply(reply, SUCCESS);
    }

    private Constant.ResultCode checkTaskStatue(List<Task> taskList){
        if (taskList.stream().anyMatch(task -> !task.getTaskStatus().equals(Constant.TaskStatus.TASK_STATUS_FINISHED)))
            return TASK_NOT_FINISH;
        if(taskList.stream().anyMatch(Task::isCollected))
            return TASK_ALREADY_COLLECTION;
        return SUCCESS;
    }

    private Collection<TaskReward> totalTaskReward(List<Task> taskList){
        List<TaskReward> taskRewards = new ArrayList<>();
        taskList.forEach(task -> taskRewards.addAll(task.getRewards()));
        return taskRewards.stream().collect(Collectors.groupingBy(TaskReward::getRewardType, Collectors.summingLong(TaskReward::getAmount)))
                .entrySet().stream().map(entry -> TaskReward.newInstance(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
