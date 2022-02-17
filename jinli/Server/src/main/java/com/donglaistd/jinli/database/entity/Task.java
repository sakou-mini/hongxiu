package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.TaskConfig;
import com.donglaistd.jinli.config.TaskReward;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;


@Document
public class Task {
    @Id
    private String id = ObjectId.get().toString();
    @Field
    @Indexed
    private LocalDateTime createDate;
    @Field
    @Indexed
    private Constant.TaskStatus taskStatus;
    @Field
    private List<TaskReward> rewards;
    @Field
    private boolean collected;
    @Field
    @Indexed
    private String ownerId;
    @Field
    @Indexed
    private Constant.TaskType taskType;
    @Field
    private TaskRecord taskRecord;
    @Field
    @Indexed
    private Long taskId;
    @Field
    private long finishTime;

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Constant.TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Constant.TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public List<TaskReward> getRewards() {
        return rewards;
    }

    public void setRewards(List<TaskReward> rewards) {
        this.rewards = rewards;
    }

    public TaskRecord getTaskRecord() {
        return taskRecord;
    }

    public void setTaskRecord(TaskRecord taskRecord) {
        this.taskRecord = taskRecord;
    }

    public Constant.TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(Constant.TaskType taskType) {
        this.taskType = taskType;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public boolean isFinish() {
        return this.taskStatus.equals(Constant.TaskStatus.TASK_STATUS_FINISHED);
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public void addTaskProgram(long program) {
        if(taskRecord == null) return;
        this.taskRecord.addProgram(program);
        if(taskRecord.isFinish()){
            this.taskStatus = Constant.TaskStatus.TASK_STATUS_FINISHED;
            this.finishTime = System.currentTimeMillis();
        }
    }

    public Task() {
    }

    public Task(String userId, Constant.TaskType taskType, TaskConfig taskConfig) {
        this.ownerId = userId;
        this.createDate = LocalDateTime.now();
        this.taskType = taskType;
        this.taskStatus = Constant.TaskStatus.TASK_STATUS_ACTIVE;
        this.rewards = taskConfig.getReward();
        this.taskId = taskConfig.getTaskId();
        if(taskConfig.getCondition()!=null)
            this.taskRecord = new TaskRecord(taskConfig.getCondition().getAmount());
    }

    public static Task newInstance(String userId, Constant.TaskType taskType, TaskConfig taskConfig) {
        return new Task(userId, taskType, taskConfig);
    }

    public Jinli.TaskInfo toProto() {
        Jinli.TaskInfo.Builder taskInfo = Jinli.TaskInfo.newBuilder().setId(getId()).setTaskId(taskId)
                .setTaskStatus(taskStatus).setTaskType(taskType)
                .setIsCollected(collected);
        if(finishTime>0)
            taskInfo.setFinishTime(String.valueOf(finishTime));
        if(taskRecord !=null){
            taskInfo.setProgress(this.taskRecord.getProgress());
        }
        return taskInfo.build();
    }

}
