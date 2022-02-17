package com.donglaistd.jinli.service.queue;


import com.donglaistd.jinli.database.entity.QueueExecute;

public interface TriggerHandler {
    void deal(QueueExecute queueExecute);
}
