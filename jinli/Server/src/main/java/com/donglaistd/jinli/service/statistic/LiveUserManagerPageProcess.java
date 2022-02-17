package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.constant.LiveUserApproveStatue;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.backoffice.UserDataStatisticsDaoService;
import com.donglaistd.jinli.database.dao.invite.UserInviteRecordDaoService;
import com.donglaistd.jinli.database.dao.platform.PlatformRechargeRecordDaoService;
import com.donglaistd.jinli.database.dao.statistic.DailyLiveRecordInfoDaoService;
import com.donglaistd.jinli.database.dao.statistic.LiveSourceLineConfigDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.backoffice.LiveUserApproveRecord;
import com.donglaistd.jinli.database.entity.backoffice.UserDataStatistics;
import com.donglaistd.jinli.database.entity.game.BaseGame;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import com.donglaistd.jinli.database.entity.statistic.DailyLiveRecordInfo;
import com.donglaistd.jinli.database.entity.system.LiveSourceLineConfig;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.http.dto.request.LiveRecordRequest;
import com.donglaistd.jinli.http.dto.request.LiveUserApproveRecordRequest;
import com.donglaistd.jinli.http.dto.request.LiveUserPageListRequest;
import com.donglaistd.jinli.http.dto.request.LiveUserPlatformRequest;
import com.donglaistd.jinli.http.entity.*;
import com.donglaistd.jinli.http.entity.live.LiveAttendance;
import com.donglaistd.jinli.http.entity.live.LiveRecordInfo;
import com.donglaistd.jinli.http.response.ErrorResponse;
import com.donglaistd.jinli.http.response.GenericResponse;
import com.donglaistd.jinli.http.response.GlobalResponseCode;
import com.donglaistd.jinli.http.response.SuccessResponse;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.LiveProcess;
import com.donglaistd.jinli.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.donglaistd.jinli.constant.GameConstant.EMPTY_GAME_CLOSE_DELAY_TIME;
import static com.donglaistd.jinli.constant.WebKeyConstant.*;

@Component
public class LiveUserManagerPageProcess {
    private static final Logger logger = Logger.getLogger(LiveUserManagerPageProcess.class.getName());
    @Autowired
    LiveProcess liveProcess;

    final LiveUserDaoService liveUserDaoService;
    final UserDaoService userDaoService;
    final UserDataStatisticsDaoService userDataStatisticsDaoService;
    final CoinFlowDaoService coinFlowDaoService;
    final LiveRecordDaoService liveRecordDaoService;
    final DataManager dataManager;
    final FollowListDaoService followListDaoService;
    final DailyLiveRecordInfoDaoService dailyLiveRecordInfoDaoService;
    final PlatformRechargeRecordDaoService platformRechargeRecordDaoService;
    final VerifyUtil verifyUtil;
    final LiveMonitorProcess liveMonitorProcess;
    final LiveUserApproveRecordDaoService liveUserApproveRecordDaoService;
    final UserInviteRecordDaoService userInviteRecordDaoService;
    final LiveSourceLineConfigDaoService liveSourceLineConfigDaoService;
    final RoomDaoService roomDaoService;

    public LiveUserManagerPageProcess(DailyLiveRecordInfoDaoService dailyLiveRecordInfoDaoService, LiveUserDaoService liveUserDaoService,
                                      UserDaoService userDaoService, UserDataStatisticsDaoService userDataStatisticsDaoService,
                                      CoinFlowDaoService coinFlowDaoService, LiveRecordDaoService liveRecordDaoService, DataManager dataManager,
                                      FollowListDaoService followListDaoService, PlatformRechargeRecordDaoService platformRechargeRecordDaoService,
                                      VerifyUtil verifyUtil, LiveMonitorProcess liveMonitorProcess, LiveUserApproveRecordDaoService liveUserApproveRecordDaoService,
                                      UserInviteRecordDaoService userInviteRecordDaoService, LiveSourceLineConfigDaoService liveSourceLineConfigDaoService, RoomDaoService roomDaoService) {
        this.dailyLiveRecordInfoDaoService = dailyLiveRecordInfoDaoService;
        this.liveUserDaoService = liveUserDaoService;
        this.userDaoService = userDaoService;
        this.userDataStatisticsDaoService = userDataStatisticsDaoService;
        this.coinFlowDaoService = coinFlowDaoService;
        this.liveRecordDaoService = liveRecordDaoService;
        this.dataManager = dataManager;
        this.followListDaoService = followListDaoService;
        this.platformRechargeRecordDaoService = platformRechargeRecordDaoService;
        this.verifyUtil = verifyUtil;
        this.liveMonitorProcess = liveMonitorProcess;
        this.liveUserApproveRecordDaoService = liveUserApproveRecordDaoService;
        this.userInviteRecordDaoService = userInviteRecordDaoService;
        this.liveSourceLineConfigDaoService = liveSourceLineConfigDaoService;
        this.roomDaoService = roomDaoService;
    }

