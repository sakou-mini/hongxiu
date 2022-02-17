package com.donglaistd.jinli.processors.handler.task;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.TaskBuilder;
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

public class SignWeekTaskRequestHandlerTest extends BaseTest {
    @Autowired
    SignWeekTaskRequestHandler signWeekTaskRequestHandler;
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    TaskBuilder taskBuilder;
    @Autowired
    TaskDaoService taskDaoService;

    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        taskProcess.getOrInitTask(user.getId());
        EventPublisher.isEnabled = new AtomicBoolean(true);
    }

    @Test
    public void signTaskSuccessTest(){
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.SignWeekTaskRequest.newBuilder();
        requestWrapper.setSignWeekTaskRequest(request);
        Jinli.JinliMessageReply reply = signWeekTaskRequestHandler.doHandle(context, requestWrapper.build(), user);
        Jinli.SignWeekTaskReply signWeekTaskReply = reply.getSignWeekTaskReply();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,reply.getResultCode());
        List<Jinli.TaskReward> rewardList = signWeekTaskReply.getRewardList();
        //TaskConfig firstSignDay = taskBuilder.getSignInTaskConfigMap().get("1");
        Assert.assertEquals(1,rewardList.size());
        Assert.assertEquals(1,rewardList.get(0).getAmount());
        Assert.assertEquals(Constant.TaskRewardType.TASK_REWARD_GOLDBEAN,rewardList.get(0).getRewardType());
        User user = dataManager.findUser(this.user.getId());
        Assert.assertEquals(25,user.getGoldBean());

        //second Sign today
        reply = signWeekTaskRequestHandler.doHandle(context, requestWrapper.build(), user);
        Assert.assertEquals(Constant.ResultCode.ALREADY_SIGN_TODAY,reply.getResultCode());

        List<Task> signTasks = taskDaoService.findWeeklyTasksByOwnerThisWeek(user.getId());
        long signCount = signTasks.stream().filter(Task::isFinish).count();
        Assert.assertEquals(1,signCount);
    }
}
