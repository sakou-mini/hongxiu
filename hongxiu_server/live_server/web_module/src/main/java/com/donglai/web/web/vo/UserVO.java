package com.donglai.web.web.vo;

import com.donglai.protocol.Constant;
import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-21 14:16
 */
@Data
public class UserVO {


    private String id;


    private String nickname;


    private String accountId;


    private String source;


    private String phoneNumber;


    private String signatureText;


    private String avatarUrl;


    private Constant.GenderType gender;


    private Long birthday;


    private Boolean status;


    private Long blogsCount;


    private Long commentCount;


    private Long focusCount;


    private Long fansCount;


    private Long lastLoginTime;


    private String lastLoginLoginIp;


    private Long updateTime;


    private String reason;

}
