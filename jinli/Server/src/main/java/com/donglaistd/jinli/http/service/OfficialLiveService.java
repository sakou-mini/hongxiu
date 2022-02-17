package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.builder.GameBuilder;
import com.donglaistd.jinli.builder.LiveUserBuilder;
import com.donglaistd.jinli.builder.RoomBuilder;
import com.donglaistd.jinli.builder.UserBuilder;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.ServerRunningRecord;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.database.entity.rank.GiftLog;
import com.donglaistd.jinli.http.entity.OfficialLiveUserReturn;
import com.donglaistd.jinli.service.statistic.LiveMonitorProcess;
import com.donglaistd.jinli.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.LiveStatus.ONLINE;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.constant.CacheNameConstant.getRoomHistoryRecordKey;
import static com.donglaistd.jinli.constant.GameConstant.EMPTY_GAME_CLOSE_DELAY_TIME;

@Component
public class OfficialLiveService {
    private static final Logger logger = Logger.getLogger(OfficialLiveService.class.getName());
    @Autowired
    private DataManager dataManager;
    @Autowired
    private GiftLogDaoService giftLogDaoService;
    @Autowired
    private BloomRedisServer bloomRedisServer;
    @Autowired
    private OfficialLiveRecordDaoService officialLiveRecordDaoService;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    private ServerRunningRecordService serverRunningRecordService;
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    GameBuilder gameBuilder;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    UserBuilder userBuilder;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    LiveMonitorProcess liveMonitorProcess;

    public List<OfficialLiveRecord> getOpenOfficialLiveInfos(List<OfficialLiveRecord> liveRecords) {
        List<OfficialLiveRecord> officialLiveRecords = new ArrayList<>(liveRecords.size());
        for (OfficialLiveRecord liveRecord : liveRecords) {
            if (verifyUtil.checkLiveIsExist(liveRecord.getLiveUserId())) {
                LiveUser liveUser = dataManager.findLiveUser(liveRecord.getLiveUserId());
                Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
                officialLiveRecords.add(updateOfficialLiveRecord(room, liveRecord));
            }
        }
        return officialLiveRecords;
    }

    private OfficialLiveRecord updateOfficialLiveRecord(Room room, OfficialLiveRecord liveRecord) {
        liveRecord.setRoomId(room.getId());
        liveRecord.setRoomDisplayId(room.getDisplayId());
        liveRecord.setRoomName(room.getRoomTitle());
        liveRecord.setCurrentCount(room.getAllPlatformAudienceList().size());
        liveRecord.setHistoryCount(room.getAllAudienceHistory().size());
        liveRecord.setGiftCoinAmount(room.getTotalGiftCoin());
        return liveRecord;
    }

    public void totalNotNormalCloseOfficialLiveRecord(OfficialLiveRecord record, long closeTime, String backOfficeName) {
        LiveUser liveUser = dataManager.findLiveUser(record.getLiveUserId());
        GiftLog giftLog = giftLogDaoService.findByReceiveIdAndTimeBetween(liveUser.getUserId(), record.getRoomCreateDate(), closeTime);
        int giftCoinFlow = Objects.nonNull(giftLog) ? giftLog.getSendAmount() : 0;
        totalCloseOfficialLiveRecord(record, closeTime,giftCoinFlow, backOfficeName);
    }

    public void totalCloseOfficialLiveRecord(OfficialLiveRecord liveRecord, long closeTime,long giftAmount,String backOfficeName) {
        String key = getRoomHistoryRecordKey(liveRecord.getRoomId());
        long historyCount = bloomRedisServer.getBloomCount(key);
        bloomRedisServer.removeBloomFilter(key);
        liveRecord.setHistoryCount(historyCount);
        liveRecord.setGiftCoinAmount(giftAmount);
        liveRecord.setOpen(false);
        liveRecord.setRoomCloseDate(closeTime);
        liveRecord.setBackOfficeName(backOfficeName);
        officialLiveRecordDaoService.save(liveRecord);
    }

    public boolean closeOfficialLive(String liveUserId, String backOfficeName) {
        if (!verifyUtil.checkLiveIsExist(liveUserId)) return false;
        OfficialLiveRecord liveRecord = officialLiveRecordDaoService.findRecentOpenOfficialLiveByLiveUserId(liveUserId);
        if (Objects.isNull(liveRecord)) return false;
        Constant.ResultCode resultCode = liveMonitorProcess.closeLiveRoom(liveUserId,backOfficeName,EMPTY_GAME_CLOSE_DELAY_TIME, Constant.EndType.NORMAL_END);
        if (!resultCode.equals(SUCCESS)) {
            return false;
        }
        cleanOfficialRoom(liveRecord.getLiveUserId());
        return true;
    }

