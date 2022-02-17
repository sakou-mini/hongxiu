package com.donglaistd.jinli.listener;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveRecordDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.OfficialLiveRecordDaoService;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.event.BaseEvent;
import com.donglaistd.jinli.event.LiveRecordEvent;
import com.donglaistd.jinli.http.service.OfficialLiveService;
import com.donglaistd.jinli.util.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.donglaistd.jinli.service.BackOfficeSocketMessageService.broadCastLiveEndMessageToHttpSocket;

@Component
public class LiveRecordListener implements EventListener{
    final LiveUserDaoService liveUserDaoService;
    final LiveRecordDaoService liveRecordDaoService;
    @Autowired
    OfficialLiveRecordDaoService officialLiveRecordDaoService;
    @Autowired
    OfficialLiveService officialLiveService;
    @Autowired
    DataManager dataManager;
    @Autowired
    FollowListDaoService followListDaoService;
    @Autowired
    UserDataStatisticsDaoService userDataStatisticsDaoService;

    public LiveRecordListener(LiveUserDaoService liveUserDaoService, LiveRecordDaoService liveRecordDaoService) {
        this.liveUserDaoService = liveUserDaoService;
        this.liveRecordDaoService = liveRecordDaoService;
    }

    @Override
    public boolean handle(BaseEvent event) {
        LiveRecordEvent liveEvent = (LiveRecordEvent) event;
        Room room = liveEvent.getRoom();
        LiveUser liveUser = liveUserDaoService.findById(room.getLiveUserId());
        saveLiveRecord(liveUser, room,liveEvent.getBaseGame().getGameType());
        recordIfOfficialLiveRoom(room.getTotalGiftCoin(), liveUser);
        broadCastLiveEndMessageToHttpSocket(room);
        return true;
    }

    @Override
    public boolean isDisposable() {
        return false;
    }

    private void recordIfOfficialLiveRoom(long totalGiftCoin, LiveUser liveUser) {
        if (liveUser.isScriptLiveUser()){
            OfficialLiveRecord record = officialLiveRecordDaoService.findRecentOpenOfficialLiveByLiveUserId(liveUser.getId());
            if(record!=null)
                officialLiveService.totalCloseOfficialLiveRecord(record, System.currentTimeMillis(), totalGiftCoin,record.getBackOfficeName());
        }
    }

    private void saveLiveRecord(LiveUser liveUser, Room room, Constant.GameType gameType){
        List<LiveRecord> liveRecordList = new ArrayList<>();
        Map<Constant.PlatformType, List<FollowList>> platformFansMap = followListDaoService.groupFolloweeByPlatform(liveUser);
        User user;
        LiveRecord liveRecord;
        UserDataStatistics userRecentLoginInfo = userDataStatisticsDaoService.findUserRecentLoginInfo(liveUser.getUserId());
        LiveRecord totalLiveRecord = Optional.ofNullable(liveRecordDaoService.totalLiveRecordInfo(liveUser.getId())).orElse(new LiveRecord());
        for (RoomDataClassify platformRoomData : room.getAllPlatformData()) {
            Set<String> audienceList = platformRoomData.getAudienceHistory();
            int fansNum = platformFansMap.getOrDefault(platformRoomData.getPlatform(),new ArrayList<>()).size();
            int audienceSize = audienceList.size();
            long liveStartTime = room.getStartDate().getTime();
            long liveTime =  System.currentTimeMillis() - liveStartTime;
            long totalBetAmount = platformRoomData.getTotalBetAmount();
            long totalGiftCoin = platformRoomData.getTotalGiftCoin();
            user = dataManager.findUser(liveUser.getUserId());
            liveRecord = LiveRecord.newInstance(liveUser.getId(),room.getDisplayId(), liveUser.getUserId(), liveStartTime,
                    liveTime, totalGiftCoin, totalBetAmount, audienceSize,gameType,platformRoomData.getPlatform());
            liveRecord.setAudienceHistory(audienceList);
            liveRecord.setBulletMessageCount(platformRoomData.getBulletMessageCount());
            liveRecord.setLiveVisitorCount(platformRoomData.getLiveVisitorCount());
            liveRecord.setConnectedLiveCount(platformRoomData.getConnectLiveCount());
            liveRecord.setLiveIp(user.getLastIp());
            liveRecord.setNewFansNum(fansNum - platformRoomData.getStartOfFansNum());
            liveRecord.setFansNum(fansNum);
            liveRecord.setGiftCount(platformRoomData.getGiftCount());
            liveRecord.setTotalLiveTime(totalLiveRecord.getLiveTime() + liveTime);
            if(userRecentLoginInfo!=null && userRecentLoginInfo.getBrand()!=null) {
                liveRecord.setLoginDevice(String.valueOf(userRecentLoginInfo.getBrand().getNumber()));
            }
            liveRecordList.add(liveRecord);
        }
        liveRecordDaoService.saveAll(liveRecordList);
    }
}
