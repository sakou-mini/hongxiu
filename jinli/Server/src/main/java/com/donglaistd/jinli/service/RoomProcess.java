package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.constant.CacheNameConstant;
import com.donglaistd.jinli.constant.GameConstant;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.dao.system.SystemMessageConfigDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import com.donglaistd.jinli.database.entity.system.SystemMessageConfig;
import com.donglaistd.jinli.util.*;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.donglaistd.jinli.constant.GameConstant.AUDIENCE_SIZE;
import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;
import static com.donglaistd.jinli.util.DataManager.getRoomFromChannel;
import static com.donglaistd.jinli.util.MessageUtil.*;

@Component
public class RoomProcess {
    @Value("${data.rank.coefficient}")
    private int COEFFICIENT;
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    BloomRedisServer bloomRedisServer;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    SystemMessageConfigDaoService systemMessageConfigDaoService;
    @Autowired
    LiveProcess liveProcess;

    //========================enterRoom============================
    public void quitIfHasEnterOtherRoom(ChannelHandlerContext ctx,User user){
        if (ctx.channel().attr(ROOM_KEY).get() != null) {
            var currentRoom = getRoomFromChannel(ctx);
            if (Objects.nonNull(currentRoom)) currentRoom.removeAudience(user);
        }
    }

    public Jinli.EnterRoomReply dealEnterRoom(Room room,User user){
        var liveUser = dataManager.findLiveUser(room.getLiveUserId());
        updateRoomUserData(room, user);
        broadcastEnterRoomMessage(room, user);
        //TODO 不使用布隆过滤器
        if(liveUser.isScriptLiveUser()) bloomRedisServer.addByDefaultBloomFilter(CacheNameConstant.ROOM_HISTORY_RECORD+":"+room.getId(),user.getId());
        BackOfficeSocketMessageService.sendRoomAudienceChangeBroadCast(room);
        return buildEnterRoomReply(room, user, liveUser);
    }

    private Jinli.EnterRoomReply buildEnterRoomReply(Room room, User user, LiveUser liveUser){
        Jinli.EnterRoomReply.Builder replyBuilder = Jinli.EnterRoomReply.newBuilder();
        String playingGameId = dataManager.findLiveUser(room.getLiveUserId()).getPlayingGameId();
        if (!StringUtils.isNullOrBlank(playingGameId)) {
            CardGame game = DataManager.findGame(playingGameId);
            if (Objects.nonNull(game)) replyBuilder.setGameType(game.getGameType());
        }
        boolean isFollow = Objects.nonNull(followListDaoService.findByFollowerIdAndFollower(room.getLiveUserId(),user.getId()));
        var hotValue = room.getAllPlatformAudienceList().size();
        var fanCount = followListDaoService.fetchHotValueByLiveUser(dataManager.findLiveUser(room.getLiveUserId()));
        var desc = Optional.ofNullable(liveUser.getLiveNotice()).orElse("");
        var liveUserInfo = GenerateLiveUserInfo(liveUser, userDaoService.findById(liveUser.getUserId()), fanCount);
        if(room.getStartDate() != null) replyBuilder.setRoomStartTime(room.getStartDate().getTime());
        if(liveUser.isScriptLiveUser()) replyBuilder.addAllLiveVideos(GameConstant.getOfficialVideos());
        SystemMessageConfig systemMessageConfig = systemMessageConfigDaoService.findSystemMessage(user.getPlatformType());
        if(systemMessageConfig != null) replyBuilder.setSystemTipMessage(systemMessageConfig.getSystemTipMessage());
        replyBuilder.setIsFollow(isFollow).setLiveUserInfo(liveUserInfo).setHotValue(hotValue * COEFFICIENT)
                .setTotalAmount(room.getCoinTotalCount()).setDescription(desc).setRoomId(room.getId())
                .setIsOfficialLiveUser(liveUser.isScriptLiveUser()).setRoomDisplayId(room.getDisplayId())
                .setPushUrl(liveProcess.getLivePushUrl(user, liveUser,room))
                .addAllPullUrl(liveProcess.getLivePullUrls(user, liveUser,room))
                .setTimeToEndLive(room.getTimeToEndLive()).setLiveMusic(room.getLiveMusic()).setLiveSourceLine(room.getLiveSourceLine())
                .addAllLiveRank(room.getAudienceLiveRankIdByLimit(AUDIENCE_SIZE)).setPattern(room.getPattern());
        return replyBuilder.build();
    }

    private void updateRoomUserData(Room room,User user){
        room.addAudience(user);
        List<String> audience = room.getAudienceByPlatform(user.getPlatformType());
        room.setPlatformTotalCount(userDaoService.countUserCoinByUserIds(audience), user.getPlatformType());
        DataManager.saveUserRoomRecord(user.getId(), new UserRoomRecord(System.currentTimeMillis(), room.getId(),user.getId()));
        dataManager.saveUserEnterRoomRecord(user.getId(), room.getLiveUserId());
        user.setCurrentRoomId(room.getId());
        dataManager.saveUser(user);
    }

    private void broadcastEnterRoomMessage(Room room,User user){
        var enterRoomMessage = Jinli.EnterRoomBroadcastMessage.newBuilder().setDisplayName(user.getDisplayName())
                .setUserId(user.getId()).setRoomId(room.getId()).setTotalCount(room.getCoinTotalCount()).setVipId(user.getVipType())
                .setHotValue(room.getAllAudience().size() * COEFFICIENT).setUserLevel(user.getLevel());
        room.broadCastToAllPlatform(buildReply(enterRoomMessage));
    }


