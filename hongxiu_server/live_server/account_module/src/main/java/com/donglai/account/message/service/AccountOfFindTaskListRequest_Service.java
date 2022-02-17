package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Task;
import com.donglai.model.db.entity.live.TaskLog;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.TaskLogService;
import com.donglai.model.db.service.live.TaskService;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2022-02-16 14:29
 */
@Service("AccountOfFindTaskListRequest")
public class AccountOfFindTaskListRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private TaskLogService taskLogService;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Account.AccountOfFindTaskListReply.Builder builder = Account.AccountOfFindTaskListReply.newBuilder();


        User byId = userService.findById(userId);

        List<Task> byUserLevel = taskService.findByUserLevel(byId.getLevel());
        List<Constant.TaskType> taskEnums = byUserLevel.stream().map(Task::getTaskEnum).collect(Collectors.toList());

        long time = TimeUtil.getCurrentDayStartTime();
        List<TaskLog> byUserIdAndTaskDate = taskLogService.findByUserIdAndTaskDate(userId, time).stream()
                .filter(v -> taskEnums.contains(v.getTaskEnum())).collect(Collectors.toList());

        for (TaskLog taskLog : byUserIdAndTaskDate) {
            builder.addTaskLog(taskLog.toProto());
        }
        return buildReply(userId, builder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
