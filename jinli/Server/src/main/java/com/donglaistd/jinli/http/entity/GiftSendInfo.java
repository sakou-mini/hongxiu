package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.GiftConfig;

import java.io.Serializable;

public class GiftSendInfo implements Serializable {
    public String giftId;
    public String giftName;
    public long giftPrice;
    public int giftNum;
    public long giftTotalPrice;
    public String giftOfLiveUserId;
    public String giftOfLiveUserName;

    public GiftSendInfo() {
    }

    public GiftSendInfo(GiftConfig giftConfig, int giftNum, User user) {
        this.giftId = giftConfig.getGiftId();
        this.giftName = giftConfig.getName();
        this.giftNum = giftNum;
        this.giftPrice = giftConfig.getPrice();
        this.giftTotalPrice = giftNum * giftConfig.getPrice();
        this.giftOfLiveUserId = user.getLiveUserId();
        this.giftOfLiveUserName = user.getDisplayName();
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public long getGiftPrice() {
        return giftPrice;
    }

    public void setGiftPrice(int giftPrice) {
        this.giftPrice = giftPrice;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public long getGiftTotalPrice() {
        return giftTotalPrice;
    }

    public void setGiftTotalPrice(int giftTotalPrice) {
        this.giftTotalPrice = giftTotalPrice;
    }

    public String getGiftOfLiveUserId() {
        return giftOfLiveUserId;
    }

    public void setGiftOfLiveUserId(String giftOfLiveUserId) {
        this.giftOfLiveUserId = giftOfLiveUserId;
    }

    public String getGiftOfLiveUserName() {
        return giftOfLiveUserName;
    }

    public void setGiftOfLiveUserName(String giftOfLiveUserName) {
        this.giftOfLiveUserName = giftOfLiveUserName;
    }

    @Override
    public String toString() {
        return "GiftSendInfo{" +
                "giftId='" + giftId + '\'' +
                ", giftName='" + giftName + '\'' +
                ", giftPrice=" + giftPrice +
                ", giftNum=" + giftNum +
                ", giftTotalPrice=" + giftTotalPrice +
                ", giftOfLiveUserId='" + giftOfLiveUserId + '\'' +
                ", giftOfLiveUserName='" + giftOfLiveUserName + '\'' +
                '}';
    }
}
