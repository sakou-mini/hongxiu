package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.constant.BackofficeOperandEnum;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class BackofficeUserOperationRecord {
    @Id
    private ObjectId id =ObjectId.get();
    @Field
    private BackofficeOperandEnum operationType;//操作类型
    @Field
    private long time;
    @Field
    private String backOfficeName;
    @Field
    private String operationContent;

    public BackofficeUserOperationRecord(String backOfficeName,BackofficeOperandEnum operationType, long time , String operationContent) {
        this.operationType = operationType;
        this.time = time;
        this.backOfficeName = backOfficeName;
        this.operationContent = operationContent;
    }

    public static BackofficeUserOperationRecord newInstance(String backOfficeName,BackofficeOperandEnum operationType, long time , String operationContent){
        return new BackofficeUserOperationRecord(backOfficeName, operationType, time, operationContent);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public BackofficeOperandEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(BackofficeOperandEnum operationType) {
        this.operationType = operationType;
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

    public String getOperationContent() {
        return operationContent;
    }

    public void setOperationContent(String operationContent) {
        this.operationContent = operationContent;
    }

}
