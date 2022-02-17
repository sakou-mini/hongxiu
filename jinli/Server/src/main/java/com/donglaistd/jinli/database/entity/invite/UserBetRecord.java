package com.donglaistd.jinli.database.entity.invite;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class UserBetRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed(unique = true)
    private String userId;
    @Field
    private long totalBetCoin;

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

    public long getTotalBetCoin() {
        return totalBetCoin;
    }

    public void setTotalBetCoin(long totalBetCoin) {
        this.totalBetCoin = totalBetCoin;
    }

    public void addTotalBetCoin(long betCoinNum){
        totalBetCoin += betCoinNum;
    }
    private UserBetRecord(String userId) {
        this.userId = userId;
    }

    public UserBetRecord() {}

    public static UserBetRecord newInstance(String userId){
        return new UserBetRecord(userId);
    }
}
