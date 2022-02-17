package com.donglaistd.jinli.http.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class GuessItem {
    @Field
    public String optionContent;
    @Field
    public long totalPeople;
    @Field
    private long totalCoin;

    public String getOptionContent() {
        return optionContent;
    }

    public void setOptionContent(String optionContent) {
        this.optionContent = optionContent;
    }

    public long getTotalPeople() {
        return totalPeople;
    }

    public void setTotalPeople(long totalPeople) {
        this.totalPeople = totalPeople;
    }

    public long getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(long totalCoin) {
        this.totalCoin = totalCoin;
    }

    public GuessItem() {
    }

    public GuessItem(String optionContent) {
        this.optionContent = optionContent;
    }
}
