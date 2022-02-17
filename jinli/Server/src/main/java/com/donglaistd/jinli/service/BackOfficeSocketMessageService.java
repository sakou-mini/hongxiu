package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.http.entity.live.ChatMessage;
import com.donglaistd.jinli.http.entity.statistic.UserGiftDetailData;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.MessageUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.donglaistd.jinli.http.message.HttpMessageIdEnum.*;
import static com.donglaistd.jinli.util.MessageUtil.buildHttpMessageReply;

@Service
public class BackOfficeSocketMessageService {
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    DataManager dataManager;

    public static void sendRoomAudienceChangeBroadCast(Room room){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("audienceNum", room.getAllPlatformAudienceList().size());
        jsonObject.put("roomId", room.getId());
        MessageUtil.sendHttpMessageTextWebByRoomId(room.getId(),buildHttpMessageReply(S2C_RoomAudienceChangeBroadCast.msgId, Constant.ResultCode.SUCCESS,jsonObject));
    }

    public void broadCastChatMessageToHttpSocket(String roomId, User user, MessageRecord messageRecord){
        MessageUtil.sendHttpMessageTextWebByRoomId(roomId,buildHttpMessageReply(S2C_ChatMessageBroadCast.msgId, Constant.ResultCode.SUCCESS,new ChatMessage(messageRecord, user)));
    }

    public void broadCastSendGiftMessageToHttpSocket(User sendUser , GiftLog giftLog) {
        LiveUser liveUser = liveUserDaoService.findByUserId(giftLog.getReceiveId());
        UserGiftDetailData userGiftDetailData = new UserGiftDetailData(sendUser, giftLog);
        MessageUtil.sendHttpMessageTextWebByRoomId(liveUser.getRoomId(),buildHttpMessageReply(S2C_SendGiftMessageBroadCast.msgId, Constant.ResultCode.SUCCESS,userGiftDetailData));
    }

    public static void broadCastMuteChatMessageToHttpSocket(String roomId){
        MessageUtil.sendHttpMessageTextWebByRoomId(roomId,buildHttpMessageReply(S2C_MuteChatMessageBroadCast.msgId,Constant.ResultCode.SUCCESS));
    }

    public static void broadCastLiveEndMessageToHttpSocket(Room room){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("roomId", room.getDisplayId());
        MessageUtil.sendHttpMessageTextWebByRoomId(room.getId(),buildHttpMessageReply(S2C_LiveEndMessageBroadCast.msgId, Constant.ResultCode.SUCCESS,jsonObject));
    }
}
