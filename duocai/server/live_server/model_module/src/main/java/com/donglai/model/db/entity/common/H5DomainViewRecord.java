package com.donglai.model.db.entity.common;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Data
public class H5DomainViewRecord {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String domain;
    @Field
    private String userId;
    @Field
    private long time;

    public H5DomainViewRecord(String domain, String userId) {
        this.domain = domain;
        this.userId = userId;
        this.time = System.currentTimeMillis();
    }

    public static H5DomainViewRecord newInstance(String domain, String userId){
        return new H5DomainViewRecord(domain, userId);
    }
}
