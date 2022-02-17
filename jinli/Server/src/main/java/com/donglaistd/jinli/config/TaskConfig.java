package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskConfig {
    private Long taskId;
    private String description;
    private TaskCondition condition;
    private Constant.JumpTarget jumpTarget;

    private List<TaskReward> reward;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskCondition getCondition() {
        return condition;
    }

    @JsonProperty("condition")
    public void setCondition(TaskCondition condition) {
        this.condition = condition;
    }


    public List<TaskReward> getReward() {
        return reward;
    }

    @JsonProperty("reward")
    public void setReward(List<TaskReward> reward) {
        this.reward = reward;
    }

    public Constant.JumpTarget getJumpTarget() {
        return jumpTarget;
    }

    public void setJumpTarget(Integer jumpTarget) {
        this.jumpTarget = Constant.JumpTarget.forNumber(jumpTarget);
    }

    private Constant.JumpTarget toJumpStart(String jumpTarget) {
        String strJumpTarget = "JUMP_TARGET_"+jumpTarget.toUpperCase();
        Constant.JumpTarget jumpTargetEnum = null;
        try {
            jumpTargetEnum = Constant.JumpTarget.valueOf(strJumpTarget);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return jumpTargetEnum;
    }
}
