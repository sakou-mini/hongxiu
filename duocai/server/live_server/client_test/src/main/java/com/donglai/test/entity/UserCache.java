package com.donglai.test.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCache {
    public String userId;
    public String account;
    public String password;
    public String roomId;
    public String liveUserId;


    public UserCache(String account, String password) {
        this.account = account;
        this.password = password;
    }
}
