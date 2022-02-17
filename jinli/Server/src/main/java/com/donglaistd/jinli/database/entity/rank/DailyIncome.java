package com.donglaistd.jinli.database.entity.rank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "dailyIncome")
public class DailyIncome {
    @Id
    private String id;
    private String userId;
    private long time;
    private long amount;

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "DailyIncome{" +
                "id='" + id + '\'' +
                ", userId=" + userId +
                ", time=" + time +
                ", amount=" + amount +
                '}';
    }
}
