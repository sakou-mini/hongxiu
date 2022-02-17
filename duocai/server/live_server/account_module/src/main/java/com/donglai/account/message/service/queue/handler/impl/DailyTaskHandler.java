package com.donglai.account.message.service.queue.handler.impl;

import com.donglai.account.message.service.queue.handler.TriggerHandler;
import com.donglai.account.process.DailyTaskProcess;
import com.donglai.account.process.QueueProcess;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DailyTaskHandler implements TriggerHandler {
    private static final int BEFORE_ONE_DAY = 1;

    @Autowired
    QueueExecuteService queueExecuteService;
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    DailyTaskProcess dailyTaskProcess;

    @Override
    public void deal(QueueExecute queueExecute) {
        log.info("deal DailyTask");
        queueExecuteService.del(queueExecute);
        long startTime = TimeUtil.getBeforeDayStartTime(BEFORE_ONE_DAY);
        long endTime = TimeUtil.getTimeDayEndTime(startTime);
        //每日服务数据统计
        dailyTaskProcess.totalDailyOfServerData(startTime, endTime);
        queueProcess.initDailyTask();
    }


}
