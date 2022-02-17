package com.donglaistd.jinli.database.entity.system.domainConfig;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DomainViewRecord {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String domain;
    @Field
    private String userId;
    @Field
    private long time;
    @Field
    private Constant.PlatformType platformType;

    public DomainViewRecord( String domain, String userId, Constant.PlatformType platformType) {
        this.domain = domain;
        this.userId = userId;
        this.platformType = platformType;
        this.time = System.currentTimeMillis();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }
}
