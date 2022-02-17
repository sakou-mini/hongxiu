package com.donglaistd.jinli.service.queue;

import com.donglaistd.jinli.constant.QueueType;
import com.donglaistd.jinli.database.dao.QueueExecuteDaoService;
import com.donglaistd.jinli.database.entity.QueueExecute;
import com.donglaistd.jinli.service.ServerAvailabilityCheckService;
import com.donglaistd.jinli.task.timewheel.TimerQueueProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class QueueFinishProcess {
    public static final Logger logger = Logger.getLogger(QueueFinishProcess.class.getName());

    @Autowired
    QueueExecuteDaoService queueExecuteDaoService;
    @Autowired
    RollMessageHandler rollMessageHandler;
    @Autowired
    ServerAvailabilityCheckService serverAvailabilityCheckService;
    @Autowired
    LiveLimitListHandler liveLimitListHandler;

    public void process(String queueId){
        if(!serverAvailabilityCheckService.isActive()) return;
        QueueExecute queueExecute = queueExecuteDaoService.findById(queueId);
        if (queueExecute == null) {
            logger.info("队列已失效, queue id is " + queueId);
            return;
        }
        logger.info("队列的结束时间为：" + queueExecute.getEndTime());
        switch (QueueType.valueOf(queueExecute.getTriggerType())){
            case SEND_ROLL_MESSAGE:
                rollMessageHandler.deal(queueExecute);
                break;
            case LIVE_LIMIT_AUTO_CLOSE:
                liveLimitListHandler.deal(queueExecute);
                break;
        }
        queueExecuteDaoService.deleteQueue(queueExecute);
    }
}
