package com.donglai.model.db.entity.live;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;

@Document
@Data
@NoArgsConstructor
public class CoinFlow {
    //id means userId
    @Id
    private String id;
    @Field
    private long coinFlow = 0;
    @Field
    private long createDate;
    @Field
    private long giftIncome = 0;
    @Field
    private long giftCost = 0;
    @Field
    private long recharge = 0;

    public CoinFlow(String userId, long createDate, long flow, long giftIncome, long recharge) {
        this.id = userId;
        this.coinFlow = flow;
        this.createDate = createDate;
        this.giftIncome = giftIncome;
        this.recharge = recharge;
    }
}
