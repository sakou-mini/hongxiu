package com.donglai.account.message.service.queue.handler;

import com.donglai.model.db.entity.common.QueueExecute;

public interface TriggerHandler {
    void deal(QueueExecute queueExecute);
}
