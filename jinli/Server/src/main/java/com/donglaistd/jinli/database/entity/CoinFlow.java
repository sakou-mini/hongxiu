package com.donglaistd.jinli.database.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

public class CoinFlow {
    @Id
    private ObjectId id;
    @Field
    @Indexed(unique = true)
    private String userId;
    @Field
    private long flow = 0;
    @Field
    private long createDate;
    @Field
    private long serviceFlow = 0;
    @Field
    private long giftIncome = 0;
    @Field
    private long giftCost = 0;
    @Field
    private long recharge = 0;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getFlow() {
        return flow;
    }

    public void setFlow(long flow) {
        this.flow = flow;
    }

    public void addFlow(long flow){
        this.flow += flow;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getServiceFlow() {
        return serviceFlow;
    }

    public void setServiceFlow(long serviceFlow) {
        this.serviceFlow = serviceFlow;
    }

    public long getGiftCost() {
        return giftCost;
    }

    public void setGiftCost(long giftCost) {
        this.giftCost = giftCost;
    }

    public long getGiftIncome() {
        return giftIncome;
    }

    public void setGiftIncome(long giftIncome) {
        this.giftIncome = giftIncome;
    }

    public CoinFlow(String userId, long createDate,long flow,  long serviceFlow, long giftIncome,long recharge) {
        this.userId = userId;
        this.flow = flow;
        this.createDate = createDate;
        this.serviceFlow = serviceFlow;
        this.giftIncome = giftIncome;
        this.recharge = recharge;
    }

    public long getRecharge() {
        return recharge;
    }

    public CoinFlow() {
    }

    public void setRecharge(long recharge) {
        this.recharge = recharge;
    }
}