    public Map<String, Object> getLiveUserSummary(Constant.PlatformType platformType) {
        Map<String, Object> result = new HashMap<>(4);
        Map<Constant.GenderType, Long> genderGroup = liveUserDaoService.groupLiveUserByGender(platformType);
        long liveUserNum = genderGroup.values().stream().mapToLong(Long::longValue).sum();
        List<String> userIds = liveUserDaoService.findAllPassLiveUserByPlatform(platformType).stream().map(LiveUser::getUserId).collect(Collectors.toList());
        Map<Long, Integer> dayLoginMap = userDataStatisticsDaoService.groupDayLoginNumByUserIdsAndTimeBetween(TimeUtil.getFirstDayOfCurrentMonth(), TimeUtil.getCurrentDayStartTime(), userIds);
        int loginNum = dayLoginMap.values().stream().mapToInt(Integer::intValue).sum();
        result.put(LIVE_USER_NUM, liveUserNum);
        result.put(MONTH_ONLINE_AVG_NUM, loginNum / TimeUtil.getDayOfMonth());
        result.put(GENDER, genderGroup);
        return result;
    }

    public PageInfo<LiveUserInfo> queryLiveUserInfoList(LiveUserPageListRequest request) {
        String condition = request.getCondition();
        PageInfo<LiveUser> liveUserPageInfo = liveUserDaoService.queryLiveUserByPageInfoAndCondition(request.getPlatformType(), condition,condition,condition, request.getQueryStatue(),request.getPage(), request.getSize());
        List<LiveUserInfo> content = new ArrayList<>(liveUserPageInfo.getContent().size());
        for (LiveUser liveUser : liveUserPageInfo.getContent()) {
            content.add(liveUserToLiveUserInfo(liveUser));
        }
        return new PageInfo<>(content, liveUserPageInfo.pageable, liveUserPageInfo.total);
    }

    public LiveUserInfo liveUserToLiveUserInfo(LiveUser liveUser) {
        long monthStartTime = TimeUtil.getFirstDayOfCurrentMonth();
        User user = userDaoService.findById(liveUser.getUserId());
        CoinFlow coinFlow = Optional.ofNullable(coinFlowDaoService.findByUserId(liveUser.getUserId())).orElse(new CoinFlow());
        long monthOnlineTime = userDataStatisticsDaoService.findByUserIdAndTimeBetween(liveUser.getUserId(), monthStartTime, System.currentTimeMillis())
                .stream().mapToLong(UserDataStatistics::getOnlineTime).sum();
        return new LiveUserInfo(user, liveUser, coinFlow, (int) TimeUnit.MILLISECONDS.toHours(monthOnlineTime));
    }

    //-----------------liveUserDetail build----------------------
    private LiveUserDetail.UserSummary buildUserSummary(User user,CoinFlow coinFlow){
        UserDataStatistics userRecentLoginInfo = userDataStatisticsDaoService.findUserRecentLoginInfo(user.getId());
        UserInviteRecord inviteRecord = userInviteRecordDaoService.findByBeInviteUserId(user.getId());
        String inviteCode = inviteRecord == null ? "" : inviteRecord.getInviteUserId();
        return new LiveUserDetail.UserSummary(user,coinFlow,userRecentLoginInfo.getBrand(),inviteCode);
    }

    private LiveUserDetail buildLiveUserDetailInfo(LiveUser liveUser,CoinFlow coinFlow){
        int fansNum = followListDaoService.findFolloweeIdsByFollowerId(liveUser.getId()).size();
        long totalGiftIncome = coinFlow.getGiftIncome();
        long monthLiveTime = liveRecordDaoService.totalLiveUserLiveTimeByTimeBetween(liveUser.getId(),
                TimeUtil.getMonthOfStartTime(System.currentTimeMillis()), System.currentTimeMillis());
        LiveRecord totalLiveRecord = Optional.ofNullable(liveRecordDaoService.totalLiveRecordInfo(liveUser.getId())).orElse(new LiveRecord());
        long totalLiveTime = totalLiveRecord.getLiveTime();
        long totalLiveGameFlow = totalLiveRecord.getGameFlow();
        long liveCount = totalLiveRecord.getRecordTime();
        long totalAudienceNum = totalLiveRecord.getAudienceNum();
        Constant.LiveStatus liveStatus = liveUser.getLiveStatus();
        LiveUserDetail liveUserDetail = new LiveUserDetail(liveUser.getId(), fansNum, totalGiftIncome, liveUser.getPhoneNumber(), liveUser.getPlatformType(),
                liveStatus, monthLiveTime, totalLiveTime, liveCount, totalLiveGameFlow, roomDaoService.findByLiveUser(liveUser).getDisplayId());
        liveUserDetail.totalAudienceNum = totalAudienceNum;
        liveUserDetail.setDisablePermissions(liveUser.getDisablePermissions());
        liveUserDetail.auditTime = liveUser.getAuditTime();
        liveUserDetail.sharedPlatform = liveUser.getSharedPlatform();
        return liveUserDetail;
    }

