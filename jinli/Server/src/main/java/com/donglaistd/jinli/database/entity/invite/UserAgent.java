package com.donglaistd.jinli.database.entity.invite;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
@Document
public class UserAgent implements Serializable {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String userId;
    @Field
    private double totalIncome;
    @Field
    private double leftIncome;

    public UserAgent() {
    }

    public UserAgent(String userId) {
        this.userId = userId;
    }

    public static UserAgent newInstance(String userId) {
        return new UserAgent(userId);
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

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getLeftIncome() {
        return leftIncome;
    }

    public double decLeftIncome(long coin){
        return this.leftIncome -= coin;
    }

    public void setLeftIncome(double leftIncome) {
        this.leftIncome = leftIncome;
    }
}
