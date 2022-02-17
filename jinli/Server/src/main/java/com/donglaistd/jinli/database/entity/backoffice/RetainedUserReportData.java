package com.donglaistd.jinli.database.entity.backoffice;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

public class RetainedUserReportData {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private long date;
    @Field
    private BigDecimal nextDayRetainRate;
    @Field
    private BigDecimal nextWeekRetainRate;
    @Field
    private BigDecimal nextMonthRetainRate;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public RetainedUserReportData() {
    }

    private RetainedUserReportData(long date, BigDecimal nextDayRetainRate, BigDecimal nextWeekRetainRate, BigDecimal nextMonthRetainRate) {
        this.date = date;
        this.nextDayRetainRate = nextDayRetainRate;
        this.nextWeekRetainRate = nextWeekRetainRate;
        this.nextMonthRetainRate = nextMonthRetainRate;
    }

    public static RetainedUserReportData newInstance(long date, BigDecimal nextDayRetainRate, BigDecimal nextWeekRetainRate, BigDecimal nextMonthRetainRate){
        return new RetainedUserReportData(date, nextDayRetainRate, nextWeekRetainRate, nextMonthRetainRate);
    }

    public BigDecimal getNextDayRetainRate() {
        return nextDayRetainRate;
    }

    public void setNextDayRetainRate(BigDecimal nextDayRetainRate) {
        this.nextDayRetainRate = nextDayRetainRate;
    }

    public BigDecimal getNextWeekRetainRate() {
        return nextWeekRetainRate;
    }

    public void setNextWeekRetainRate(BigDecimal nextWeekRetainRate) {
        this.nextWeekRetainRate = nextWeekRetainRate;
    }

    public BigDecimal getNextMonthRetainRate() {
        return nextMonthRetainRate;
    }

    public void setNextMonthRetainRate(BigDecimal nextMonthRetainRate) {
        this.nextMonthRetainRate = nextMonthRetainRate;
    }
}