    private LiveUserDetail.LiveSummary buildLatestLiveSummary(LiveUser liveUser) {
        Room liveRoom = DataManager.findOnlineRoom(liveUser.getRoomId());
        LiveRecord lastLiveRecord = Optional.ofNullable(liveRecordDaoService.findRecentLiveRecordByLiveUser(liveUser.getId())).orElse(new LiveRecord());
        LiveUserDetail.LiveSummary liveSummary = new LiveUserDetail.LiveSummary(lastLiveRecord.getLiveStartTime(),lastLiveRecord.getLiveTime(),lastLiveRecord.getGiftFlow());
        if(liveRoom != null && liveRoom.isLive()){
            liveSummary.roomTitle = liveRoom.getRoomTitle();
            liveSummary.audienceNum = liveRoom.getAllPlatformAudienceList().size();
            liveSummary.liveStartTime = liveRoom.getStartDate().getTime();
            liveSummary.roomImage = liveRoom.getRoomImage();
            liveSummary.roomId = liveRoom.getId();
            CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
            if(game != null) liveSummary.gameType = game.getGameType();
        }
        return liveSummary;
    }

    public LiveUserDetail getLiveUserDetail(String liveUserId) {
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(liveUser == null) return null;
        User user = userDaoService.findById(liveUser.getUserId());
        CoinFlow coinFlow = Optional.ofNullable(coinFlowDaoService.findByUserId(user.getId())).orElse(new CoinFlow());
        LiveUserDetail.UserSummary userSummary = buildUserSummary(user, coinFlow);
        LiveUserDetail.LiveSummary liveSummary = buildLatestLiveSummary(liveUser);
        LiveUserDetail liveUserDetail = buildLiveUserDetailInfo(liveUser, coinFlow);
        liveUserDetail.userSummary = userSummary;
        liveUserDetail.latestLiveSummary = liveSummary;
        return liveUserDetail;
    }

    public List<DailyLiveRecordInfo> findDailyLiveRecordInfo(String liveUserId, long startTime, long endTime){
        Map<Long, DailyLiveRecordInfo> dailyLiveRecordInfoMap = dailyLiveRecordInfoDaoService
                .findByLiveUserIdAndTimes(liveUserId, startTime, endTime)
                .stream().collect(Collectors.toMap(DailyLiveRecordInfo::getTime, liveRecord -> liveRecord));
        for (Long day : TimeUtil.getDayTimeBetweenTimes(startTime, endTime)) {
            dailyLiveRecordInfoMap.putIfAbsent(day, DailyLiveRecordInfo.newInstance(liveUserId,0,0,0,0,day,0, Constant.PlatformType.PLATFORM_JINLI));
        }
        return dailyLiveRecordInfoMap.values().stream().sorted(Comparator.comparingLong(DailyLiveRecordInfo::getTime)).collect(Collectors.toList());
    }

    public List<RoomInfo> getSortedOnlineRoomInfo(String liveUserId, String displayName, String roomId, Constant.PlatformType platform){
        List<Predicate<Room>> predicates = new ArrayList<>(3);
        predicates.add(room -> room.isLive() && room.isSharedToPlatform(platform));
        if (!StringUtils.isNullOrBlank(liveUserId)) {
            predicates.add(room -> room.getLiveUserId().equals(liveUserId));
        }
        if (!StringUtils.isNullOrBlank(displayName)){
            User user = userDaoService.findByDisplayName(displayName);
            if(Objects.isNull(user)) return null;
            predicates.add(room -> room.getLiveUserId().equals(user.getLiveUserId()));
        }
        if (!StringUtils.isNullOrBlank(roomId)) {
            predicates.add(room -> room.getDisplayId().equals(roomId));
        }
        Stream<Room> roomStream = DataManager.getOnlineRoomList().stream();
        for (Predicate<Room> predicate : predicates) {
            roomStream = roomStream.filter(predicate);
        }
        List<Room> rooms = roomStream.sorted(ComparatorUtil.getRoomRecommendComparator(platform)).collect(Collectors.toList());
        List<RoomInfo> roomInfos = new ArrayList<>(rooms.size());
        LiveUser liveUser;
        User user;
        BaseGame game;
        RoomInfo roomInfo;
        for (Room room : rooms) {
            liveUser = dataManager.findLiveUser(room.getLiveUserId());
            user = userDaoService.findByLiveUserId(room.getLiveUserId());
            game = (BaseGame) DataManager.findGame(liveUser.getPlayingGameId());
            roomInfo = new RoomInfo(room, user);
            if(game != null) {
                roomInfo.gameType = game.getGameType();
                roomInfos.add(roomInfo);
            }
        }
        return roomInfos;
    }


