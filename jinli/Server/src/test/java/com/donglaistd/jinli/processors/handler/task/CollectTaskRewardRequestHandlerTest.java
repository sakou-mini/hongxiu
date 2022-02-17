package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.TaskDaoService;
import com.donglaistd.jinli.database.entity.Task;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.TaskProcess;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CollectTaskRewardRequestHandlerTest extends BaseTest {
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    TaskDaoService taskDaoService;
    @Autowired
    CollectTaskRewardRequestHandler collectTaskRewardRequestHandler;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        taskProcess.getOrInitTask(user.getId());
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test
    public void collectTaskRewardTest(){
        List<Task> tasks = taskDaoService.findDailyTasksByOwnerToday(user.getId());
        Task task = tasks.get(0);
        task.addTaskProgram(1);
        taskDaoService.save(task);
        Assert.assertEquals(Constant.TaskStatus.TASK_STATUS_FINISHED, task.getTaskStatus());
        Assert.assertFalse(task.isCollected());

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.CollectTaskRewardRequest.newBuilder().addId(task.getId());
        requestWrapper.setCollectTaskRewardRequest(request);
        Jinli.JinliMessageReply messageReply = collectTaskRewardRequestHandler.doHandle(context, requestWrapper.build(), user);
        Assert.assertEquals(Constant.ResultCode.SUCCESS, messageReply.getResultCode());
        Jinli.CollectTaskRewardReply reply = messageReply.getCollectTaskRewardReply();
        List<Jinli.TaskInfo> taskInfoList = reply.getTaskInfoList();
        Assert.assertEquals(1,taskInfoList.size());
        User user = dataManager.findUser(this.user.getId());
        Assert.assertEquals(2, user.getGoldBean());
    }

}
