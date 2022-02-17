package com.donglaistd.jinli.database.entity.invite;

import com.donglaistd.jinli.Jinli;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Document
public class DayGroupContributionRecord {
    @Id
    private ObjectId id = ObjectId.get();

    @Field
    @Indexed
    private String userId;

    @Field
    private long firstAgentTotalBet;

    @Field
    private long secondAgentTotalBet;

    @Field
    private BigDecimal awardCoin;

    @Field
    private long time;

    public DayGroupContributionRecord(String userId, long firstAgentTotalBet, long secondAgentTotalBet, BigDecimal awardCoin, long time) {
        this.userId = userId;
        this.firstAgentTotalBet = firstAgentTotalBet;
        this.secondAgentTotalBet = secondAgentTotalBet;
        this.awardCoin = awardCoin;
        this.time = time;
    }
    public DayGroupContributionRecord() {
    }

    public DayGroupContributionRecord(String userId) {
        this.userId = userId;
    }

    public static DayGroupContributionRecord newInstance(String userId){
        return new DayGroupContributionRecord(userId);
    }

    public static DayGroupContributionRecord newInstance(String userId, long firstAgentTotalBet, long secondAgentTotalBet, BigDecimal awardCoin, long time){
        return new DayGroupContributionRecord(userId, firstAgentTotalBet, secondAgentTotalBet, awardCoin,time);
    }

    public String getId() {
        return id.toString();
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

    public long getFirstAgentTotalBet() {
        return firstAgentTotalBet;
    }

    public void setFirstAgentTotalBet(long firstAgentTotalBet) {
        this.firstAgentTotalBet = firstAgentTotalBet;
    }

    public long getSecondAgentTotalBet() {
        return secondAgentTotalBet;
    }

    public void setSecondAgentTotalBet(long secondAgentTotalBet) {
        this.secondAgentTotalBet = secondAgentTotalBet;
    }

    public double getAwardCoin() {
        return awardCoin.doubleValue();
    }

    public void setAwardCoin(BigDecimal awardCoin) {
        this.awardCoin = awardCoin;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Jinli.DayAgentIncomeDetail toProto(){
       return Jinli.DayAgentIncomeDetail.newBuilder().setTime(getTime()).setCommission(getAwardCoin())
               .setFirstAgentFlow(getFirstAgentTotalBet()).setSecondAgentFlow(secondAgentTotalBet).build();
    }

    @Override
    public String toString() {
        return "DayGroupContributionRecord{" +
                "userId='" + userId + '\'' +
                ", firstAgentTotalBet=" + firstAgentTotalBet +
                ", secondAgentTotalBet=" + secondAgentTotalBet +
                ", awardCoin=" + awardCoin +
                ", time=" + time +
                '}';
    }
}

