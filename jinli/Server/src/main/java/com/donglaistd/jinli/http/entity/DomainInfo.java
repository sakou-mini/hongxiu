package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.constant.DomainStatue;
import com.donglaistd.jinli.database.entity.system.domainConfig.DomainConfig;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DomainInfo implements Serializable {
    private long id;
    private String domainName;
    private long useDay;
    private DomainStatue statue;
    private long lastHourViewNum;
    private long currentViewNum;

    public DomainInfo(DomainConfig domainConfig, long lastHourViewNum, long currentViewNum) {
        this.id = domainConfig.getId();
        this.domainName = domainConfig.getDomainName();
        this.useDay = TimeUnit.MILLISECONDS.toDays((System.currentTimeMillis() - domainConfig.getCreateTime()));
        this.statue = domainConfig.getStatue();
        this.lastHourViewNum = lastHourViewNum;
        this.currentViewNum = currentViewNum;
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

    public long getUseDay() {
        return useDay;
    }

    public void setUseDay(long useDay) {
        this.useDay = useDay;
    }

    public DomainStatue getStatue() {
        return statue;
    }

    public void setStatue(DomainStatue statue) {
        this.statue = statue;
    }

    public long getLastHourViewNum() {
        return lastHourViewNum;
    }

    public void setLastHourViewNum(long lastHourViewNum) {
        this.lastHourViewNum = lastHourViewNum;
    }

    public long getCurrentViewNum() {
        return currentViewNum;
    }

    public void setCurrentViewNum(long currentViewNum) {
        this.currentViewNum = currentViewNum;
    }
}
