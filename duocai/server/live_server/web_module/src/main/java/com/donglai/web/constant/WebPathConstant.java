package com.donglai.web.constant;

public class WebPathConstant {
    public static final String H5_ROOM_BASE_PATH = "/duocai/client/h5/web-mobile/index.html";

    public static String getPlatformLiveRoomUrl(String roomId,String token){
        return String.format("%s?roomId=%s&token=%s", H5_ROOM_BASE_PATH, roomId, token);
    }
}
