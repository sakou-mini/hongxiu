package com.donglai.statistics.message.services.queue.handler.impl;

import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import com.donglai.statistics.message.services.queue.handler.TriggerHandler;
import com.donglai.statistics.process.DailyTaskProcess;
import com.donglai.statistics.process.QueueProcess;
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
        log.info("deald DailyTask");
        queueExecuteService.del(queueExecute);
        long startTime = TimeUtil.getBeforeDayStartTime(BEFORE_ONE_DAY);
        long endTime = TimeUtil.getTimeDayEndTime(startTime);
        //TODO 1.每日任务统计
        //每日服务数据统计
        dailyTaskProcess.totalDailyOfServerData(startTime, endTime);
        queueProcess.initDailyTask();
    }


}
