package com.donglaistd.jinli.database.entity.system.domainConfig;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.DomainLine;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DomainConfigRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private DomainLine domainLine;
    @Field
    private String oldDomain;
    @Field
    private String newDomain;
    @Field
    private long time;
    @Field
    private String backOfficeName;
    @Field
    private Constant.PlatformType platformType;

    public DomainConfigRecord(DomainLine domainLine, String oldDomain, String newDomain, String backOfficeName, Constant.PlatformType platformType) {
        this.domainLine = domainLine;
        this.oldDomain = oldDomain;
        this.newDomain = newDomain;
        this.backOfficeName = backOfficeName;
        this.time = System.currentTimeMillis();
        this.platformType = platformType;
    }

    public DomainConfigRecord(DomainLine domainLine, String domain,String backOfficeName, Constant.PlatformType platformType) {
        this.domainLine = domainLine;
        this.newDomain = domain;
        this.time =  System.currentTimeMillis();
        this.backOfficeName = backOfficeName;
        this.platformType = platformType;
    }

    public DomainConfigRecord() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public DomainLine getDomainLine() {
        return domainLine;
    }

    public void setDomainLine(DomainLine domainLine) {
        this.domainLine = domainLine;
    }

    public String getOldDomain() {
        return oldDomain;
    }

    public void setOldDomain(String oldDomain) {
        this.oldDomain = oldDomain;
    }

    public String getNewDomain() {
        return newDomain;
    }

    public void setNewDomain(String newDomain) {
        this.newDomain = newDomain;
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

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }
}
