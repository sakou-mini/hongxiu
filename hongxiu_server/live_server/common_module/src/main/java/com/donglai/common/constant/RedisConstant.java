package com.donglai.common.constant;

import com.donglai.protocol.Constant;

public class RedisConstant {
    public static final String SEPARATOR = "::";
    public static final String QUEUE_EXECUTE_INFO = "queueExecuteInfo";
    public static final String QUEUE_REFTYPE_REFID_INFO = "queueRefTypeRefIdInfo";

    public static final String USER_ID = "user_id";
    public static final String USER_SETTING = "user_setting";
    public static final String USER_ACCOUNT = "user_accountId";
    public static final String ONLINE_USER = "online_user";
    public static final String PLATFORM_TOKEN = "platform_token";

    public static final String KEYWORDS = "keywords";
    //statistic server(后台首页的今日数据)
    public static final String TODAY_OF_SERVERDATA = "todayOfServerData";

    public static String getPlatformOnlineUserKey(Constant.PlatformType platformType) {
        return ONLINE_USER + SEPARATOR + platformType.name();
    }

    public static String getPlatformTokenKey(String token) {
        return PLATFORM_TOKEN + "::" + token;
    }
}
