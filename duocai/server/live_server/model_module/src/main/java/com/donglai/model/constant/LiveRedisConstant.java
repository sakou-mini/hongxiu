package com.donglai.model.constant;

public class LiveRedisConstant {
    public static final String ROOM = "room";
    public static final String LIVE_USER= "liveUser";
    public static final String LIVING_ROOM = "livingRoom";
    public static final String LIVE_LINE_DOMAIN = "liveLineDomain";

    public static final String ENTER_ROOM_RECORD = "enter_room_record";

    public static final String SEND_BULLET_MESSAGE_RECORD = "bullet_message_record";
    public static final String GIFT_RANK = "giftRank";
    public static final String SEPARATOR = "::";
    public static final String BIZ_GIFT_CONTRIBUTE = "contribute";
    public static final String BIZ_GIFT_INCOME = "income";

    public static String getGiftContributeRankKey(){
        // giftRank::contribute ->  zSet:{（userId,socre）}
        return GIFT_RANK + SEPARATOR + BIZ_GIFT_CONTRIBUTE;
    }

    public static String getGiftIncomeRankKey(){
        // giftRank::income ->  zSet:{（userId,socre）}
        return GIFT_RANK + SEPARATOR + BIZ_GIFT_INCOME;
    }
}
