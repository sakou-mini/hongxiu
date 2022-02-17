package com.donglaistd.jinli.http.entity;

import java.io.Serializable;

public class RechargeResult implements Serializable {
    private int result;
    private String msg;

    public RechargeResult(int result, String msg) {
        this.result = result;
        this.msg = msg;
    }

    public RechargeResult() {
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

    @Override
    public String toString() {
        return "RechargeResult{" +
                "result=" + result +
                ", msg='" + msg + '\'' +
                '}';
    }
}
