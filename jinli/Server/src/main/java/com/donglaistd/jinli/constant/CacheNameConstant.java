package com.donglaistd.jinli.constant;

import com.donglaistd.jinli.Constant;

public class CacheNameConstant {
    public static final String BullBull="bull_bull";
    public static final String GoldenFlower="golden_flower";
    public static final String DiaryIds="diary_ids";
    public static final String DiaryStars = "diary_stars";
    public static final String DIARY_VIEW_HISTORY = "diary_view_history";
    public static final String UserDiary = "user_diary";
    public static final String DiaryLock = "DiaryLock";
    public static final String ROOM_HISTORY_RECORD = "room_history_record";
    public static final String ROOM_BET_AMOUNT = "room_bet_amount";
    public static final String LOCK_SERVER = "lock::server";
    public static final String DisconnectUser = "disconnectUser";
    public static final String ONLINE_ROOM = "onlineRoom";
    public static final String ENTER_ROOM_RECORD = "enterRoomRecord";
    public static final String LIVE_TIME_RECORD = "live_time_record";
    public static final String LIVE_AUDIENCE_NUMBER_RECORD = "live_audience_number_record";
    public static final String GIFT_CONTRIBUTE_RANK = "gift_contribute_rank";
    public static final String USER = "user";
    public static final String LIVE_USER = "live_user";
    public static final String FANS_CONTRIBUTE_RANK = "fans_contribute_rank";

    public static final String MENU_ROLE_KEY = "menu_role_key";

    public static final String PLATFORM_TOKEN_ACCOUNT = "platform_token::account";
    public static final String PLATFORM_TOKEN = "platform_token";

    public static final String USER_SESSION = "userSession";

    public static String getEnterRoomRecordKey(String userId){
        return ENTER_ROOM_RECORD + "::" + userId;
    }

    public static String getLiveTimeRecordKey(long time){
        return LIVE_TIME_RECORD + "::" + time;
    }

    public static String getLiveAudienceNumberRecordKey(long time){
        return LIVE_AUDIENCE_NUMBER_RECORD + "::" + time;
    }

    public static String getGiftContributeRankKey(Constant.PlatformType platformType){
        return GIFT_CONTRIBUTE_RANK + "::" + platformType;
    }

    public static String getDiaryViewHistoryKey(String userId){
        return DIARY_VIEW_HISTORY + "::" + userId;
    }

    public static String getRoomHistoryRecordKey(String roomId) {
        return ROOM_HISTORY_RECORD + ":" + roomId;
    }

    public static String getUserCacheKey(String userId){
        return USER + "::" + userId;
    }

    public static String getLiveUserCacheKey(String liveUserId){
        return LIVE_USER + "::" + liveUserId;
    }

    public static String getFansContributeRankKey(String userId){
        return FANS_CONTRIBUTE_RANK + "::" + userId;
    }

    public static String getPlatformTokenAccountKey(String account){
        return PLATFORM_TOKEN_ACCOUNT + "::" + account;
    }

    public static String getPlatformTokenKey(String token){
        return PLATFORM_TOKEN + "::" + token;
    }

    public static String getUserSessionKey(String userId) {
        return USER_SESSION + "::" + userId;
    }
}
