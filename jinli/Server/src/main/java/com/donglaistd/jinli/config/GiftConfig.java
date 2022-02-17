package com.donglaistd.jinli.config;

import com.donglaistd.jinli.Constant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftConfig {

    public String id;
    public int price;
    public Constant.GiftPriceType priceType;

    @JsonCreator
    public GiftConfig(@JsonProperty("id") String id, @JsonProperty("price") int price , @JsonProperty("priceType")int priceType) {
        this.id = id;
        this.price = price;
        this.priceType = Constant.GiftPriceType.forNumber(priceType);
    }
}
