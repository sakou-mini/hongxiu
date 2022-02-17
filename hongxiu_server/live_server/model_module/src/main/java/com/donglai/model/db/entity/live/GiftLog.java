package com.donglai.model.db.entity.live;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document
@Data
@NoArgsConstructor
public class GiftLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    private String senderId;
    @Field
    private String receiveId;
    @Field
    private long sendAmount;
    @Field
    private long time;
    @Field
    private String giftId;
    @Field
    private int sendNum;

    private GiftLog(String senderId, String receiveId, long sendAmount, String giftId, int sendNum) {
        this.senderId = senderId;
        this.receiveId = receiveId;
        this.sendAmount = sendAmount;
        this.giftId = giftId;
        this.sendNum = sendNum;
        this.time = System.currentTimeMillis();
    }

    public static GiftLog newInstance(String senderId, String receiveId, long sendAmount, String giftId, int sendNum) {
        return new GiftLog(senderId, receiveId, sendAmount, giftId, sendNum);
    }

}
