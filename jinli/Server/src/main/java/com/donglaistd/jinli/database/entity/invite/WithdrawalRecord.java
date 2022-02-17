package com.donglaistd.jinli.database.entity.invite;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class WithdrawalRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String userId;
    @Field
    private long coinFlow;
    @Field
    private long time;

    public WithdrawalRecord(String userId, long coinFlow) {
        this.userId = userId;
        this.coinFlow = coinFlow;
        this.time = System.currentTimeMillis();
    }
    public WithdrawalRecord() {
    }
    public static WithdrawalRecord newInstance(String userId, long coinFlow){
        return new WithdrawalRecord(userId, coinFlow);
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

    public long getCoinFlow() {
        return coinFlow;
    }

    public void setCoinFlow(long coinFlow) {
        this.coinFlow = coinFlow;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
