package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;
import org.bson.types.ObjectId;

public class GuessStatistics {
    public String id;

    public String title;

    public Constant.GuessState state;

    public long total;

    public long totalCoin;

    public Long wagerStartTime;

    public Long wagerEndTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Constant.GuessState getState() {
        return state;
    }

    public void setState(Constant.GuessState state) {
        this.state = state;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(long totalCoin) {
        this.totalCoin = totalCoin;
    }

    public Long getWagerStartTime() {
        return wagerStartTime;
    }

    public void setWagerStartTime(Long wagerStartTime) {
        this.wagerStartTime = wagerStartTime;
    }

    public Long getWagerEndTime() {
        return wagerEndTime;
    }

    public void setWagerEndTime(Long wagerEndTime) {
        this.wagerEndTime = wagerEndTime;
    }
}
