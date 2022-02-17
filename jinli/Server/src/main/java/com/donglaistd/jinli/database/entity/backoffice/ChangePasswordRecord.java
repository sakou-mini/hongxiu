package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class ChangePasswordRecord {
    @Id
    private ObjectId id =ObjectId.get();
    @Field
    private long time;
    @Field
    private String backOfficeName;
    @Field
    private String userId;
    @Field
    private long gameCoin;

    public ChangePasswordRecord() {
    }

    public ChangePasswordRecord(long time, String backOfficeName, String userId, long gameCoin) {
        this.time = time;
        this.backOfficeName = backOfficeName;
        this.userId = userId;
        this.gameCoin = gameCoin;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBackOfficeName() {
        return backOfficeName;
    }

    public void setBackOfficeName(String backOfficeName) {
        this.backOfficeName = backOfficeName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getGameCoin() {
        return gameCoin;
    }

    public void setGameCoin(long gameCoin) {
        this.gameCoin = gameCoin;
    }

    @Override
    public String toString() {
        return "ChangePasswordRecord{" +
                "id=" + id +
                ", time=" + time +
                ", backOfficeName='" + backOfficeName + '\'' +
                ", userId='" + userId + '\'' +
                ", gameCoin=" + gameCoin +
                '}';
    }
}