    public void cleanOfficialRoom(String liveUserId) {
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        dataManager.removeLiveUser(liveUser);
    }

    //when server restart close not close officialLive
    public void closeNotExitOfficialLive() {
        ServerRunningRecord serverRunningRecord = serverRunningRecordService.findNearestLastServerRunningRecord();
        long endTime = System.currentTimeMillis();
        if (Objects.nonNull(serverRunningRecord)) endTime = serverRunningRecord.getRecordTime();
        List<OfficialLiveRecord> records = officialLiveRecordDaoService.findByIsClose(true);
        if (!records.isEmpty())
            logger.info("start update abnormal officialLive");
        for (OfficialLiveRecord record : records) {
            if (!verifyUtil.checkLiveIsExist(record.getLiveUserId())) {
                totalNotNormalCloseOfficialLiveRecord(record, endTime, "SYSTEM");
                cleanOfficialRoom(record.getLiveUserId());
            }
        }
    }

    public boolean liveUserRepeat(String liveUserName,String roomDisplayId) {
        if(userDaoService.existByDisplayName(liveUserName)){
            User user = userDaoService.findByDisplayName(liveUserName);
            Room room = roomDaoService.findByDisplayId(roomDisplayId);
            if(Objects.nonNull(room)){
                LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
                if(Objects.nonNull(liveUser)) return !Objects.equals(liveUser.getUserId(), user.getId());
            }
            return true;
        } else{
            return false;
        }
    }

    public OfficialLiveUserReturn createLiveUser(String liveUserName, String liveUserImg, String roomName, String roomImg, String roomDisplayId) {
        //1.check roomId is used
        if(verifyUtil.checkOfficialRoomIdIsUsedByNormalUser(roomDisplayId)){
           return new OfficialLiveUserReturn(Constant.ResultCode.ROOM_ID_REPEATED);
        }
        //2.check roomTitle is used
        if(!roomDaoService.isNotUsedRoomTitle(roomName)) {
            return new OfficialLiveUserReturn(Constant.ResultCode.ROOM_TITLE_REPEATED);
        }
        //3.create User if not exit
        var userCount = userDaoService.count();
        Room room = roomDaoService.findByDisplayId(roomDisplayId);
        LiveUser liveUser = Objects.nonNull(room) ? liveUserDaoService.findById(room.getLiveUserId()) : null;
        User user;
        if(Objects.isNull(liveUser) || Objects.isNull(userDaoService.findById(liveUser.getUserId()))){
            user = userBuilder.createOfficialUser(StringUtils.generateTouristName(userCount), liveUserName, liveUserImg, 1000000);
        }else{
            user = userDaoService.findById(liveUser.getUserId());
        }
        user.setOnline(true);
        user.setAvatarUrl(liveUserImg);
        user.setDisplayName(liveUserName);
        user.setScriptUser(true);
        liveUser =  Objects.isNull(liveUser) ?  liveUserBuilder.create(user.getId(),ONLINE, Constant.PlatformType.PLATFORM_JINLI) : liveUser;
        liveUser.setScriptLiveUser(true);
        room = roomBuilder.createOfficeRoom(roomDisplayId, Constant.Pattern.LIVE_VIDEO, liveUser.getId(), roomName, "official Live room", roomImg,user.getId());
        room.initPlatformRoomData(Constant.PlatformType.PLATFORM_JINLI,user.getId(),0);
        user.setLiveUserId(liveUser.getId());
        user.setCurrentRoomId(room.getId());
        room.setStartDate(new Date());
        liveUser.setRoomId(room.getId());
        dataManager.saveRoom(room);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        OfficialLiveUserReturn back = new OfficialLiveUserReturn();
        back.liveUser = liveUser;
        back.resultCode = Constant.ResultCode.SUCCESS;
        back.room = room;
        return back;
    }

    public void createGame(LiveUser liveUser, Constant.GameType type, boolean banker) {
        BaseGame game = gameBuilder.createGame(type, liveUser, banker);
        DataManager.addGame(game);
        if (!(game instanceof EmptyGame)) {
            game.beginGameLoop(game.getBettingTime());
        }
    }
}
