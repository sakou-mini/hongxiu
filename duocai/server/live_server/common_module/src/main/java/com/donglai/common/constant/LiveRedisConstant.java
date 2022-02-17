package com.donglai.common.constant;

public class LiveRedisConstant {
    public static final String ROOM = "room";
    public static final String LIVE_USER = "liveUser";
    public static final String LIVING_ROOM = "livingRoom";
    public static final String LIVE_LINE_DOMAIN = "liveLineDomain";

    public static final String ENTER_ROOM_RECORD = "enter_room_record";

    public static final String GIFT_RANK = "giftRank";

    public static final String SEND_BULLET_MESSAGE_RECORD = "bullet_message_record";

    public static final String SEPARATOR = "::";

    public static String getEnterRoomRecordKey(String userId) {
        //enter_room_record::{userId} -> roomId
        return ENTER_ROOM_RECORD + SEPARATOR + userId;
    }

    public static String getUserLastSendBulletKey(String userId) {
        //bullet_message_record::{userId} -> time
        return SEND_BULLET_MESSAGE_RECORD + SEPARATOR + userId;
    }
}
