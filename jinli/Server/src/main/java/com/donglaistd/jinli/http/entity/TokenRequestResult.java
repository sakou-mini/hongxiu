package com.donglaistd.jinli.http.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenRequestResult implements Serializable {
    private final int result;
    private final String msg;
    private final String token;
    private final String platformName;
    private final String accountName;
    private final Long expireTime;

    public TokenRequestResult(int result, String msg, String platformName, String token, String accountName, Long expireTime) {
        this.result = result;
        this.msg = msg;
        this.platformName = platformName;
        this.token = token;
        this.accountName = accountName;
        this.expireTime = expireTime;
    }

    public TokenRequestResult(int result, String msg) {
        this.result = result;
        this.msg = msg;
        this.platformName = null;
        this.token = null;
        this.accountName = null;
        this.expireTime = null;
    }

    public String getToken() {
        return token;
    }

    public int getResult() {
        return result;
    }

    public String getAccountName() {
        return accountName;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public String getMsg() {
        return msg;
    }

    public String getPlatformName() {
        return platformName;
    }
}