    public PageInfo<LiveRecordInfo> queryLiveRecord(int page,int size,String liveUserId,String roomId,Constant.PlatformType platformType ,Long startTime,Long endTime){
        List<LiveRecordInfo> liveRecordInfos = new ArrayList<>();
        PageInfo<LiveRecord> liveRecordPageInfo = liveRecordDaoService.pageQueryLiveRecord(size, page, liveUserId, roomId,platformType,startTime,endTime);
        Map<String, User> userMap = new HashMap<>();
        Map<String, LiveRecord> totalLiveRecordMap = new HashMap<>();
        String userId;
        LiveRecord totalLiveRecord;
        long monthStartTime = TimeUtil.getMonthOfStartTime(System.currentTimeMillis());
        for (LiveRecord liveRecord : liveRecordPageInfo.getContent()) {
            userId = liveRecord.getUserId();
            if(!userMap.containsKey(userId)) userMap.put(userId, userDaoService.findById(userId));
            User user = userMap.get(userId);
            if(!totalLiveRecordMap.containsKey(userId)) {
                totalLiveRecordMap.put(userId, Optional.ofNullable(liveRecordDaoService.
                        totalLiveRecordInfoByRecordTimes(user.getLiveUserId(),monthStartTime,System.currentTimeMillis())).orElse(new LiveRecord()));
            }
            totalLiveRecord = totalLiveRecordMap.get(userId);
            liveRecordInfos.add(LiveRecordInfo.newInstance(userMap.get(userId), liveRecord, totalLiveRecord.getLiveTime(), totalLiveRecord.getGiftFlow(),totalLiveRecord.getGameFlow()));
        }
        return new PageInfo<>(liveRecordInfos, liveRecordPageInfo.getPageable(), liveRecordPageInfo.getTotal());
    }

    public boolean swapRoomRecommendSort(String roomId, boolean raiseRecommend , Constant.PlatformType platform) {
        Room swapRoom = DataManager.findOnlineRoom(roomId);
        if(Objects.isNull(swapRoom)) return false;
        List<Room> recommendRoom = DataManager.getOnlineRoomList().stream().filter(room -> room.isSharedToPlatform(platform))
                .sorted(ComparatorUtil.getRoomRecommendComparator(platform)).collect(Collectors.toList());
        int index = recommendRoom.indexOf(swapRoom);
        if(recommendRoom.size() <= 1) return true;
        Room replacedRoom;
        if(raiseRecommend){
            if(index <= 0) {
                swapRoom.setPlatformRecommendWeight(platform,recommendRoom.size());
                return true;
            }
            replacedRoom = recommendRoom.get(index - 1);
        }else {
            if (index == (recommendRoom.size()-1)) {
                swapRoom.setPlatformRecommendWeight(platform,1);
                return true;
            }
            replacedRoom = recommendRoom.get(index + 1);
        }
        //重新编排房间顺序
        for (int i = 0; i < recommendRoom.size(); i++) {
            recommendRoom.get(i).setPlatformRecommendWeight(platform,recommendRoom.size()-i);
        }
        int replaceRoomWeight = replacedRoom.getPlatformRecommendWeight(platform);
        replacedRoom.setPlatformRecommendWeight(platform, swapRoom.getPlatformRecommendWeight(platform));
        swapRoom.setPlatformRecommendWeight(platform, replaceRoomWeight);
        return true;
    }

