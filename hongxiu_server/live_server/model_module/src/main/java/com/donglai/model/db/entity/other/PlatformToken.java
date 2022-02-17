package com.donglai.model.db.entity.other;

import lombok.Data;

import java.io.Serializable;

@Data
public class PlatformToken implements Serializable {
    private final String token;
    private final Long expireTime;
    //private final String accountId;
    private final String createdIp;

    public PlatformToken(String token, Long expireTime, String createdIp) {
        this.token = token;
        this.expireTime = expireTime;
        //this.accountId = accountId;
        this.createdIp = createdIp;
    }
}
