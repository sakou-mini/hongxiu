package com.donglaistd.jinli.http.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class GuessWager {
    @Field
    private String itemId;
    @Field
    private Long betNum;
    @Field
    private Long profitLoss;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Long getBetNum() {
        return betNum;
    }

    public void setBetNum(Long betNum) {
        this.betNum = betNum;
    }

    public Long getProfitLoss() {
        return profitLoss;
    }

    public void setProfitLoss(Long profitLoss) {
        this.profitLoss = profitLoss;
    }
}