    public PageInfo<LiveAttendance> queryLiveAttendance(boolean isStandards, long time, int page, int size) {
        long startTime = TimeUtil.getTimeDayStartTime(time);
        long endTime = startTime + TimeUnit.DAYS.toMillis(1) - 1;
        long liveNum = dailyLiveRecordInfoDaoService.countLiveNumByTimeBetween(startTime, endTime);
        PageInfo<DailyLiveRecordInfo> dayLiveRecord = dailyLiveRecordInfoDaoService.findPageDailyLiveRecordByTimeBetweenAndLiveTime(startTime, endTime, isStandards, page, size);
        List<LiveAttendance> liveAttendances = new ArrayList<>(dayLiveRecord.getContent().size());
        for (DailyLiveRecordInfo recordInfo : dayLiveRecord.getContent()) {
            liveAttendances.add(LiveAttendance.newInstance(userDaoService.findByLiveUserId(recordInfo.getLiveUserId()), recordInfo));
        }
        PageInfo<LiveAttendance> liveAttendancePageInfo = new PageInfo<>(liveAttendances, dayLiveRecord.getPageable(), dayLiveRecord.getTotal());
        long standardsNum = isStandards ? dayLiveRecord.getTotal() : liveNum - dayLiveRecord.getTotal();
        long unStandardsNum = liveNum - standardsNum;
        liveAttendancePageInfo.addOtherParam("liveNum",liveNum);
        liveAttendancePageInfo.addOtherParam("standardsNum",standardsNum);
        liveAttendancePageInfo.addOtherParam("unStandardsNum",unStandardsNum);
        return liveAttendancePageInfo;
    }

    public PageInfo<UserListData> findPlatformLiveUser(Constant.PlatformType platform, String liveUserId, String roomId, String userId, int page, int size) {
        PageInfo<LiveUser> liveUserPageInfo = liveUserDaoService.queryLiveUserByPageInfoAndCondition(platform, liveUserId, userId, roomId, LiveUserPageListRequest.QueryLiveUserStatue.ALL, page, size);
        List<UserListData> userListData = new ArrayList<>(liveUserPageInfo.getContent().size());
        for (LiveUser liveUser : liveUserPageInfo.getContent()) {
            userListData.add(buildSimpleUserData(userDaoService.findById(liveUser.getUserId())));
        }
        return new PageInfo<>(userListData, liveUserPageInfo.getTotal());
    }

    private UserListData buildSimpleUserData(User user){
        CoinFlow coinFlow = coinFlowDaoService.findByUserId(user.getId());
        long recharge = coinFlow != null ? coinFlow.getRecharge() : 0;
        var userData = new UserListData();
        userData.setGameCoin(user.getGameCoin());
        userData.setId(user.getId());
        userData.setDisplayName(user.getDisplayName());
        userData.setRecharge(recharge);
        userData.setVipType(user.getVipType());
        userData.setLiveUserId(user.getLiveUserId());
        userData.setAvatarUrl(user.getAvatarUrl());
        return userData;
    }

    public Constant.ResultCode changeLiveUserPlantForm(String liveUserId, Constant.PlatformType plantForm) {
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(Objects.isNull(liveUser))
            return Constant.ResultCode.NOT_LIVE_USER;
        if(!StringUtils.isNullOrBlank(liveUser.getPlayingGameId()) && DataManager.findGame(liveUser.getPlayingGameId())!=null)
            return Constant.ResultCode.LIVE_USER_IS_LIVE;
        liveUser.setPlatformType(plantForm);
        User user = dataManager.findUser(liveUser.getUserId());
        logger.info("修改 主播平台 用户-》" + user   +"liveUser:" + liveUser);
        user.setPlatformType(plantForm);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        return  Constant.ResultCode.SUCCESS;
    }

    public Constant.ResultCode banLiveUser(String liveUserId) {
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if (!verifyUtil.verifyIsLiveUser(liveUser)) return Constant.ResultCode.NOT_LIVE_USER;
        liveUser.setLiveStatus(Constant.LiveStatus.LIVE_BAN);
        liveUser.setBanTime(System.currentTimeMillis());
        dataManager.saveLiveUser(liveUser);
        if(verifyUtil.checkLiveIsExist(liveUserId)){
            liveMonitorProcess.closeLiveRoom(liveUser.getId(),"",EMPTY_GAME_CLOSE_DELAY_TIME, Constant.EndType.NORMAL_END);
        }
        broadCastLiveUserIsBanMessage(liveUser, Constant.LiveStatus.LIVE_BAN);
        return Constant.ResultCode.SUCCESS;
    }

