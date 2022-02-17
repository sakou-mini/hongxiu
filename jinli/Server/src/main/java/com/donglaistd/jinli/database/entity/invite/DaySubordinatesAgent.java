package com.donglaistd.jinli.database.entity.invite;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
@Document
public class DaySubordinatesAgent implements Serializable {
    @Id
    private String userId;
    @Field
    private String displayName;
    @Field
    private long userDayBetIncome;
    @Field
    private long totalBetIncome;
    @Field
    private long teamTotalBetIncome;

    public DaySubordinatesAgent(String userId, long userDayBetIncome, long totalBetIncome, long teamTotalBetIncome) {
        this.userId = userId;
        this.userDayBetIncome = userDayBetIncome;
        this.totalBetIncome = totalBetIncome;
        this.teamTotalBetIncome = teamTotalBetIncome;
    }

    public DaySubordinatesAgent() {
    }

    public static DaySubordinatesAgent newInstance(String userId, long userDayBetIncome, long totalBetIncome, long teamTotalBetIncome){
        return new DaySubordinatesAgent(userId,userDayBetIncome,totalBetIncome,teamTotalBetIncome);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public long getUserDayBetIncome() {
        return userDayBetIncome;
    }

    public void setUserDayBetIncome(long userDayBetIncome) {
        this.userDayBetIncome = userDayBetIncome;
    }

    public long getTotalBetIncome() {
        return totalBetIncome;
    }

    public void setTotalBetIncome(long totalBetIncome) {
        this.totalBetIncome = totalBetIncome;
    }

    public long getTeamTotalBetIncome() {
        return teamTotalBetIncome;
    }

    public void setTeamTotalBetIncome(long teamTotalBetIncome) {
        this.teamTotalBetIncome = teamTotalBetIncome;
    }

}
