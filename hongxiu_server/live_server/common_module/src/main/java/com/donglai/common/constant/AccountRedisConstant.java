package com.donglai.common.constant;

import com.donglai.protocol.Constant;

public class AccountRedisConstant {
    public static final String SEPARATOR = "::";
    public static final String LOGIN_SESSION = "login_session";
    public static final String BIND_ACCOUNT = "bind_account";
    public static final String PHONE_AUTH_CODE = "phone_auth_code";
    public static final String USER_INFO_SESSION = "user_info_session";

    public static String getLoginSession(String accountId) {
        return LOGIN_SESSION + SEPARATOR + accountId;
    }

    public static String getBindKey(String accountId) {
        return BIND_ACCOUNT + SEPARATOR + accountId;
    }

    public static String getPhoneAuthCodeKey(Constant.AuthCodeType type, String phoneNumber) {
        //phone_auth_code::ACCOUNT_AUTH::182565836
        return PHONE_AUTH_CODE + SEPARATOR + type.name() + SEPARATOR + phoneNumber;
    }

    public static String getUserInfoSession(String accountId) {
        return USER_INFO_SESSION + SEPARATOR + accountId;
    }
}
