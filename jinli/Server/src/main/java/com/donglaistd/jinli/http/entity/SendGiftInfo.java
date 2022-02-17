package com.donglaistd.jinli.http.entity;

import com.google.common.base.Strings;

public class SendGiftInfo {
    private String accountName;
    private String receiver;
    private String giftId;
    private int giftCount;


    public SendGiftInfo() {
    }

    public SendGiftInfo(String accountName, String receiver, String giftId, int giftCount) {
        this.accountName = accountName;
        this.receiver = receiver;
        this.giftId = giftId;
        this.giftCount = giftCount;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public int getGiftCount() {
        return giftCount;
    }

    public void setGiftCount(int giftCount) {
        this.giftCount = giftCount;
    }

    public boolean verifyParam(){
        return !Strings.isNullOrEmpty(accountName) && !Strings.isNullOrEmpty(receiver) && !Strings.isNullOrEmpty(giftId) && giftCount > 0;
    }
}
