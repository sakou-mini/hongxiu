package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.service.BackOfficeSocketMessageService;
import com.donglaistd.jinli.service.RoomProcess;
import com.donglaistd.jinli.service.TaskProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.ROOM_DOES_NOT_EXIST;
import static com.donglaistd.jinli.util.DataManager.closeRoomInfo;
import static com.donglaistd.jinli.util.DataManager.saveRoomKeyToChannel;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class QuitRoomRequestHandler extends MessageHandler {
    @Autowired
    DataManager dataManager;
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    RoomProcess roomProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QuitRoomRequest request = messageRequest.getQuitRoomRequest();
        var replyBuilder = Jinli.QuitRoomReply.newBuilder();
        var roomId = request.getRoomId();
        Room room = DataManager.roomMap.get(roomId);
        room = Objects.isNull(room) ? closeRoomInfo.get(roomId) : room;
        if (Objects.isNull(room)) {
            resultCode = ROOM_DOES_NOT_EXIST;
        }else{
            var banker = quitBankerIfIsBanker(room, user);
            if (banker == null) quitRoomIfNotBanker(user, room);
            resultCode = Constant.ResultCode.SUCCESS;
            if (!StringUtils.isNullOrBlank(user.getLiveUserId()) && dataManager.findLiveUser(user.getLiveUserId()) != null)
                saveRoomKeyToChannel(ctx, dataManager.findLiveUser(user.getLiveUserId()).getRoomId());
            else
                saveRoomKeyToChannel(ctx, null);
            taskProcess.totalWatchLiveTime(user, room);
            BackOfficeSocketMessageService.sendRoomAudienceChangeBroadCast(room);
        }
        return buildReply(replyBuilder, resultCode);
    }

    private User quitBankerIfIsBanker(Room room, User user) {
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        if (Objects.isNull(liveUser) || StringUtils.isNullOrBlank(liveUser.getPlayingGameId())) return null;
        CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
        if (game instanceof BankerGame) {
            BankerGame bankerGame = (BankerGame) game;
            bankerGame.quitBanker(user, true);
            if(Objects.equals(bankerGame.getBanker(),user)) return user;
        }
        return null;
    }

    private void quitRoomIfNotBanker(User user, Room room){
        room.removeAudience(user);
        room.removeConnectLiveCodeByUser(user);
        user.cleanCurrentRoomId();
        dataManager.saveUser(user);
        roomProcess.sendQuiteRoomMessage(room,user);
        dataManager.removeEnterRoomRecord(user.getId());
    }

}
