package com.donglaistd.jinli.event;

import com.donglaistd.jinli.database.entity.User;

public class GiftEvent implements BaseEvent {
    private final User sendUser;
    private final User receiveUser;
    private final String giftId;
    private final int sendAmount;
    private final int sendNum;

    public GiftEvent(User sendUser, User receiveUser, String giftId, int totalCoin,int sendNum) {
        this.sendUser = sendUser;
        this.receiveUser = receiveUser;
        this.giftId = giftId;
        this.sendAmount = totalCoin;
        this.sendNum = sendNum;
    }

    public User getSendUser() {
        return sendUser;
    }

    public User getReceiveUser() {
        return receiveUser;
    }

    public String getGiftId() {
        return giftId;
    }

    public int getSendAmount() {
        return sendAmount;
    }

    public int getSendNum() {
        return sendNum;
    }
}
