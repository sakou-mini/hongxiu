package com.donglai.model.db.entity.common;

import com.donglai.protocol.Constant;
import lombok.Data;

@Data
public class UserDescribe {
    private String mobileCode;
    private String phoneNumber;
    private String signatureText;
    private String backgroundImage;
    private boolean isTourist = true;
    private Constant.GenderType gender = Constant.GenderType.Gender_NULL;
    private long birthday;
    private String region;
}
