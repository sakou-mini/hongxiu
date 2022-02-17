package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.util.OrderIdUtils;
import org.springframework.data.mongodb.core.mapping.Document;

/*Q platform send gift order record*/
@Document
public class GiftOrder {
    private String id = OrderIdUtils.getOrderNumber();

    private String giftId;
    private String senderId;
    private String receiverId;
    private int sendAmount;
    private long totalPrice;
    private long createTime;

    public GiftOrder(String giftId, String senderId, String receiverId, int sendAmount, long totalPrice) {
        this.giftId = giftId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.sendAmount = sendAmount;
        this.totalPrice = totalPrice;
        this.createTime = System.currentTimeMillis();
    }

    public GiftOrder() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getSendAmount() {
        return sendAmount;
    }

    public void setSendAmount(int sendAmount) {
        this.sendAmount = sendAmount;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "GiftOrder{" +
                "id='" + id + '\'' +
                ", giftId='" + giftId + '\'' +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", sendAmount='" + sendAmount + '\'' +
                ", totalPrice='" + totalPrice + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
