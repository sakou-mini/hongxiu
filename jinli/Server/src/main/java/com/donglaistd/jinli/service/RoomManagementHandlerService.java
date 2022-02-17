package com.donglaistd.jinli.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.BlacklistDaoService;
import com.donglaistd.jinli.database.dao.GlobalBlackListDaoService;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RoomManagementHandlerService {
    private final UserDaoService userDaoService;
    @Autowired
    GlobalBlackListDaoService globalBlackListDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    private BlacklistDaoService blacklistDaoService;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;

    public RoomManagementHandlerService(UserDaoService userDaoService) {
        this.userDaoService = userDaoService;
    }

    public boolean isMuteInRoom(Room room, Blacklist blacklist,User user){
        var muteChat = Objects.nonNull(blacklist) && blacklist.containsMuteChat(user.getId());
        var globalMuteChat = globalBlackListDaoService.isGlobalMute(user.getId());
        var platformMute = (!room.containsAdministrator(user.getId()) && userAttributeDaoService.findByUserIdOrSaveIfNotExit(user.getId()).isPlatformMute());
        return muteChat || globalMuteChat || platformMute;
    }

    public RoomManagement.Audience getAudience(Room room, Blacklist blacklist, String uid) {
        User onlineUser = userDaoService.findById(uid);
        var muteChat = Objects.nonNull(blacklist) && blacklist.containsMuteChat(uid);
        var platformMute = (!room.containsAdministrator(uid) && userAttributeDaoService.findByUserIdOrSaveIfNotExit(uid).isPlatformMute());
        var globalMuteChat = globalBlackListDaoService.isGlobalMute(uid);
        RoomManagement.Audience.Builder build = RoomManagement.Audience.newBuilder().setUserId(uid)
                .setDisplayName(onlineUser.getDisplayName())
                .setLevel(onlineUser.getLevel())
                .setAvatarUrl(onlineUser.getAvatarUrl())
                .setIsMute(Objects.nonNull(blacklist) && blacklist.containsMute(uid))
                .setIsMuteChat(isMuteInRoom(room,blacklist,onlineUser))
                .setIsManager(room.containsAdministrator(uid))
                .setVipId(onlineUser.getVipType()).setPlatformType(onlineUser.getPlatformType());
        if(globalMuteChat){
            GlobalBlackList globalBlackList = globalBlackListDaoService.finByUserId(uid);
            build.setMuteChatProperty(globalBlackList.getMuteProperty().toProto());
        }else if(muteChat){
            build.setMuteChatProperty(blacklist.getMuteChatProperty(uid).toProto());
        }else if(platformMute){
            RoomManagement.MuteProperty muteProperty = MuteProperty.newInstance(Constant.MuteTimeType.MUTE_TIME_DEFAULT, Constant.MuteReason.MUTE_OTHER,
                    "", Constant.MuteIdentity.IDENTITY_NULL, null, Constant.MuteArea.ALL_ROOM).toProto();
            build.setMuteChatProperty(muteProperty);
        }
        return build.build();
    }

    public List<RoomManagement.Audience> getMuteChatRecordIfAdministratorsOrLiveUser(Blacklist blacklist, User localUser, Room room) {
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        List<RoomManagement.Audience> muteChatRecords = new ArrayList<>();
        if (localUser == null) return muteChatRecords;
        String muteOptUserId;
        if (Objects.equals(room.getLiveUserId(), localUser.getLiveUserId()) || room.containsAdministrator(localUser.getId())) {
            List<String> audienceIds = room.getAllPlatformAudienceList().stream().filter(id -> !Objects.equals(id, liveUser.getUserId())).collect(Collectors.toList());
            RoomManagement.Audience.Builder builder;
            for (String userId : audienceIds) {
                if(isMuteInRoom(room,blacklist,localUser)) {
                    builder = getAudience(room, blacklist, userId).toBuilder();
                    muteOptUserId = builder.getMuteChatProperty().getMuteOptUserId();
                    if (!StringUtils.isNullOrBlank(muteOptUserId)) {
                        builder.setMuteOptUserDisplayName(Optional.ofNullable(userDaoService.findById(muteOptUserId)).orElse(new User()).getDisplayName());
                    }
                    muteChatRecords.add(builder.build());
                }
            }
            //add unMute history record
            if(Objects.nonNull(blacklist)) {
                blacklist.updateUnMuteHistory();
                if(Objects.equals(room.getLiveUserId(),localUser.getLiveUserId())){  //to save update after unMuteChatHistory
                    blacklistDaoService.save(blacklist);
                }
                blacklist.getUnMuteHistories().forEach(v -> muteChatRecords.add(buildUnMuteRecordAudience(v)));
            }
        }
        return muteChatRecords;
    }

    private RoomManagement.Audience buildUnMuteRecordAudience(UnMuteHistory unMuteHistory){
        var userId = unMuteHistory.getUserId();
        var muteProperty = unMuteHistory.getMuteProperty();
        User muteUser = userDaoService.findById(userId);
        RoomManagement.Audience.Builder build = RoomManagement.Audience.newBuilder()
                .setUserId(userId)
                .setDisplayName(muteUser.getDisplayName())
                .setLevel(muteUser.getLevel())
                .setAvatarUrl(muteUser.getAvatarUrl())
                .setIsMuteChat(false)
                .setVipId(muteUser.getVipType())
                .setMuteChatProperty(muteProperty.toProto()).setPlatformType(muteUser.getPlatformType());
        if (!StringUtils.isNullOrBlank(muteProperty.getMuteOptUserId())) {
            build.setMuteOptUserDisplayName(Optional.ofNullable(userDaoService.findById(muteProperty.getMuteOptUserId())).orElse(new User()).getDisplayName());
        }
        return build.build();
    }
}
