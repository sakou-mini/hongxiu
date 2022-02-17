package com.donglaistd.jinli.constant;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.StringUtils;

public class PlatformConstant {
    public static final String Q_PLATFORM_H5_PATH = "/qMobile/game/index.html";
    public static final String Q_PLATFORM_H5_LOAD_PATH = "/qMobile/game/index.html?type=1";
    public static String T_PLATFORM_ID_PREFIX = "t_";
    public static String Q_PLATFORM_ID_PREFIX = "q_";

    public static String getPlatformUserIdByPlatform(Constant.PlatformType platform, String id){
        if ((StringUtils.isNullOrBlank(id))) return "";
        switch (platform){
            case PLATFORM_T:
                return T_PLATFORM_ID_PREFIX + id;
            case PLATFORM_Q:
                return Q_PLATFORM_ID_PREFIX + id;
            default: return id;
        }
    }
}
