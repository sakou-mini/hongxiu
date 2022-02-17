package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.LiveGameInfo;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class GameRecoverService {
    private Logger logger = Logger.getLogger(GameRecoverService.class.getName());
    @Autowired
    DataManager dataManager;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    FollowListDaoService followListDaoService;

    public void recoverLiveGame(User user, String liveUserId, Jinli.LoginReply.Builder loginReply){
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(Objects.isNull(liveUser))  dataManager.removeEnterRoomRecord(user.getId());
        LiveGameInfo liveGameInfo = liveUser.getLiveGameInfo();
        if(Objects.isNull(liveGameInfo) || StringUtils.isNullOrBlank(liveUser.getPlayingGameId())){
            dataManager.removeEnterRoomRecord(user.getId());
            return;
        }
        BaseGame game = (BaseGame) DataManager.findGame(liveUser.getPlayingGameId());
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if(Objects.isNull(game) && Objects.isNull(room)){
            room = roomDaoService.findByLiveUser(liveUser);
            game = recoverGameAndRoom(room, liveUser);
            user.setCurrentRoomId(room.getId());
            dataManager.saveUser(user);
        }
        else if(game == null || (room != null && !room.isLive())) return;
        loginReply.clearBankerReconnect();  //only one Reconnect
        Jinli.ReconnectLiveMessage reconnectLiveMessage = generateReconnectLiveMessage(room, liveUser, game);
        if(Objects.equals(user.getId(),liveUser.getUserId())){
            loginReply.setLiveUserReconnect(reconnectLiveMessage);
            logger.warning("recover liveUser live--------->");
        }else {
            loginReply.setUserReconnect(reconnectLiveMessage);
            logger.warning("recover user join--------->");
        }
    }

    public BaseGame recoverGameAndRoom(Room room,LiveUser liveUser){
        LiveGameInfo liveGameInfo = liveUser.getLiveGameInfo();
        room.setStartDate(new Date());
        recoverPlatformParam(room, liveGameInfo,liveUser);
        liveUser.setRtmpCode(Utils.createUUID());
        dataManager.saveRoom(room);
        BaseGame game = gameBuilder.createGame(liveGameInfo.getGameType(), liveUser, liveGameInfo.isBankerGame());
        DataManager.addGame(game);
        if(!(game instanceof EmptyGame)) game.startGame();
        return game;
    }

    private void recoverPlatformParam(Room room, LiveGameInfo liveGameInfo,LiveUser liveUser){
        for (LiveGameInfo.PlatformRoomParam platformRoomParam : liveGameInfo.getPlatformRoomParams()) {
            int fansNum = followListDaoService.groupFolloweeByPlatform(liveUser).getOrDefault(platformRoomParam.getPlatform(),new ArrayList<>()).size();
            room.initPlatformRoomData(platformRoomParam.getPlatform(), liveUser.getUserId(), fansNum, platformRoomParam.getOtherPlatformGameCode(), platformRoomParam.getOtherPlatformRoomCode());
        }
    }

    public Jinli.ReconnectLiveMessage generateReconnectLiveMessage(Room room, LiveUser liveUser, CardGame game){
        Jinli.ReconnectLiveMessage.Builder reconnectLiveBuilder = Jinli.ReconnectLiveMessage.newBuilder().setPattern(room.getPattern()).setRoomId(room.getId())
                .setCurrentLiveUserId(liveUser.getUserId()).setGameType(game.getGameType());
        if(!StringUtils.isNullOrBlank(liveUser.getRtmpCode())){
            reconnectLiveBuilder.setRtmpCode(liveUser.getRtmpCode());
        }
        return reconnectLiveBuilder.build();
    }
}
