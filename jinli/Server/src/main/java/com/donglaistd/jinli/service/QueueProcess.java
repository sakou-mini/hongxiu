package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.dao.QueueExecuteDaoService;
import com.donglaistd.jinli.database.entity.QueueExecute;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.task.timewheel.TimerQueueProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglaistd.jinli.constant.QueueType.LIVE_LIMIT_AUTO_CLOSE;
import static com.donglaistd.jinli.constant.QueueType.SEND_ROLL_MESSAGE;

@Service
public class QueueProcess {
    @Autowired
    QueueExecuteDaoService queueExecuteDaoService;

    public QueueExecute addRollQueue( SystemMessageConfig systemMessageConfig){
        long startTime = System.currentTimeMillis();
        long endTime = startTime + systemMessageConfig.getRollIntervalTime();
        QueueExecute newQueue = QueueExecute.newInstance(System.currentTimeMillis(), endTime, SEND_ROLL_MESSAGE.getValue(), systemMessageConfig.getId());
        newQueue = queueExecuteDaoService.save(newQueue);
        TimerQueueProcess.addTask(newQueue.getId());
        return newQueue;
    }

    public QueueExecute addLiveLimitQueue(String liveUserId,long endTime){
        long startTime = System.currentTimeMillis();
        QueueExecute newQueue = QueueExecute.newInstance(startTime, endTime, LIVE_LIMIT_AUTO_CLOSE.getValue(), liveUserId);
        newQueue = queueExecuteDaoService.save(newQueue);
        TimerQueueProcess.addTask(newQueue.getId());
        return newQueue;
    }

    public void deleteLiveLimitQueue(String liveUserId){
        queueExecuteDaoService.deleteByRefIdAndTriggerType(LIVE_LIMIT_AUTO_CLOSE.getValue(),liveUserId);
    }

}
