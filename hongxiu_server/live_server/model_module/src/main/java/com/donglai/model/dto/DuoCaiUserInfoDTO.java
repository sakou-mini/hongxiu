package com.donglai.model.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class DuoCaiUserInfoDTO {
    private String id;  // 该用户于多彩网C端id  (唯一)
    private String username;// 该用户帐号 (唯一)
    private String nickname;// 该用户昵称 (非唯一)
    private String category;// "1" 表示一般用户 , "2" 表示大神(竞猜师)

    public boolean allowLogin() {
        return Objects.equals("2", category);
    }
}
