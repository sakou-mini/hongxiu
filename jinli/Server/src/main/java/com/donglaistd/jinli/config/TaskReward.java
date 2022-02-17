package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskReward {
    Constant.TaskRewardType rewardType;
    private long amount;

    public Constant.TaskRewardType getRewardType() {
        return rewardType;
    }

    public void setRewardType(int rewardType) {
        this.rewardType = Constant.TaskRewardType.forNumber(rewardType);
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public TaskReward() {
    }

    @JsonCreator
    public TaskReward( @JsonProperty("rewardType") int rewardType, @JsonProperty("amount") long amount) {
        this.rewardType = Constant.TaskRewardType.forNumber(rewardType);;
        this.amount = amount;
    }

    public TaskReward(Constant.TaskRewardType rewardType, long amount) {
        this.rewardType = rewardType;
        this.amount = amount;
    }

    public static TaskReward newInstance(Constant.TaskRewardType rewardType, long amount){
        return new TaskReward(rewardType,amount);
    }

    public void addAmount(long amount){
        this.amount += amount;
    }

    public Jinli.TaskReward toProto(){
        return Jinli.TaskReward.newBuilder().setAmount(amount).setRewardType(rewardType).build();
    }
}
