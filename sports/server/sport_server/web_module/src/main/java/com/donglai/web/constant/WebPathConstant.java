package com.donglai.web.constant;

public class WebPathConstant {
    public static final String DEFAULT_AVATAR = "/serverRes/default/image_default/duocai/icon.png";
    public static final String H5_ROOM_BASE_PATH = "/clientRes/h5/web-mobile/index.html";

    public static final String H5_SPORT_ROOM_BASE_PATH = "/sport/client/h5/web-mobile/index.html";

    public static String getPlatformLiveRoomUrl(String roomId,String token) {
        return String.format("%s?roomId=%s&token=%s", H5_ROOM_BASE_PATH, roomId, token);
    }

    public static String getSportLiveRoomUrl(String roomId,String token) {
        return String.format("%s?roomId=%s&token=%s", H5_SPORT_ROOM_BASE_PATH, roomId, token);
    }
}