    public Constant.ResultCode unLockLiveUser(String liveUserId){
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(Objects.isNull(liveUser)) return Constant.ResultCode.NOT_LIVE_USER;
        if(!Objects.equals(Constant.LiveStatus.LIVE_BAN,liveUser.getLiveStatus())) return Constant.ResultCode.LIVE_USER_NOT_BAN;
        liveUser.setLiveStatus(Constant.LiveStatus.OFFLINE);
        dataManager.saveLiveUser(liveUser);
        broadCastLiveUserIsBanMessage(liveUser, Constant.LiveStatus.OFFLINE);
        return Constant.ResultCode.SUCCESS;
    }

    private void broadCastLiveUserIsBanMessage(LiveUser liveUser, Constant.LiveStatus liveStatus){
        Jinli.LiveUserBanBroadcastMessage.Builder builder = Jinli.LiveUserBanBroadcastMessage.newBuilder().setUserId(liveUser.getUserId()).setLiveStatus(liveStatus);
        MessageUtil.sendMessage(liveUser.getUserId(),MessageUtil.buildReply(builder));
    }

    public Constant.ResultCode updateLiveUserBanPermission(String liveUserId, List<String> permissions){
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        Constant.ResultCode resultCode = verifyUtil.checkLiveUserAndPermissionParam(liveUser, permissions);
        if(!Constant.ResultCode.SUCCESS.equals(resultCode)) return resultCode;
        Set<Constant.LiveUserPermission> permission= permissions.stream().map(Constant.LiveUserPermission::valueOf).collect(Collectors.toSet());
        liveUser.setDisablePermissions(permission);
        dataManager.saveLiveUser(liveUser);
        broadCastLiveUserPermissionMessage(liveUser);
        return Constant.ResultCode.SUCCESS;
    }

    private void broadCastLiveUserPermissionMessage(LiveUser liveUser){
        var liveUserInfo = liveProcess.buildLiveUserInfo(liveUser, roomDaoService.findByLiveUserId(liveUser.getId()));
        var builder = Jinli.LiveUserPermissionBroadcastMessage.newBuilder().setLiveUserInfo(liveUserInfo);
        MessageUtil.sendMessage(liveUser.getUserId(),MessageUtil.buildReply(builder));
    }

   /* public PageInfo<LiveUser> getApplyLiveUserPageInfo(int page, int size, int queryType, String msg, int platform) {
        PageRequest pageInfo = PageRequest.of(page, size);
        return liveUserDaoService.findLiveUserByStatueAndPageInfoAndQueryCondition(pageInfo,Constant.LiveStatus.UNAPPROVED,queryType,msg, Constant.PlatformType.forNumber(platform));
    }*/

    public PageInfo<ApplicationLiveUserData> getLiveUserApproveRecordPageInfo(LiveUserApproveRecordRequest request) {
        PageInfo<LiveUserApproveRecord> result = liveUserApproveRecordDaoService.pageQueryApproveRecord(request);
        List<ApplicationLiveUserData> contents = new ArrayList<>();
        result.getContent().forEach(record -> contents.add(buildLiveUserApproveRecordToApplicationLiveUserData(record)));
        return new PageInfo<>(contents, result.getTotal());
    }

    public ApplicationLiveUserData buildLiveUserApproveRecordToApplicationLiveUserData(LiveUserApproveRecord record){
        var liveUserData = new ApplicationLiveUserData();
        var user = userDaoService.findById(record.getUserId());
        var liveUser = liveUserDaoService.findById(record.getApproveLiveUserId());
        liveUserData.getGameCoin = user.getGameCoin();
        liveUserData.displayName = user.getDisplayName();
        liveUserData.avatarUrl = user.getAvatarUrl();
        liveUserData.phoneNumber = StringUtils.isNullOrBlank(user.getPhoneNumber()) ? "unbound" : user.getPhoneNumber();
        liveUserData.realName = liveUser.getRealName();
        liveUserData.idImageURL = liveUser.getIdImageURL();
        liveUserData.applyLiveUserTime = record.getApplyDate();
        liveUserData.id = record.getUserId();
        liveUserData.statue = record.getStatue().getCode();
        liveUserData.backOfficeName = record.getBackOfficeName();
        return liveUserData;
    }

    public PageInfo<AuthenticationLiveUser> getLiveUserApproveRecordByCondition(PageRequest pageInfo, String userId, boolean isPass, Long start, Long end, int platform) {
        if(Constant.PlatformType.forNumber(platform) == null)
            return new PageInfo<>(new ArrayList<>(0), 0);
        PageInfo<LiveUserApproveRecord> pageResult = liveUserApproveRecordDaoService.findPageInfoApproveRecordByConditionAndTime(pageInfo, userId, isPass, start, end, Constant.PlatformType.forNumber(platform));
        Collection<LiveUserApproveRecord> contents = pageResult.getContent();
        List<AuthenticationLiveUser> authenticationContent = new ArrayList<>();
        for (LiveUserApproveRecord record : contents) {
            authenticationContent.add(buildAuthenticationLiveUser(record));
        }
        return new PageInfo<>(authenticationContent, pageResult.getTotal());
    }

