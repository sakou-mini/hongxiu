package com.donglai.common.constant;

import com.donglai.protocol.Constant;

public class RedisConstant {
    public static final String QUEUE_EXECUTE_INFO = "queueExecuteInfo";
    public static final String QUEUE_REFTYPE_REFID_INFO="queueRefTypeRefIdInfo";

    public static final String USER_ID = "user_id";
    public static final String USER_SETTING = "user_setting";
    public static final String USER_ACCOUNT= "user_accountId";
    public static final String ONLINE_USER = "online_user";
    public static final String PLATFORM_TOKEN_ACCOUNT = "platform_token::account";
    public static final String PLATFORM_TOKEN = "platform_token";

    public static String getPlatformOnlineUserKey(){
        return ONLINE_USER;
    }

    public static String getPlatformTokenAccountKey(String id){
        return PLATFORM_TOKEN_ACCOUNT + "::" + id;
    }

    public static String getPlatformTokenKey(String token){
        return PLATFORM_TOKEN + "::" + token;
    }
}
