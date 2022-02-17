package com.donglai.live.constant;

public class LiveRedisConstant {
    public static final String GIFT_RANK = "giftRank";
    public static final String SEPARATOR = "::";
    public static final String BIZ_GIFT_CONTRIBUTE = "contribute";
    public static final String BIZ_GIFT_INCOME = "income";

    public static String getGiftContributeRankKey() {
        // giftRank::contribute ->  zSet:{（userId,socre）}
        return GIFT_RANK + SEPARATOR + BIZ_GIFT_CONTRIBUTE;
    }

    public static String getGiftIncomeRankKey() {
        // giftRank::income ->  zSet:{（userId,socre）}
        return GIFT_RANK + SEPARATOR + BIZ_GIFT_INCOME;
    }
}
