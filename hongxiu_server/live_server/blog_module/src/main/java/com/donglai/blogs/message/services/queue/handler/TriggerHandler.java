package com.donglai.blogs.message.services.queue.handler;

import com.donglai.model.db.entity.common.QueueExecute;

public interface TriggerHandler {
    void deal(QueueExecute queueExecute);
}
