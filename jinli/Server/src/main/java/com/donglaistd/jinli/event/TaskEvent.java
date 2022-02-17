package com.donglaistd.jinli.event;

import com.donglaistd.jinli.config.ConditionType;

public class TaskEvent implements BaseEvent{
    private String userId;
    private ConditionType conditionType;
    private long process;

    public ConditionType getConditionType() {
        return conditionType;
    }

    public long getProcess() {
        return process;
    }

    public String getUserId() {
        return userId;
    }

    private TaskEvent(String userId, ConditionType conditionType, long process) {
        this.userId = userId;
        this.conditionType = conditionType;
        this.process = process;
    }
    public static TaskEvent newInstance(String userId, ConditionType conditionType, long process){
        return new TaskEvent(userId, conditionType, process);
    }
}
