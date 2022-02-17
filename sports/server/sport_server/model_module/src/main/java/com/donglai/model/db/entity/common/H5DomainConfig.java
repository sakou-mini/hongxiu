package com.donglai.model.db.entity.common;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.constant.DomainArea;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
@Data
@NoArgsConstructor
public class H5DomainConfig {
    @Id
    @AutoIncKey
    private long id;
    @Field
    @Indexed(unique = true)
    private String domainName;
    @Field
    private long createTime;
    @Field
    private boolean status = true; //true ：正常  ， false ： 不可用
    @Field
    private DomainArea line;

    private H5DomainConfig(String domainName, DomainArea line) {
        this.domainName = domainName;
        this.createTime = System.currentTimeMillis();
        this.line = line;
    }

    public static H5DomainConfig newInstance(String domainName, DomainArea line){
        return new H5DomainConfig(domainName, line);
    }
}
