package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.*;
import com.donglaistd.jinli.config.QPlatformGameConfig;
import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_Q;
import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_T;
import static com.donglaistd.jinli.constant.GameConstant.*;

@Component
public class PlatformLiveService {
    @Autowired
    private RoomBuilder roomBuilder;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    DataManager dataManager;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;
    @Autowired
    QPlatformGameConfigBuilder qPlatformGameConfigBuilder;

    public LiveUser createPlatformLive(User platformUser,String roomTitle,String roomImage) {
        LiveUser liveUser = liveUserBuilder.create(platformUser.getId(), Constant.LiveStatus.ONLINE ,platformUser.getPlatformType());
        Room room = roomBuilder.create(liveUser.getId(), platformUser.getId(),roomTitle, "",roomImage);
        room.setStartDate(new Date());
        room.setPattern(Constant.Pattern.LIVE_AUDIO);
        room.initPlatformRoomData(platformUser.getPlatformType(),platformUser.getId(),0);
        if(Objects.equals(platformUser.getPlatformType(),PLATFORM_Q)){
            QPlatformGameConfig qPlatformGameConfig = qPlatformGameConfigBuilder.getAllQplatformGames().get(0);
            room.initPlatformRoomData(platformUser.getPlatformType(),platformUser.getId(),0,qPlatformGameConfig.getGameCode(),qPlatformGameConfig.getRoomCode());
        }else {
            room.initPlatformRoomData(platformUser.getPlatformType(),platformUser.getId(),0);
        }
        liveUser.setRoomId(room.getId());
        liveUser.setRtmpCode(Utils.createUUID());
        liveUser.setScriptLiveUser(true);
        platformUser.setLiveUserId(liveUser.getId());
        platformUser.setCurrentRoomId(room.getId());
        //create Game
        BaseGame game = gameBuilder.createGame(Constant.GameType.JIAOYOU, liveUser, false);
        dataManager.saveUser(platformUser);
        dataManager.saveLiveUser(liveUser);
        dataManager.saveRoom(room);
        DataManager.addGame(game);
        return liveUser;
    }

    private LiveUser initPlatformLiveByPlatformName(String accountName, Constant.PlatformType platform,String roomTitle,String roomImage,String userName,String avatar) {
        User user = userDaoService.findByAccountName(accountName);
        LiveUser platformLiveUser;
        if (user == null) {
            user = userBuilder.createUser(accountName, userName,avatar, "",true);
        }
        user.setScriptUser(true);
        user.setPlatformType(platform);
        user.setDisplayName(userName);
        user.setAvatarUrl(avatar);
        if(StringUtils.isNullOrBlank(user.getLiveUserId()) ||  dataManager.findLiveUser(user.getLiveUserId()) == null){
            platformLiveUser = createPlatformLive(user, roomTitle, roomImage);
        } else {
            platformLiveUser = dataManager.findLiveUser(user.getLiveUserId());
            if(platformLiveUser.getPlayingGameId()== null || DataManager.findOnlineRoom(platformLiveUser.getRoomId()) == null){
                Room room = roomDaoService.findByLiveUser(platformLiveUser);
                room.setRoomTitle(roomTitle);
                room.setStartDate(new Date());
                room.setRoomImage(roomImage);
                room.setPattern(Constant.Pattern.LIVE_AUDIO);
                if(Objects.equals(platform,PLATFORM_Q)){
                    QPlatformGameConfig qPlatformGameConfig = qPlatformGameConfigBuilder.getAllQplatformGames().get(0);
                    room.initPlatformRoomData(user.getPlatformType(),user.getId(),0,qPlatformGameConfig.getGameCode(),qPlatformGameConfig.getRoomCode());
                }else {
                    room.initPlatformRoomData(user.getPlatformType(),user.getId(),0);
                }
                user.setCurrentRoomId(room.getId());
                platformLiveUser.setPlatformType(user.getPlatformType());
                platformLiveUser.setRtmpCode(Utils.createUUID());
                BaseGame game = gameBuilder.createGame(Constant.GameType.JIAOYOU, platformLiveUser, false);
                DataManager.addGame(game);
                platformLiveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
                dataManager.saveRoom(room);
                dataManager.saveLiveUser(platformLiveUser);
                dataManager.saveUser(user);
            }
        }
        return platformLiveUser;
    }


    public List<LiveUser> initPlatformLiveT(int num){
        List<LiveUser> liveUsers = new ArrayList<>();
        String roomImage;
        String avatar;
        for (int i = 1; i <= num; i++) {
            avatar = PLATFORM_T_OFFICIAL_AVATAR_IMAGE + i + ".png";
            roomImage = PLATFORM_T_OFFICIAL_ROOM_IMAGE + i + ".jpg";
            liveUsers.add(initPlatformLiveByPlatformName(PLATFORM_T_OFFICIAL_LIVE_NAME+i, PLATFORM_T,"音乐电台"+i,roomImage,
                    PLATFORM_T_OFFICIAL_DISPLAYNAME_LIST.get(i - 1),avatar));
        }
        return liveUsers;
    }

   /* public List<LiveUser> initPlatformQLive(){
        List<BackOfficeUser> backOfficeUsers = backOfficeUserDaoService.findAll();
        if(backOfficeUsers==null) return null;
        BackOfficeUser platformQAccount = backOfficeUsers.stream().filter(backOfficeUser -> backOfficeUser.getRoles().contains(PLATFORM_Q)).findFirst().orElse(null);
        if(platformQAccount == null) return new ArrayList<>(0);
        LiveUser liveUser = initPlatformLiveByPlatformName(platformQAccount.getAccountName(), Constant.PlatformType.PLATFORM_Q,"Q平台官方直播间Q","","","");
        dataManager.saveLiveUser(liveUser);
        return Lists.newArrayList(liveUser);
    }*/

    public List<LiveUser> initPlatformLiveQ(int num){
        List<LiveUser> liveUsers = new ArrayList<>();
        String roomImage;
        String avatar;
        for (int i = 1; i <= num; i++) {
            avatar = PLATFORM_Q_OFFICIAL_AVATAR_IMAGE + i + ".png";
            roomImage = PLATFORM_Q_OFFICIAL_ROOM_IMAGE + i + ".jpg";
            liveUsers.add(initPlatformLiveByPlatformName(PLATFORM_Q_OFFICIAL_LIVE_NAME+i, PLATFORM_Q,"音乐电台"+i,roomImage,
                    PLATFORM_Q_OFFICIAL_DISPLAYNAME_LIST.get(i - 1),avatar));
        }
        return liveUsers;
    }
}
