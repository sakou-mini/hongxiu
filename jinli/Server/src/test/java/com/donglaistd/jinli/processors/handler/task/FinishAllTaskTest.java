package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.TaskBuilder;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.config.TaskConfig;
import com.donglaistd.jinli.config.TaskReward;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.TaskProcess;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FinishAllTaskTest extends BaseTest {
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    TaskBuilder taskBuilder;
    @Autowired
    TaskDaoService taskDaoService;
    @Autowired
    CollectTaskRewardRequestHandler collectTaskRewardRequestHandler;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        EventPublisher.isEnabled = new AtomicBoolean(true);
        taskProcess.getOrInitTask(user.getId());
    }

    @Test
    public void finishAllPermanentTestAndCollectedReward(){
        Collection<TaskConfig> taskConfigs = taskBuilder.getPermanentTaskConfigMap().values();
        for (TaskConfig taskConfig : taskConfigs) {
            if (!ConditionType.finishPermanentTask.equals(taskConfig.getCondition().getConditionType())) {
                EventPublisher.publish(TaskEvent.newInstance(user.getId(), taskConfig.getCondition().getConditionType(), taskConfig.getCondition().getAmount()));
            }
        }

        List<Task> permanentTasks = taskDaoService.findAllPermanentTasksByOwner(user.getId());
        long count = permanentTasks.stream().filter(Task::isFinish).count();
        Assert.assertEquals(taskConfigs.size(),count);

        //1.Collect Award
        List<String> ids = permanentTasks.stream().map(Task::getId).collect(Collectors.toList());
        Jinli.JinliMessageRequest.Builder request = Jinli.JinliMessageRequest.newBuilder()
                .setCollectTaskRewardRequest(Jinli.CollectTaskRewardRequest.newBuilder().addAllId(ids));
        Jinli.JinliMessageReply reply = collectTaskRewardRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,reply.getResultCode());
        List<Jinli.TaskInfo> taskInfoList = reply.getCollectTaskRewardReply().getTaskInfoList();

        Collection<TaskReward> rewardList = totalTaskReward(taskInfoList);
        Map<Constant.TaskRewardType, Long> award = rewardList.stream().collect(Collectors.toMap(TaskReward::getRewardType, TaskReward::getAmount));


        Assert.assertEquals(1275,award.get(Constant.TaskRewardType.TASK_REWARD_GOLDBEAN).longValue());
        Assert.assertEquals(1037,award.get(Constant.TaskRewardType.TASK_REWARD_EXPERIENCE).longValue());
        Assert.assertEquals(16,award.get(Constant.TaskRewardType.TASK_REWARD_GAMECOIN).longValue());
        user = dataManager.findUser(this.user.getId());
        Assert.assertEquals(1275,user.getGoldBean());
        Assert.assertEquals(37,user.getExperience());
        Assert.assertEquals(6,user.getLevel());
        Assert.assertEquals(16,user.getGameCoin());
    }

    private Collection<TaskReward> totalTaskReward(List<Jinli.TaskInfo> taskList){
        List<String> ids = taskList.stream().map(Jinli.TaskInfo::getId).collect(Collectors.toList());
        List<Task> tasks = taskDaoService.findAllByIds(ids);
        List<TaskReward> taskRewards = new ArrayList<>();
        tasks.forEach(task -> taskRewards.addAll(task.getRewards()));
        return taskRewards.stream().collect(Collectors.groupingBy(TaskReward::getRewardType, Collectors.summingLong(TaskReward::getAmount)))
                .entrySet().stream().map(entry -> TaskReward.newInstance(entry.getKey(), entry.getValue())).collect(Collectors.toList());
    }
}
