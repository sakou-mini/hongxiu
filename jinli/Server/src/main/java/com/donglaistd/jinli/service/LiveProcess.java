package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveWatchRecordDaoService;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.database.entity.statistic.record.UserRoomRecord;
import com.donglaistd.jinli.event.GameEndEvent;
import com.donglaistd.jinli.event.LiveRecordEvent;
import com.donglaistd.jinli.http.service.OfficialLiveService;
import com.donglaistd.jinli.listener.SwitchGameListener;
import com.donglaistd.jinli.processors.handler.SwitchGameRequestHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.ScheduledTaskUtil;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.VerifyUtil;
import com.donglaistd.jinli.util.live.LiveStream;
import com.donglaistd.jinli.util.live.LiveStreamFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.LiveStatus.UNAPPROVED;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class LiveProcess {
    private static final Logger logger = Logger.getLogger(LiveProcess.class.getName());
    @Value("${data.liveroom.recommendroom.count}")
    private int MAX_RECOMMEND_COUNT;
    @Autowired
    DataManager dataManager;
    @Autowired
    MusicListDaoService musicListDaoService;
    @Autowired
    OfficialLiveService officialLiveService;
    @Autowired
    QueueProcess queueProcess;
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;
    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    VerifyUtil verifyUtil;


    //====================================end live================================
    public Constant.ResultCode verifyEndLive(LiveUser liveUser){
        if (liveUser == null) return ROOM_DOES_NOT_EXIST;
        if (Objects.equals(liveUser.getLiveStatus(),UNAPPROVED)) return LIVE_PLAYER_UNAPPROVED;
        Room room =  DataManager.findOnlineRoom(liveUser.getRoomId());
        BaseGame game = (BaseGame) DataManager.findGame(liveUser.getPlayingGameId());
        if (Objects.isNull(room)) return ROOM_DOES_NOT_EXIST;
        else if (Objects.isNull(game)) return GAME_NOT_EXISTS;
        else if (Objects.equals(game.getNextGameStatue(), Constant.GameStatus.BETTING)) return GAME_IS_NOT_OVER;
        return SUCCESS;
    }

    public void delayEndLiveGame(long delayMillTime, LiveUser liveUser, Constant.EndType endType){
        Room room =  DataManager.findOnlineRoom(liveUser.getRoomId());
        BaseGame game = (BaseGame) DataManager.findGame(liveUser.getPlayingGameId());
        if(game instanceof EmptyGame){
            if(room.getTimeToEndLive() <= 0){
                room.setTimeToEndLive(System.currentTimeMillis() + delayMillTime);
                dataManager.saveRoom(room);
                ScheduledTaskUtil.schedule(() -> {
                    endAndCleanLiveRoom(game, liveUser, room, endType);
                    EventPublisher.publish(new LiveRecordEvent(room,game));
                }, delayMillTime);
                broadCastReadyEndLiveMessage(room);
            }
        } else endAndCleanLiveRoom(game, liveUser, room, endType);
    }

    private void endAndCleanLiveRoom(BaseGame game,LiveUser liveUser,Room room,Constant.EndType endType){
        cleanSwitchGameListener(game);
        liveUser.cleanLive();
        dataManager.removeEnterRoomRecord(liveUser.getUserId());
        DataManager.removeOnlineRoom(room.getId());
        DataManager.closeRoomInfo.put(room.getId(), room);
        dataManager.saveLiveUser(liveUser);
        dataManager.removeLiveUser(liveUser);
        stopMusic(liveUser.getUserId());
        if(game instanceof  EmptyGame) DataManager.removeGame(game.getGameId());
        var endLiveBroadcastBuilder = Jinli.EndLiveBroadcastMessage.newBuilder().setEndType(endType);
        room.broadCastToAllPlatform(buildReply(endLiveBroadcastBuilder));
        //房间内剩余观众直播观看记录
        for (String audience : room.getAllAudience()) {
            UserRoomRecord userRoomRecord = DataManager.getUserRoomRecord(audience);
            if(userRoomRecord.getEnterRoomTime() <= 0 || !Objects.equals(userRoomRecord.getRoomId(),room.getId())) continue;
            recordWatchLiveIfNotLiveUser(dataManager.findUser(audience),room,userRoomRecord);
            DataManager.cleanUserRoomRecord(audience);
            dataManager.removeEnterRoomRecord(audience);
        }
    }

    private void stopMusic(String userId){
        MusicList musicList = musicListDaoService.findByUserId(userId);
        if(musicList!=null){
            musicList.resetPlayMusic();
            musicListDaoService.save(musicList);
        }
    }

    private void cleanSwitchGameListener(BaseGame game) {
        SwitchGameListener switchGameListener = SwitchGameRequestHandler.gameListenerMap.remove(game);
        if (Objects.nonNull(switchGameListener)) {
            EventPublisher.removeListener(GameEndEvent.class, switchGameListener);
        }
    }

    private void broadCastReadyEndLiveMessage(Room room){
        Jinli.TimeToEndLiveBroadcastMessage.Builder builder = Jinli.TimeToEndLiveBroadcastMessage.newBuilder().setTimeToEndLive(room.getTimeToEndLive());
        room.broadCastToAllPlatform(buildReply(builder));
    }

    //=============== get push or pull LiveUrl======================

    public String getLiveSecret(User user, LiveUser liveUser, Room room) {
        Constant.LiveSourceLine line = room.getLiveSourceLine();
        String liveDomain = room.getLiveDomain();
        logger.info("line is----------->:" + line +"use domain is:"+ liveDomain);
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(line);
        if(Objects.isNull(liveStream)) return "";
        String address = Objects.equals(user.getId(), liveUser.getUserId()) ?
                liveStream.getRtmpPushUrl(liveDomain, liveUser.getLiveUrl(),liveUser) :
                liveStream.getRtmpPullUrl(liveDomain, liveUser.getLiveUrl(),liveUser);
        if(Objects.equals(user.getId(),liveUser.getUserId())) {
            logger.info("push Url is:" + address);
        }else
            logger.info("pull Url is:" + address);
        return address;
    }

    public String getLivePushUrl(User user, LiveUser liveUser, Room room) {
        Constant.LiveSourceLine line = room.getLiveSourceLine();
        String liveDomain = room.getLiveDomain();
        logger.info("line is----------->:" + line +"use domain is:"+ liveDomain);
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(line);
        if(Objects.isNull(liveStream)) return "";
        String pushUrl = liveStream.getRtmpPushUrl(liveDomain, liveUser.getLiveUrl(), liveUser);
        logger.info("push Url is:" + pushUrl);
        return pushUrl;
    }

    public List<String> getLivePullUrls(User user, LiveUser liveUser, Room room) {
        Constant.LiveSourceLine line = room.getLiveSourceLine();
        String liveDomain = room.getLiveDomain();
        logger.info("line is----------->:" + line +"use domain is:"+ liveDomain);
        LiveStream liveStream = LiveStreamFactory.getLiveStreamByLiveSourceLine(line);
        if(Objects.isNull(liveStream)) return new ArrayList<>(0);
        String pushUrl = liveStream.getRtmpPullUrl(liveDomain, liveUser.getLiveUrl(), liveUser);
        logger.info("pull Url is:" + pushUrl);
        return liveStream.getAllShareRtmpPullUrl(liveDomain, liveUser.getLiveUrl());
    }

    //=============== RecommendLiveUser======================
    public List<LiveUser> getRecommendLiveUserListByUser(User user,Set<LiveUser> leaders){
        Set<String>  leaderIds = leaders.stream().map(LiveUser::getId).collect(Collectors.toSet());
        List<LiveUser> recommendList = new ArrayList<>();
        var startTime = Calendar.getInstance().getTime().getTime();
        while (recommendList.size() < MAX_RECOMMEND_COUNT) {
            LiveUser randomLiveUser = dataManager.randomLiveUser(user.getPlatformType(),leaderIds);
            if(Objects.isNull(randomLiveUser)) break;
            recommendList.add(randomLiveUser);
            leaderIds.add(randomLiveUser.getId());
            if (Calendar.getInstance().getTime().getTime() - startTime > 10) break;
        }
        Collections.shuffle(recommendList);
        return recommendList;
    }

    //record Live watch record
    public void recordWatchLiveIfNotLiveUser(User user, Room room, UserRoomRecord userRoomRecord) {
        if (Objects.nonNull(user) && Objects.nonNull(room) && !Objects.equals(room.getLiveUserId(), user.getLiveUserId()) && !verifyUtil.checkIsLiveUser(user)) {
            UserDataStatistics userRecentLoginInfo = userDataStatisticsDaoService.findUserRecentLoginInfo(user.getId());
            if (userRecentLoginInfo != null) {
                LiveWatchRecord liveWatchRecord = liveWatchRecordDaoService.save(new LiveWatchRecord(userRoomRecord, System.currentTimeMillis(), userRecentLoginInfo.getBrand(), user.getPlatformType()
                        , room.getDisplayId(), user.getLastIp(), room.getLiveUserId()));
                userAttributeDaoService.updateUserWatchLiveInfo(user.getId(),liveWatchRecord.getWatchTime());
            }
        }
    }

    public Jinli.LiveUserInfo buildLiveUserInfo(LiveUser liveUser,Room room){
        User user = dataManager.findUser(liveUser.getUserId());
        var liveUserInfo = Jinli.LiveUserInfo.newBuilder();
        int fansNum = followListDaoService.fetchHotValueByLiveUser(liveUser);
        liveUserInfo.setUserId(user.getId());
        liveUserInfo.setRoomId(room.getId());
        liveUserInfo.setRoomDisplayId(room.getDisplayId());
        liveUserInfo.setAvatarUrl(user.getAvatarUrl());
        liveUserInfo.setHotValue(fansNum);
        liveUserInfo.setStatus(liveUser.getLiveStatus());
        liveUserInfo.setDisplayName(user.getDisplayName());
        liveUserInfo.setLevel(liveUser.getLevel());
        if(!StringUtils.isNullOrBlank(room.getDescription()))
            liveUserInfo.setDescription(room.getDescription());
        liveUserInfo.setLiveUrl(liveUser.getLiveUrl());
        liveUserInfo.setFanCount(fansNum);
        liveUserInfo.setRoomImage(room.getRoomImage());
        liveUserInfo.setLiveUserId(liveUser.getId());
        liveUserInfo.setPlatformType(liveUser.getPlatformType());

        if(!StringUtils.isNullOrBlank(liveUser.getLiveNotice()))
            liveUserInfo.setLiveNotice(liveUser.getLiveNotice());
        liveUserInfo.addAllDisablePermissions(liveUser.getDisablePermissions());
        List<Constant.PlatformType> sharedPlatform = new ArrayList<>(liveUser.getSharedPlatform());
        sharedPlatform.add(0, liveUser.getPlatformType());
        liveUserInfo.addAllSharedPlatform(sharedPlatform).setQuickChat(liveUser.getQuickChat());
        return liveUserInfo.build();
    }
}
