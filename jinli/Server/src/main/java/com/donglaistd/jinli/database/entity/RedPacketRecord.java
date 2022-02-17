package com.donglaistd.jinli.database.entity;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Calendar;

@Document
public class RedPacketRecord {
    private ObjectId id = ObjectId.get();

    private String redPacketId;

    private String userId;

    private int amount;

    private long grabTime;

    public RedPacketRecord(String redPacketId, String userId, int amount) {
        this.redPacketId = redPacketId;
        this.userId = userId;
        this.amount = amount;
        this.grabTime = Calendar.getInstance().getTimeInMillis();
    }

    public static RedPacketRecord getInstance(String redPacketId, String userId, int amount) {
        return new RedPacketRecord(redPacketId, userId, amount);
    }

    public String getId() {
        return id.toString();
    }

    public String getRedPacketId() {
        return redPacketId;
    }

    public String getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }

    public long getGrabTime() {
        return grabTime;
    }

    @Override
    public String toString() {
        return "RedPacketRecord{" +
                "id=" + id +
                ", redPacketId='" + redPacketId + '\'' +
                ", userId='" + userId + '\'' +
                ", amount=" + amount +
                ", grabTime=" + grabTime +
                '}';
    }
}
