package com.donglaistd.jinli.database.entity;

import org.bson.types.ObjectId;

import javax.persistence.Id;

public class QueueExecute {
    @Id
    private String id = ObjectId.get().toString();
    private long startTime;

    private long endTime;

    private int triggerType;
    /**
     * 额外附加属性
     */
    private String refId;

    public QueueExecute(long startTime, long endTime, int triggerType, String refId) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.triggerType = triggerType;
        this.refId = refId;
    }

    public static QueueExecute newInstance(long startTime, long endTime, int triggerType, String refId){
        return new QueueExecute(startTime, endTime, triggerType, refId);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(int triggerType) {
        this.triggerType = triggerType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}
