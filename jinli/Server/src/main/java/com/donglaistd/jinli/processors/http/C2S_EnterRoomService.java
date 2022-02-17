package com.donglaistd.jinli.processors.http;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveDetail;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.http.message.HttpMessageReply;
import com.donglaistd.jinli.http.message.HttpMessageRequest;
import com.donglaistd.jinli.util.DataManager;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.util.MessageUtil.buildHttpMessageReply;

@Component
public class C2S_EnterRoomService extends HttpMessageService {

    @Autowired
    DataManager dataManager;
    @Autowired
    UserDaoService userDaoService;

    @Override
    protected HttpMessageReply doHandle(ChannelHandlerContext ctx, HttpMessageRequest message) {
        Object request = message.getRequest();
        EnterRoomRequest enterRoomRequest = new Gson().fromJson(request.toString(), EnterRoomRequest.class);
        String roomId = enterRoomRequest.getRoomId();
        Room room = DataManager.findOnlineRoom(roomId);
        if(Objects.isNull(room))
            return buildErrorMessageReply(message.getMessageId(), Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        DataManager.saveRoomIdToHttpChannel(ctx,roomId);
        return buildHttpMessageReply(message.getMessageId(),Constant.ResultCode.SUCCESS,buildLiveRoomDetail(room));
    }

    private LiveDetail buildLiveRoomDetail(Room room) {
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
        User user = userDaoService.findById(liveUser.getUserId());
        return new LiveDetail().setUserId(liveUser.getUserId())
                .setDisplayName(user.getDisplayName())
                .setLiveUserId(liveUser.getId())
                .setRoomId(room.getId()).setRoomDisplayId(room.getDisplayId())
                .setAudienceNum(room.getAllPlatformAudienceList().size())
                .setAudienceCount(room.getAllAudience().size()).setGameType(game.getGameType())
                .setGameIncome(room.getTotalBetAmount())
                .setGiftIncome(room.getTotalGiftCoin())
                .setRoomTitle(room.getRoomTitle())
                .setActiveNum(room.getRoomAllChatUserNum())
                .setChatNum(room.getRoomAllChatHistory().size())
                .setLiveStartTime(room.getStartDate().getTime())
                .setHot(room.isHot()).setLivePattern(room.getPattern()).setSharedPlatform(room.getAllSharedPlatform());
    }

    static class EnterRoomRequest{
        private String roomId;

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }
    }
}
