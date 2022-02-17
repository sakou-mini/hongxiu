package com.donglaistd.jinli.database.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Document
public class ServerRunningRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long recordTime;

    private ServerRunningRecord() {

    }
    public static ServerRunningRecord newInstance(){
        return new ServerRunningRecord();
    }

    public String getId() {
        return id.toString();
    }

    public long getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(long recordTime) {
        this.recordTime = recordTime;
    }
}
