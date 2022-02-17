package com.donglaistd.jinli.constant;

import static com.donglaistd.jinli.constant.StatisticEnum.ActiveDaysEnum.*;

public enum StatisticEnum {
   DAY_USERDATA,MONTH_USERDATA,OTHER;

    public enum StatisticItemEnum {
        REGISTER_NUM,
        IP_NUM,SPEND_NUM,
        SPEND_AMOUNT,
        ONLINEUSER_NUM,
        USER_NUM,
        LIVE_NUM,
        LIVE_WATCH_NUM,
        LOGIN_NUM,
        FOLLOW_NUM,
        LIVE_WATCH_TIME,
        AVG_LIVE_WATCH_TIME,
        BULLET_CHAT_NUM,
        LIVEUSER_NUM,
        JINLI_LIVEUSER_NUM;
    }

    public enum ActiveDaysEnum{
        ACTIVE_NEW_USER,
        ACTIVE_2_3_DAY,
        ACTIVE_4_7_DAY,
        ACTIVE_8_14_DAY,
        ACTIVE_15_30_DAY,
        ACTIVE_31_90_DAY,
        ACTIVE_91_180_DAY,
        ACTIVE_181_365_DAY,
        ACTIVE_YEAR
    }

    public static StatisticEnum.ActiveDaysEnum getActiveTypeByDay(int day){
        if(day>=2 && day<=3) return ACTIVE_2_3_DAY;
        else if(day>=4 && day<=7) return ACTIVE_4_7_DAY;
        else if(day>=8 && day<=14) return ACTIVE_8_14_DAY;
        else if(day>=15 && day<=30) return ACTIVE_15_30_DAY;
        else if(day>=31 && day<=90) return ACTIVE_31_90_DAY;
        else if(day>=91 && day<=180) return ACTIVE_91_180_DAY;
        else if(day>=181 && day<=365) return ACTIVE_181_365_DAY;
        else if(day>365) return ACTIVE_YEAR;
        else return null;
    }
}
