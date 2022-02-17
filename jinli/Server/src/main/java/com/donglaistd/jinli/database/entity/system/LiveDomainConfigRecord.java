package com.donglaistd.jinli.database.entity.system;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document
public class LiveDomainConfigRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private List<String> oldDomain;
    @Field
    private List<String> newDomain;
    @Field
    private long time;
    @Field
    private String backOfficeUserId;
    @Transient
    private String backOfficeName;

    public LiveDomainConfigRecord() {
    }

    private LiveDomainConfigRecord(List<String> oldDomain, List<String> newDomain, String backOfficeUserId) {
        this.oldDomain = oldDomain;
        this.newDomain = newDomain;
        this.time = System.currentTimeMillis();
        this.backOfficeUserId = backOfficeUserId;
    }

    public static LiveDomainConfigRecord newInstance(List<String> oldDomain, List<String> newDomain, String backOfficeUserId){
        return new LiveDomainConfigRecord(oldDomain, newDomain, backOfficeUserId);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<String> getOldDomain() {
        return oldDomain;
    }

    public void setOldDomain(List<String> oldDomain) {
        this.oldDomain = oldDomain;
    }

    public List<String> getNewDomain() {
        return newDomain;
    }

    public void setNewDomain(List<String> newDomain) {
        this.newDomain = newDomain;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getBackOfficeUserId() {
        return backOfficeUserId;
    }

    public void setBackOfficeUserId(String backOfficeUserId) {
        this.backOfficeUserId = backOfficeUserId;
    }

    public String getBackOfficeName() {
        return backOfficeName;
    }

    public void setBackOfficeName(String backOfficeName) {
        this.backOfficeName = backOfficeName;
    }

}
