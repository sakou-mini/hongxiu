package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;

import java.util.Map;

public class BetRecordSummary {
    private long todayBetAmount;
    private long todayBetCount;
    private long allBetAmount;
    private long allBetCount;
    private Map<Constant.GameType, Long> todayGameIncome;
    private Map<Constant.GameType, Long> allGameIncome;

    public BetRecordSummary(long todayBetAmount, long todayBetCount, long allBetAmount, long allBetCount) {
        this.todayBetAmount = todayBetAmount;
        this.todayBetCount = todayBetCount;
        this.allBetAmount = allBetAmount;
        this.allBetCount = allBetCount;
    }

    public long getTodayBetAmount() {
        return todayBetAmount;
    }

    public void setTodayBetAmount(long todayBetAmount) {
        this.todayBetAmount = todayBetAmount;
    }

    public long getTodayBetCount() {
        return todayBetCount;
    }

    public void setTodayBetCount(long todayBetCount) {
        this.todayBetCount = todayBetCount;
    }

    public long getAllBetAmount() {
        return allBetAmount;
    }

    public void setAllBetAmount(long allBetAmount) {
        this.allBetAmount = allBetAmount;
    }

    public long getAllBetCount() {
        return allBetCount;
    }

    public void setAllBetCount(long allBetCount) {
        this.allBetCount = allBetCount;
    }

    public Map<Constant.GameType, Long> getTodayGameIncome() {
        return todayGameIncome;
    }

    public void setTodayGameIncome(Map<Constant.GameType, Long> todayGameIncome) {
        this.todayGameIncome = todayGameIncome;
    }

    public Map<Constant.GameType, Long> getAllGameIncome() {
        return allGameIncome;
    }

    public void setAllGameIncome(Map<Constant.GameType, Long> allGameIncome) {
        this.allGameIncome = allGameIncome;
    }
}
