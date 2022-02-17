package com.donglai.account.message.service;

import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.account.AwardLog;
import com.donglai.model.db.entity.account.UserLevel;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Task;
import com.donglai.model.db.entity.live.TaskLog;
import com.donglai.model.db.repository.live.UserLevelRepository;
import com.donglai.model.db.service.account.AwardLogService;
import com.donglai.model.db.service.account.UserLevelService;
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
import java.util.Objects;

import static com.donglai.account.util.MessageUtil.buildReply;

/**
 * @author Moon
 * @date 2022-02-14 13:53
 */
@Service("AccountOfFinishTaskRequest")
public class AccountOfFinishTaskRequest_Service implements TopicMessageServiceI<String> {

    @Autowired
    private TaskLogService taskLogService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserLevelService userLevelService;

    @Autowired
    private UserService userService;

    @Autowired
    private AwardLogService awardLogService;


    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {

        Account.AccountOfFinishTaskRequest request = message.getAccountOfFinishTaskRequest();
        Account.AccountOfFinishTaskReply.Builder builder = Account.AccountOfFinishTaskReply.newBuilder();

        Constant.TaskType type = request.getType();

        User byId = userService.findById(userId);

        //等级是否有这个任务
        List<Task> byUserLevel = taskService.findByUserLevel(byId.getLevel());
        boolean isExist = byUserLevel.stream().anyMatch(v -> v.getTaskEnum().equals(type));
        if (!isExist) {
            return buildReply(userId, builder.build(), Constant.ResultCode.SUCCESS);
        }
        //今天的任务列表是否已创建
        long currentDayStartTime = TimeUtil.getCurrentDayStartTime();
        TaskLog byTaskEnumAndTaskDate = taskLogService.findByTaskEnumAndTaskDate(type, currentDayStartTime);
        if (Objects.isNull(byTaskEnumAndTaskDate)) {
            //初始化
            for (Task task : byUserLevel) {
                TaskLog taskLog = new TaskLog();
                taskLog.setTaskEnum(task.getTaskEnum());
                taskLog.setTaskCount(task.getTaskCount());
                taskLog.setTaskSchedule(0);
                taskLog.setTaskIntegral(task.getTaskIntegral());
                taskLog.setTaskGold(task.getTaskGold());
                taskLog.setTaskDate(currentDayStartTime);
                taskLog.setUserId(userId);
            }
        }
        //是否已经完成
        byTaskEnumAndTaskDate = taskLogService.findByTaskEnumAndTaskDate(type, currentDayStartTime);
        //未完成 进
        if (!byTaskEnumAndTaskDate.isDone()) {
            int num = byTaskEnumAndTaskDate.getTaskSchedule() + 1;
            byTaskEnumAndTaskDate.setTaskSchedule(num);
            //完成任务
            if (num == byTaskEnumAndTaskDate.getTaskCount()) {
                byTaskEnumAndTaskDate.setDone(true);
                //发放积分奖励和添加积分明细
                byId.setIntegral(byId.getIntegral() + byTaskEnumAndTaskDate.getTaskIntegral());
                AwardLog awardLog = AwardLog.newInstance(0, byTaskEnumAndTaskDate.getTaskIntegral(), byId.getId(), System.currentTimeMillis());
                awardLogService.save(awardLog);

                //金币增加
                AwardLog awardGoldLog = AwardLog.newInstance(1, byTaskEnumAndTaskDate.getTaskGold(), byId.getId(), System.currentTimeMillis());
                awardLogService.save(awardGoldLog);

                //等级增加
                int level = byId.getLevel();
                while(true){
                    level++;
                    UserLevel byLevel = userLevelService.findByLevel(level);
                    if(Objects.nonNull(byLevel)){
                        int levelIntegral = byLevel.getLevelIntegral();
                        if(levelIntegral <= byId.getIntegral()){
                            byId.setLevel(level);
                        }else{
                            return buildReply(userId, builder.build(), Constant.ResultCode.SUCCESS);
                        }
                    }
                }
            }
        }
        return buildReply(userId, builder.build(), Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
