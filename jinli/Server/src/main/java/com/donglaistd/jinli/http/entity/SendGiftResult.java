package com.donglaistd.jinli.http.entity;

public class SendGiftResult {
    private int result;
    private String msg;

    public SendGiftResult() {
    }

    public SendGiftResult(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
