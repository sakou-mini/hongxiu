package com.donglaistd.jinli.database.entity.system.domainConfig;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.annotation.AutoIncKey;
import com.donglaistd.jinli.constant.DomainLine;
import com.donglaistd.jinli.constant.DomainStatue;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document
public class DomainConfig {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String domainName;
    @Field
    private long createTime;
    @Field
    private DomainStatue statue;
    @Field
    private DomainLine line;
    @Field
    private Constant.PlatformType platformType;

    public DomainConfig(String domainName, DomainStatue statue, DomainLine line, Constant.PlatformType platformType) {
        this.domainName = domainName;
        this.createTime = System.currentTimeMillis();
        this.statue = statue;
        this.line = line;
        this.platformType = platformType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public DomainStatue getStatue() {
        return statue;
    }

    public void setStatue(DomainStatue statue) {
        this.statue = statue;
    }

    public DomainLine getLine() {
        return line;
    }

    public void setLine(DomainLine line) {
        this.line = line;
    }

    public Constant.PlatformType getPlatformType() {
        if(platformType==null) return Constant.PlatformType.PLATFORM_T;
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }
}
