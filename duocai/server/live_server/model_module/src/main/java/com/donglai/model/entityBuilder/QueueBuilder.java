package com.donglai.model.entityBuilder;

import com.donglai.common.constant.QueueType;
import com.donglai.model.db.entity.common.QueueExecute;
import com.donglai.model.db.service.common.QueueExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QueueBuilder {
    @Autowired
    QueueExecuteService queueExecuteService;

    public static QueueExecute createQueue(long endTime, int triggerType, String refId,String fromServer) {
        QueueExecute queueExecute = new QueueExecute(System.currentTimeMillis(), endTime, triggerType, refId, null);
        queueExecute.setFromServer(fromServer);
        return queueExecute;
    }

    public static QueueExecute createQueue(long endTime, QueueType queueType, String refId,String fromServer) {
        return createQueue(endTime, queueType.getValue(), refId,fromServer);
    }

    public static QueueExecute createQueue(String userId, String refId, long endTime, QueueType queueType,String fromServer) {
        QueueExecute queueExecute = new QueueExecute(userId, System.currentTimeMillis(), endTime, queueType.getValue(), refId, null);
        queueExecute.setFromServer(fromServer);
        return queueExecute;
    }
}
