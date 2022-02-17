package com.donglaistd.jinli.http.entity;

import java.util.HashMap;
import java.util.Map;

public class GuessWagerDetails {
    public String userId;

    public String displayName;

    public Map<String, GuessBetInfo> wagerList = new HashMap<>();

    public Long wagerTime;

    public String orderNum;

    public Long totalCoin;

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

    public Map<String, GuessBetInfo> getWagerList() {
        return wagerList;
    }

    public void setWagerList(Map<String, GuessBetInfo> wagerList) {
        this.wagerList = wagerList;
    }

    public Long getWagerTime() {
        return wagerTime;
    }

    public void setWagerTime(Long wagerTime) {
        this.wagerTime = wagerTime;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Long getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Long totalCoin) {
        this.totalCoin = totalCoin;
    }
}
