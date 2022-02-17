package com.donglaistd.jinli.http.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class GuessBetInfo {
    @Field
    private String optionContent;
    @Field
    private Long betNum;
    @Field
    private Long profitLoss;

    public String getOptionContent() {
        return optionContent;
    }

    public void setOptionContent(String optionContent) {
        this.optionContent = optionContent;
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
