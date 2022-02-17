package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.dao.GlobalBlackListDaoService;
import com.donglaistd.jinli.database.dao.OfficialLiveRecordDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.backoffice.OfficialLiveRecord;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.game.EmptyGame;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.live.ChatMessage;
import com.donglaistd.jinli.http.entity.live.RoomAudience;
import com.donglaistd.jinli.processors.handler.EndLiveRequestHandler;
import com.donglaistd.jinli.processors.handler.connectLive.UnMuteChatRequestHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.donglaistd.jinli.service.RoomProcess.buildMuteChatBroadcastMessage;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class LiveMonitorProcess {
    @Autowired
    DataManager dataManager;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    BlacklistDaoService blacklistDaoService;
    @Autowired
    GlobalBlackListDaoService globalBlackListDaoService;
    @Autowired
    EndLiveRequestHandler endLiveRequestHandler;
    @Autowired
    UnMuteChatRequestHandler unMuteChatRequestHandler;
    @Autowired
    OfficialLiveRecordDaoService officialLiveRecordDaoService;

    public List<ChatMessage> queryChatHistory(String roomId) {
        List<ChatMessage> chatMessageHistory = new ArrayList<>(0);
        Room room = DataManager.findOnlineRoom(roomId);
        if (room == null) return chatMessageHistory;
        chatMessageHistory = room.getRoomAllChatHistory().stream().map(messageRecord -> new ChatMessage(messageRecord, userDaoService.findById(messageRecord.getFromUid())))
                .collect(Collectors.toCollection(() -> new ArrayList<>(0)));
        return chatMessageHistory;
    }

    public PageInfo<RoomAudience> queryRoomAudienceList(String roomId,String userId,String ip , int page,int size){
        Room room = DataManager.findOnlineRoom(roomId);
        if(room == null) return new PageInfo<>(new ArrayList<>(0),0);
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        List<Predicate<String>> predicates = new ArrayList<>();
        Blacklist blacklist = blacklistDaoService.findByRoomId(room.getId());
        if (!StringUtils.isNullOrBlank(userId)) {
            User user = Optional.ofNullable(userDaoService.findUserByPlatformUserIdOrUserId(userId)).orElse(new User());
            predicates.add(id -> Objects.equals(id, user.getId()));
        }
        if(!StringUtils.isNullOrBlank(ip)){
            predicates.add(id -> Objects.equals(ip, userDaoService.findById(id).getLastIp()));
        }
        Stream<String> audienceIdsStream =  room.getAllPlatformAudienceList().stream();
        for (Predicate<String> predicate : predicates) {
            audienceIdsStream = room.getAllPlatformAudienceList().stream().filter(predicate);
        }
        List<String> audienceIds = audienceIdsStream.collect(Collectors.toList());
        //top liveUser
        if(audienceIds.contains(liveUser.getUserId())){
            int index = audienceIds.indexOf(liveUser.getUserId());
            Collections.swap(audienceIds, index ,0);
        }
        long totalNum = audienceIds.size();
        int startIndex = Math.min(((page-1) * size), (int) totalNum);
        int endIndex = Math.min(size * page,(int) totalNum);
        List<String> ids = audienceIds.subList(Math.max(startIndex, 0), endIndex);
        List<RoomAudience> content = ids.stream().map(id -> getAudience(room,liveUser,blacklist, id)).collect(Collectors.toList());
        return new PageInfo<>(content, totalNum);
    }

    private RoomAudience getAudience(Room room,LiveUser liveUser,Blacklist blacklist,  String uid) {
        User user = userDaoService.findById(uid);
        var isMuteChat = Objects.nonNull(blacklist) && blacklist.containsMuteChat(uid);
        if(!isMuteChat) {
            isMuteChat = globalBlackListDaoService.isGlobalMute(uid);
        }
        RoomAudience.AudienceIdentity identity = RoomAudience.AudienceIdentity.AUDIENCE;
        if (room.getAdministrators().contains(uid)) {
            identity = RoomAudience.AudienceIdentity.ADMINISTRATOR;
        }
        if (Objects.equals(liveUser.getUserId(), uid)) {
            identity = RoomAudience.AudienceIdentity.LIVEUSER;
        }
        return  new RoomAudience(user,isMuteChat,identity);
    }

    public Constant.ResultCode muteRoomChat(String roomId, List<String> ids, MuteProperty muteProperty){
        muteProperty.setMuteOptIdentity(Constant.MuteIdentity.IDENTITY_BACKOFFICE);
        muteProperty.setMuteStartTime(System.currentTimeMillis());
        Room room = DataManager.findOnlineRoom(roomId);
        if(Objects.isNull(room)){
            return Constant.ResultCode.ROOM_DOES_NOT_EXIST;
        }
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        if(ids.contains(liveUser.getUserId())){
            return Constant.ResultCode.CAN_NOT_MUTE_LIVEUSER;
        }
        if(userDaoService.findByUserIds(ids).size() != ids.size())
           return Constant.ResultCode.USER_NOT_FOUND;
        switch (muteProperty.getMuteArea()){
            case LOCAL_ROOM:
                Blacklist blacklist = Optional.ofNullable(blacklistDaoService.findByRoomId(roomId)).orElse(Blacklist.getInstance(room.getId(), null));
                ids.forEach(userId -> blacklist.addMuteChat(userId, muteProperty));
                blacklistDaoService.save(blacklist);
                break;
            case ALL_ROOM:
                List<GlobalBlackList> globalBlackLists = new ArrayList<>(ids.size());
                ids.forEach(id->globalBlackLists.add(GlobalBlackList.newInstance(id, muteProperty)));
                globalBlackListDaoService.saveAll(globalBlackLists);
                break;
        }
        ids.forEach(id -> room.broadCastToAllPlatform(buildReply(buildMuteChatBroadcastMessage(id, muteProperty))));
        return Constant.ResultCode.SUCCESS;
    }

    public void unMuteAllChat(String roomId, String userId) {
        globalBlackListDaoService.deleteByUserId(userId);
        unMuteChatRequestHandler.removeUserToBlackList(roomId, userId, Constant.MuteIdentity.IDENTITY_BACKOFFICE);
    }

    public Constant.ResultCode closeLiveRoom(String liveUserId, String backOfficeName, long delayTime , Constant.EndType endType) {
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(liveUser.isScriptLiveUser() && !StringUtils.isNullOrBlank(backOfficeName)){
            OfficialLiveRecord officialLiveRecord = officialLiveRecordDaoService.findRecentOpenOfficialLiveByLiveUserId(liveUserId);
            if(officialLiveRecord!=null) {
                officialLiveRecord.setBackOfficeName(backOfficeName);
                officialLiveRecordDaoService.save(officialLiveRecord);
            }
        }
        CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
        if(Objects.nonNull(game)  && !(game instanceof EmptyGame)){
            game.endGame();
        }
        return endLiveRequestHandler.process(liveUser, delayTime,endType).getResultCode();
    }
}
