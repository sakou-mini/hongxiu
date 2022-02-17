package com.donglaistd.jinli.http.message;

import java.util.HashMap;
import java.util.Map;

public enum HttpMessageIdEnum {
    C2S_EnterRoom(1000),
    S2C_EnterRoom(1001),
    S2C_RoomAudienceChangeBroadCast(1003),
    S2C_ChatMessageBroadCast(1004),
    S2C_SendGiftMessageBroadCast(1005),
    S2C_LiveEndMessageBroadCast(1006),
    S2C_MuteChatMessageBroadCast(1007);
    public int msgId;
    HttpMessageIdEnum(int msgId) {
        this.msgId = msgId;
    }

    public static HttpMessageIdEnum valueOf(int typeValue){
        switch (typeValue){
            case 1000: return C2S_EnterRoom;
            case 1001: return S2C_EnterRoom;
            case 1003: return S2C_RoomAudienceChangeBroadCast;
            case 1004: return S2C_ChatMessageBroadCast;
            case 1005: return S2C_SendGiftMessageBroadCast;
            case 1006: return S2C_LiveEndMessageBroadCast;
            case 1007: return S2C_MuteChatMessageBroadCast;
            default: return null;
        }
    }

    public int getMsgId() {
        return msgId;
    }

    public static class RequestMapper {
        private static final Map<HttpMessageIdEnum, String> requestMapper = new HashMap<>();
        static {
            for (HttpMessageIdEnum value : HttpMessageIdEnum.values()) {
                requestMapper.put(value, value.name());
            }
        }
        public static String getRequestMessageById(int msgId){
            return requestMapper.get(HttpMessageIdEnum.valueOf(msgId));
        }
    }
}
