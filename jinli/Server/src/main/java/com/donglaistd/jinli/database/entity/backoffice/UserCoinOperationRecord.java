package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class UserCoinOperationRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String backOfficeId;
    @Field
    private String userId;
    @Field
    private long originalCoin;
    @Field
    private long existingCoin;
    @Field
    private long operationCoin;
    @Field
    private long time;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getBackOfficeId() {
        return backOfficeId;
    }

    public void setBackOfficeId(String backOfficeId) {
        this.backOfficeId = backOfficeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getOriginalCoin() {
        return originalCoin;
    }

    public void setOriginalCoin(long originalCoin) {
        this.originalCoin = originalCoin;
    }

    public long getExistingCoin() {
        return existingCoin;
    }

    public void setExistingCoin(long existingCoin) {
        this.existingCoin = existingCoin;
    }

    public long getOperationCoin() {
        return operationCoin;
    }

    public void setOperationCoin(long operationCoin) {
        this.operationCoin = operationCoin;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