    private AuthenticationLiveUser buildAuthenticationLiveUser(LiveUserApproveRecord record){
        AuthenticationLiveUser passLiveUserMsg;
        var liveUser = liveUserDaoService.findById(record.getApproveLiveUserId());
        passLiveUserMsg = new AuthenticationLiveUser();
        var user = userDaoService.findById(liveUser.getUserId());
        passLiveUserMsg.id = user.getId();
        passLiveUserMsg.liveUserId = liveUser.getId();
        passLiveUserMsg.avatarUrl=user.getAvatarUrl();
        passLiveUserMsg.realName = liveUser.getRealName();
        passLiveUserMsg.avatarUrl=user.getAvatarUrl();
        passLiveUserMsg.displayName = user.getDisplayName();
        passLiveUserMsg.phoneNumber = user.getPhoneNumber() == null ? "unbound" : user.getPhoneNumber();
        passLiveUserMsg.idImageURL = liveUser.getIdImageURL();
        passLiveUserMsg.backOfficeName = record.getBackOfficeName();
        passLiveUserMsg.applicationsNumber = liveUserApproveRecordDaoService.findByApprovedUserId(record.getApproveLiveUserId(),null,null).size();
        passLiveUserMsg.getGameCoin = user.getGameCoin();
        passLiveUserMsg.userId = liveUser.getUserId();
        passLiveUserMsg.applyLiveUserTime = liveUser.getApplyTime();
        passLiveUserMsg.approveState = record.getApproveState();
        return passLiveUserMsg;
    }

    public LiveUserInfo getLiveUserApplyDetailInfo(String userId){
        User user = userDaoService.findById(userId);
        LiveUser liveUser = liveUserDaoService.findByUserId(user.getId());
        LiveUserInfo liveUserInfo = new LiveUserInfo();
        liveUserInfo.setAddress(liveUser.getAddress());
        liveUserInfo.setBirthDay(liveUser.getBirthDay());
        liveUserInfo.setContactWay(liveUser.getContactWay());
        liveUserInfo.setDisplayName(user.getDisplayName());
        liveUserInfo.setCountry(liveUser.getCountry());
        liveUserInfo.setEmail(liveUser.getEmail());
        liveUserInfo.setGender(liveUser.getGender());
        liveUserInfo.setId(liveUser.getUserId());
        liveUserInfo.setLiveUserId(liveUser.getId());
        liveUserInfo.setImages(liveUser.getImages());
        liveUserInfo.setLiveStatus(liveUser.getLiveStatus());
        liveUserInfo.setPhoneNumber(liveUser.getPhoneNumber());
        liveUserInfo.setRealName(liveUser.getRealName());
        if(liveUser.getLiveStatus() == Constant.LiveStatus.APPROVED_FAIL){
            var record = liveUserApproveRecordDaoService.findByApprovedUserId(liveUser.getId(),null,null);
            liveUserInfo.setRejectReasons(record.get(record.size()-1).getRejectReasons());
        }
        return liveUserInfo;
    }

    public void openOrCloseLiveSourceLine(Constant.LiveSourceLine liveSourceLine,boolean close){
        LiveSourceLineConfig liveSourceLineConfig = liveSourceLineConfigDaoService.findLiveSourceLineConfigByPlatformType(Constant.PlatformType.PLATFORM_JINLI);
        if(close)
            liveSourceLineConfig.getAvailableLine().remove(liveSourceLine);
        else
            liveSourceLineConfig.getAvailableLine().add(liveSourceLine);
        liveSourceLineConfigDaoService.save(liveSourceLineConfig);
        Jinli.JinliMessageReply messageReply = MessageUtil.buildReply(Jinli.LiveSourceChangeBroadcastMessage.newBuilder().addAllLiveSourceLines(liveSourceLineConfig.getAvailableLine()));
        DataManager.getOnlineRoomList().stream().filter(room -> room.getStartDate()!=null).forEach(room ->
                MessageUtil.sendMessage(liveUserDaoService.findById(room.getLiveUserId()).getUserId(),messageReply));
    }

