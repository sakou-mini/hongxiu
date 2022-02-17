package com.donglai.common.util;

import com.donglai.common.constant.EmailPhoneConstant;
import com.donglai.protocol.Constant;
import com.google.common.base.Strings;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_LIVE;
import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_OFFLINE;

public class VerifyUtil {

    public static boolean isPassLiveUserStatus(Constant.LiveUserStatus status) {
        return Objects.equals(LIVE_OFFLINE, status) || Objects.equals(LIVE_LIVE, status);
    }

    /*1.验证手机号*/
    public static boolean verifyPhoneNumber(String tel) {
        if (Strings.isNullOrEmpty(tel))
            return false;
        return Pattern.matches(EmailPhoneConstant.REGEX_MOBILE, tel);
    }
}
