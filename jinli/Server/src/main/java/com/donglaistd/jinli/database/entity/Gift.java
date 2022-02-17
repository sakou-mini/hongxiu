package com.donglaistd.jinli.database.entity;

import java.io.Serializable;

public class Gift implements Serializable {
    private String giftId;

    public Gift(String giftId) {
        this.giftId = giftId;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }
}