    public Jinli.RoomInfo buildLivingRoomInfo(Room room) {
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        if( Objects.isNull(liveUser) || Objects.isNull(liveUser.getPlayingGameId())) return null;
        BaseGame game = (BaseGame) DataManager.findGame(liveUser.getPlayingGameId());
        User user = userDaoService.findById(liveUser.getUserId());
        if(Objects.isNull(game) || Objects.isNull(user)) return null;
        var builder = Jinli.RoomInfo.newBuilder().setId(room.getId())
                .setRoomTitle(room.getRoomTitle()).setDescription(room.getDescription()).setRoomImage(room.getRoomImage())
                .setHotValue(room.getAllAudience().size() * COEFFICIENT).setGameType(game.getGameType())
                .setPattern(room.getPattern()).setTotalAmount(room.getCoinTotalCount()).setUserId(liveUser.getUserId())
                .setLiveUserInfo(GenerateLiveUserInfo(liveUser, user,0));
        return builder.build();
    }

    public List<Jinli.LiveRank> buildLiveRankByLiveRankPair(List<Pair<String,Integer>> liveRankPair) {
        List<Jinli.LiveRank> liveRanks = new ArrayList<>();
        User user;
        Jinli.LiveRank liveRank;
        for (Pair<String, Integer> rankPair : liveRankPair) {
            user = userDaoService.findById(rankPair.getLeft());
            liveRank = Jinli.LiveRank.newBuilder().setUserId(user.getId()).setLevel(user.getLevel())
                    .setDisplayName(user.getDisplayName()).setAvatarUrl(user.getAvatarUrl()).setValue(rankPair.getRight()).build();
            liveRanks.add(liveRank);
        }
        return liveRanks;
    }

    private Jinli.QuitRoomBroadcastMessage buildQuitRoomMessage(Room room, User user) {
        room.setTotalCoinCount(userDaoService.countUserCoinByUserIds(room.getAllPlatformAudienceList()));
        int hotValue = Optional.ofNullable(room.getAllPlatformAudienceList()).map(List::size).orElse(0);
        return Jinli.QuitRoomBroadcastMessage.newBuilder().setTotalCount(room.getCoinTotalCount()).setRoomId(room.getId()).setHotValue(hotValue * COEFFICIENT).setUserId(user.getId())
                .addAllLiveRank(room.getAudienceLiveRankIdByLimit(AUDIENCE_SIZE)).build();
    }

    public void sendQuiteRoomMessage(Room room,User quitUser){
        Jinli.JinliMessageReply messageReply = buildReply(buildQuitRoomMessage(room, quitUser));
        room.broadCastToAllPlatform(messageReply);
        sendMessage(quitUser.getId(),messageReply);
    }

    // =================Deal StartLive===================
    public void initStartLiveRoom(Room room, LiveUser liveUser, Jinli.StartLiveRequest request) {
        liveUser.setLiveStatus(Constant.LiveStatus.ONLINE);
        liveUser.setRtmpCode(Utils.createUUID());
        liveUser.setLastLiveTime(System.currentTimeMillis());
        room.cleanRoom();
        room.setLiveSourceLine(request.getLiveSourceLine());
        room.setRoomTitle(request.getRoomTitle());
        room.setStartDate(new Date(request.getRoomStartTime()));
        room.resetTotalBetAmount();
        room.setPattern(request.getPattern());
        room.setLiveDomain(request.getLiveDomain());
        Map<Constant.PlatformType, List<FollowList>> fansMap = followListDaoService.groupFolloweeByPlatform(liveUser);
        for (Jinli.LivePlatformParam livePlatformParam : request.getLivePlatformParamsList()) {
            int fansNum = fansMap.getOrDefault(livePlatformParam.getPlatform(), new ArrayList<>()).size();
            room.initPlatformRoomData(livePlatformParam.getPlatform(),liveUser.getUserId(),fansNum, livePlatformParam.getPlatformGameCode(),livePlatformParam.getPlatformRoomCode());
        }
        dataManager.saveRoom(room);
        dataManager.saveLiveUser(liveUser);
    }

    //=============================mutechat operation==================================
    public static Jinli.MuteChatBroadcastMessage buildMuteChatBroadcastMessage(String userId,MuteProperty muteProperty){
        return Jinli.MuteChatBroadcastMessage.newBuilder().setUserId(userId)
                .setIsMuteChat(true).setIdentity(muteProperty.getMuteOptIdentity()).setMuteReason(muteProperty.getMuteReason())
                .setMuteTime(muteProperty.getMuteTimeType()).setMuteArea(muteProperty.getMuteArea()).build();
    }

    public static Jinli.MuteChatBroadcastMessage buildUnMuteChatBroadcastMessage(String userId, Constant.MuteIdentity muteIdentity){
        Jinli.MuteChatBroadcastMessage.Builder builder = Jinli.MuteChatBroadcastMessage.newBuilder().setUserId(userId)
                .setIsMuteChat(false).setIdentity(muteIdentity);
        return builder.build();
    }

    public static Constant.MuteIdentity getMuteIdentify(User user,Room room){
        if (room.validateIsLiveUser(user.getLiveUserId())) {
            return Constant.MuteIdentity.IDENTITY_LIVEUSER;
        }else if (room.getAdministrators().contains(user.getId())){
            return Constant.MuteIdentity.IDENTITY_ADMINISTRATOR;
        }
        return Constant.MuteIdentity.IDENTITY_NULL;
    }
}