    public PageInfo<LiveUserInfo> queryBanLiveUserInfoList(int platform, String liveUserId, String liveUserName, String roomId, int page, int size, Long startTime , Long endTime) {
        Constant.PlatformType platformType = Constant.PlatformType.forNumber(platform);
        PageRequest pageRequest = PageRequest.of(page, size);
        PageInfo<LiveUser> banLiveUser = liveUserDaoService.findBanLiveUser(platformType, liveUserId, liveUserName, roomId, pageRequest, startTime, endTime);
        List<LiveUserInfo> content = new ArrayList<>(banLiveUser.getContent().size());
        for (LiveUser liveUser : banLiveUser.getContent()) {
            content.add(liveUserToLiveUserInfo(liveUser));
        }
        return new PageInfo<>(content, banLiveUser.pageable, banLiveUser.total);
    }

    public Constant.ResultCode unLockLiveList(List<String> ids) {
        Constant.ResultCode resultCode;
        for (String id : ids) {
            resultCode = unLockLiveUser(id);
            if (!Objects.equals(Constant.ResultCode.SUCCESS, resultCode)) {
                return resultCode;
            }
        }
        return Constant.ResultCode.SUCCESS;
    }

    public PageInfo<LiveRecordInfo> queryPlatformLiveRecordByLiveRecordRequest(LiveRecordRequest recordRequest) {
        List<LiveRecordInfo> liveRecordInfos = new ArrayList<>();
        PageInfo<LiveRecord> pageInfo = liveRecordDaoService.findByLiveRecordByLiveRecordRequest(recordRequest);
        User user;
        for (LiveRecord liveRecord : pageInfo.getContent()) {
            user = userDaoService.findById(liveRecord.getUserId());
            liveRecordInfos.add(LiveRecordInfo.newInstance(user, liveRecord));
        }
        return new PageInfo<>(liveRecordInfos, pageInfo.getTotal());
    }

    public void passLiveUser(String liveUserId,String backofficeUserName) {
        var liveUser = liveUserDaoService.findById(liveUserId);
        if(liveUser==null) return;
        liveUser.setLiveStatus(Constant.LiveStatus.OFFLINE);
        liveUser.setAuditTime(System.currentTimeMillis());
        User user = dataManager.findUser(liveUser.getUserId());
        user.setPlatformType(liveUser.getPlatformType());
        user.setLiveUserId(liveUserId);
        //clean userCoin if other platform LiveUser
        if(user.isPlatformUser()) user.setGameCoin(0);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        LiveUserApproveRecord recentApproveRecord = liveUserApproveRecordDaoService.findRecentApproveRecordByStatue(liveUserId, LiveUserApproveStatue.UNAPPROVE);
        if(Objects.nonNull(recentApproveRecord)) {
            recentApproveRecord.approveLiveUser(LiveUserApproveStatue.PASS,backofficeUserName);
            liveUserApproveRecordDaoService.save(recentApproveRecord);
        }
        /*var saveLiveUserApprove = new LiveUserApproveRecord(backofficeUserName, liveUser.getId(), true, "", liveUser.getPlatformType(), new Date());
        liveUserApproveRecordDaoService.save(saveLiveUserApprove);*/
        //userDataStatisticsDaoService.deleteTodayUserDataStatistics(user.getId());
        EventPublisher.publish(TaskEvent.newInstance(liveUser.getUserId(), ConditionType.becomeLiveUser, 1));
    }

    public GenericResponse updateLiveUserSharedPlatform(LiveUserPlatformRequest request){
        if(StringUtils.isNullOrBlank(request.getLiveUserId())||request.getSharedPlatform().contains(null))
            return new ErrorResponse(GlobalResponseCode.REQUEST_PARAM_ERROR);
        LiveUser liveUser = dataManager.findLiveUser(request.getLiveUserId());
        if(Objects.isNull(liveUser) || !verifyUtil.verifyIsLiveUser(liveUser))
            return new ErrorResponse(GlobalResponseCode.LIVE_USER_NOT_FOUNT);
        Set<Constant.PlatformType> sharedPlatform = request.getSharedPlatform();
        sharedPlatform.remove(liveUser.getPlatformType());
        liveUser.setSharedPlatform(sharedPlatform);
        dataManager.saveLiveUser(liveUser);
        broadCastSharedPlatformChange(liveUser);
        return new SuccessResponse();
    }

    private void broadCastSharedPlatformChange(LiveUser liveUser){
        Jinli.LiveUserInfo liveUserInfo = liveProcess.buildLiveUserInfo(liveUser, roomDaoService.findByLiveUserId(liveUser.getId()));
        var broadcastMessage = Jinli.SharedPlatformChangeBroadcastMessage.newBuilder().setLiveUserInfo(liveUserInfo).build();
        MessageUtil.sendMessage(liveUser.getUserId(),MessageUtil.buildReply(broadcastMessage));
    }
}
