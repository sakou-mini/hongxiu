package com.donglaistd.jinli.service.statistic;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.backoffice.ChangePasswordRecordDaoService;
import com.donglaistd.jinli.database.dao.invite.UserInviteRecordDaoService;
import com.donglaistd.jinli.database.dao.statistic.*;
import com.donglaistd.jinli.database.dao.statistic.record.ConnectedLiveRecordDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.database.entity.backoffice.ChangePasswordRecord;
import com.donglaistd.jinli.database.entity.invite.UserInviteRecord;
import com.donglaistd.jinli.database.entity.statistic.CommonStatisticInfo;
import com.donglaistd.jinli.database.entity.statistic.DailyUserActiveRecord;
import com.donglaistd.jinli.database.entity.statistic.DayLiveDataTotal;
import com.donglaistd.jinli.database.entity.statistic.DayUserDataTotal;
import com.donglaistd.jinli.http.dto.reply.DayUserDataTotalReply;
import com.donglaistd.jinli.domain.LiveWatchRecordResult;
import com.donglaistd.jinli.http.dto.reply.UserListReply;
import com.donglaistd.jinli.http.dto.reply.WatchLiveRecordReply;
import com.donglaistd.jinli.http.dto.request.PlatformTimeRequest;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.http.entity.UserListData;
import com.donglaistd.jinli.http.entity.statistic.UserGiftDetailData;
import com.donglaistd.jinli.util.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.StatisticEnum.*;
import static com.donglaistd.jinli.constant.StatisticEnum.StatisticItemEnum.*;
import static com.donglaistd.jinli.constant.WebKeyConstant.MONTH_USER_DATA;

@Component
public class UserManagerPageProcess {
    @Autowired
    CommonStatisticProcess commonStatisticProcess;
    @Autowired
    CommonStatisticInfoDaoService commonStatisticInfoDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DailyDownloadRecordDaoService downloadRecordDaoService;
    @Autowired
    CoinFlowDaoService coinFlowDaoService;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    ChangePasswordRecordDaoService changePasswordRecordDaoService;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    GiftLogDaoService giftLogDaoService;
    @Autowired
    UserInviteRecordDaoService userInviteRecordDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    GiftOrderDaoService giftOrderDaoService;
    @Autowired
    DayLiveDataTotalDaoService dayLiveDataTotalDaoService;
    @Autowired
    DayUserDataTotalDaoService dayUserDataTotalDaoService;
    @Autowired
    LiveWatchRecordDaoService liveWatchRecordDaoService;
    @Autowired
    BulletChatMessageRecordDaoService bulletChatMessageRecordDaoService;
    @Autowired
    ConnectedLiveRecordDaoService connectedLiveRecordDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    PlatformUtil platformUtil;
    @Autowired
    DailyUserActiveRecordDaoService dailyUserActiveRecordDaoService;

    private CommonStatisticInfo createEmptyCommonStatisticInfoOfUserdata(long time){
        CommonStatisticInfo commonStatisticInfo = CommonStatisticInfo.newInstance(time, MONTH_USERDATA);
        commonStatisticInfo.putStatisticItem(REGISTER_NUM,0);
        commonStatisticInfo.putStatisticItem(SPEND_AMOUNT,0);
        commonStatisticInfo.putStatisticItem(SPEND_NUM,0);
        commonStatisticInfo.putStatisticItem(IP_NUM,0);
        return commonStatisticInfo;
    }

    public CommonStatisticInfo getOnlineUserDataSummary() {
        List<Room> onlineRoom = DataManager.getOnlineRoomList().stream().filter(Room::isLive).collect(Collectors.toList());
        long totalUserNum = userDaoService.countAllActiveUser();
        long onlineNum =  DataManager.userChannel.keySet().stream().filter(uid -> !verifyUtil.checkIsLiveUser(dataManager.findUser(uid))).count();
        List<LiveUser> allPassLiveUser = liveUserDaoService.findAllPassLiveUser();
        long liveUserNum = allPassLiveUser.size();
        long jinliLiveUserNum = allPassLiveUser.stream().filter(liveUser -> Objects.equals(liveUser.getPlatformType(), Constant.PlatformType.PLATFORM_JINLI)).count();
        CommonStatisticInfo commonStatisticInfo =  CommonStatisticInfo.newInstance(0, OTHER);
        commonStatisticInfo.putStatisticItem(ONLINEUSER_NUM,onlineNum);
        commonStatisticInfo.putStatisticItem(USER_NUM,totalUserNum);
        commonStatisticInfo.putStatisticItem(LIVEUSER_NUM,liveUserNum);
        commonStatisticInfo.putStatisticItem(JINLI_LIVEUSER_NUM,jinliLiveUserNum);
        commonStatisticInfo.putStatisticItem(LIVE_NUM,onlineRoom.size());
        commonStatisticInfo.putStatisticItem(LIVE_WATCH_NUM,commonStatisticProcess.totalLiveAudienceNumByPlatform(onlineRoom));
        return commonStatisticInfo;
    }

    public Map<String,Object> getMonthUserDetail(){
        int yearOfMonth = 11;
        long yearOfMonthStartTime = TimeUtil.getBeforeMonthStartTime(yearOfMonth);
        Map<Long, CommonStatisticInfo> commonStatisticInfoMap = commonStatisticInfoDaoService
                .findStatisticInfoByTimeBetweenAndStatisticType(yearOfMonthStartTime, System.currentTimeMillis(), MONTH_USERDATA)
                .stream().collect(Collectors.toMap(CommonStatisticInfo::getStatisticTime, commonStatisticInfo -> commonStatisticInfo));
        for (int month = yearOfMonth; month > 0;month--){
            long monthTime = TimeUtil.getBeforeMonthStartTime(month);
            commonStatisticInfoMap.putIfAbsent(monthTime, createEmptyCommonStatisticInfoOfUserdata(monthTime));
        }
        List<CommonStatisticInfo> monthOfUserDataDetail = commonStatisticInfoMap.values().stream().sorted(Comparator.comparing(CommonStatisticInfo::getStatisticTime)).collect(Collectors.toList());
        monthOfUserDataDetail.add(commonStatisticProcess.totalUserdataByTimeBetween(TimeUtil.getMonthOfStartTime(System.currentTimeMillis()), System.currentTimeMillis(),MONTH_USERDATA));
        Map<String, Object> monthDetailsResult = new HashMap<>(3);
        monthDetailsResult.put(MONTH_USER_DATA, monthOfUserDataDetail);
        return monthDetailsResult;
    }

    public  PageInfo<UserGiftDetailData> getTodayUserDataDetail(int size,int page){
        return commonStatisticProcess.getTodayUserSpendDetail(size, page);
    }

    public  CommonStatisticInfo getTodayUserDataSummary(){
        return commonStatisticProcess.totalCurrentDayUserData();
    }

    public CommonStatisticInfo getCurrentMonthUserDataSummary(){
        return commonStatisticProcess.totalUserdataByTimeBetween(TimeUtil.getMonthOfStartTime(System.currentTimeMillis()), System.currentTimeMillis(),MONTH_USERDATA);
    }

    public UserListData getUserListDataByUser(User user){
        var userData = new UserListData();
        var coinFlow = coinFlowDaoService.findByUserId(user.getId());
        userData.setCoinFlow(coinFlow == null ? 0 : coinFlow.getFlow());
        userData.setServiceFlow(coinFlow == null ? 0 : coinFlow.getServiceFlow());
        userData.setLiveUser(verifyUtil.checkIsLiveUser(user));
        userData.setLiveUserId(user.getLiveUserId());
        userData.setGameCoin(user.getGameCoin());
        userData.setId(user.getPlatformShowUserId());
        userData.setLastLoginTime(user.getLastLoginTime());
        userData.setLevel(user.getLevel());
        userData.setDisplayName(user.getDisplayName());
        userData.setPhoneNumber(user.getPhoneNumber());
        userData.setRecharge(getRechargeCoinAmount(user, coinFlow));
        userData.setVipType(user.getVipType());
        userData.setLastMobileModel(user.getLastMobileModel());
        userData.setLastIp(user.getLastIp());
        userData.setCreateDate(user.getCreateDate().getTime());
        userData.setOnline(Objects.nonNull(DataManager.getUserChannel(user.getId())));
        userData.setAccountStatue(userAttributeDaoService.findByUserIdOrSaveIfNotExit(user.getId()).getStatue());
        userData.setPlatform(user.getPlatformType());
        userData.setAvatarUrl(user.getAvatarUrl());
        UserInviteRecord inviteRecord = userInviteRecordDaoService.findByBeInviteUserId(user.getId());
        if(Objects.nonNull(inviteRecord)) userData.setInviteCode(inviteRecord.getInviteUserId());
        userData.setGiftSpendAmount(giftLogDaoService.getUserSendGiftAmount(user.getId()));
        userData.setPlatformTag(platformUtil.getUserPlatformCreateTag(user));
        userData.setJinliUserId(user.getId());
        return userData;
    }

    public long getRechargeCoinAmount(User user, CoinFlow coinFlow) {
        switch (user.getPlatformType()){
            case PLATFORM_Q:
                return giftOrderDaoService.totalGiftOrderByUserId(user.getId());
            case PLATFORM_JINLI:
            case PLATFORM_T:
                return coinFlow == null ? 0 :coinFlow.getRecharge();
            default: return 0;
        }
    }

    public PageInfo<Map<String,Object>> findChangePasswordRecord(String userId, String displayName, PageRequest pageRequest) {
        PageInfo<ChangePasswordRecord> pageInfo = changePasswordRecordDaoService.getChangePasswordRecordPageInfo(userId, displayName, pageRequest);
        List<Map<String,Object>> content = new ArrayList<>(pageInfo.getContent().size());
        User user;
        JSONObject jsonObject;
        for (ChangePasswordRecord changePasswordRecord : pageInfo.getContent()) {
            user = userDaoService.findById(changePasswordRecord.getUserId());
            jsonObject = new JSONObject(changePasswordRecord);
            jsonObject.put("avatarUrl", user.getAvatarUrl());
            jsonObject.put("displayName", user.getDisplayName());
            jsonObject.put("createTime", user.getCreateDate().getTime());
            content.add(jsonObject.toMap());
        }
        return new PageInfo<>(content,pageInfo.getTotal());
    }

    public void broadCastUserBanMessageToUser(Constant.AccountStatue accountStatue,List<String> userIds){
        Jinli.UserBanBroadcastMessage.Builder builder = Jinli.UserBanBroadcastMessage.newBuilder().setStatue(accountStatue);
        for (String userId : userIds) {
            MessageUtil.sendMessage(userId,MessageUtil.buildReply(builder));
        }
    }

    public CommonStatisticInfo getPlatformIndexData(Constant.PlatformType platform) {
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = System.currentTimeMillis();
        return commonStatisticProcess.totalPlatformUserDataByTimeBetween(startTime, endTime, platform, DAY_USERDATA);
    }

    public DayLiveDataTotal getPlatformTodayLiveData(Constant.PlatformType platform) {
        long startTime = TimeUtil.getCurrentDayStartTime();
        long endTime = System.currentTimeMillis();
        return commonStatisticProcess.totalPlatformTodayLiveData(platform,startTime,endTime);
    }

    public List<DayLiveDataTotal> getPlatformLiveDataByTimes(Constant.PlatformType platform ,long startTime , long endTime) {
        return dayLiveDataTotalDaoService.findByPlatformAndTimeBetween(platform,startTime,endTime);
    }

    public PageInfo<DayLiveDataTotal> getPlatformLiveDataByPlatformTimeRequest(PlatformTimeRequest request) {
        return dayLiveDataTotalDaoService.findByPagePlatformAndTimeBetween(request);
    }

    public CommonStatisticInfo queryPlatformUserOnlineInfo(Constant.PlatformType platform) {
        long totalUserNum = userDaoService.countNormalActiveUserNumByPlatform(platform);
        long onlineNum = DataManager.userChannel.keySet().stream().filter(id -> {
            User user = userDaoService.findById(id);
            return Objects.equals(user.getPlatformType(), platform) && !verifyUtil.checkIsLiveUser(user);
        }).count();
        CommonStatisticInfo commonStatisticInfo =  CommonStatisticInfo.newInstance(0, OTHER);
        commonStatisticInfo.putStatisticItem(ONLINEUSER_NUM,onlineNum);
        commonStatisticInfo.putStatisticItem(USER_NUM,totalUserNum);
        return commonStatisticInfo;
    }

    public DayUserDataTotalReply queryPlatformDayUserTotalData(PlatformTimeRequest request) {
        PageInfo<DayUserDataTotal> pageInfo = dayUserDataTotalDaoService.queryPlatformDataByTimesAndPage(request);
        return new DayUserDataTotalReply(pageInfo);
    }

    public PageInfo<UserListReply> queryUserListForTotal(UserListRequest request) {
        List<UserListReply> replyContent = new ArrayList<>();
        PageInfo<User> userPageInfo = userDaoService.findByUserListRequest(request);
        for (User user : Optional.ofNullable(userPageInfo.getContent()).orElse(new ArrayList<>())) {
            replyContent.add(getUserListReply(user));
        }
        return new PageInfo<>(replyContent,userPageInfo.getTotal());
    }

    private UserListReply getUserListReply(User user){
        UserAttribute attribute = userAttributeDaoService.findByUserId(user.getId());
        LiveWatchRecordResult watchRecordResult = liveWatchRecordDaoService.totalUserWatchRecord(user.getId());
        var coinFlow = Optional.ofNullable(coinFlowDaoService.findByUserId(user.getId())).orElse(new CoinFlow());
        long exchangeCoinAmount = getRechargeCoinAmount(user, coinFlow);
        long sendGiftCount = giftLogDaoService.countUserSendGift(user.getId());
        var userListReply = new UserListReply(user);
        userListReply.setWatchLiveCount(watchRecordResult.getWatchCount());
        userListReply.setWatchLiveNum(watchRecordResult.getWatchNum());
        userListReply.setExchangeCoinAmount(exchangeCoinAmount);
        userListReply.setAvgWatchLiveTime(watchRecordResult.getAvgWatchLiveTime());
        userListReply.setWatchLiveTime(watchRecordResult.getWatchTime());
        userListReply.setSendGiftCount(sendGiftCount);
        userListReply.setSendGiftFlow(coinFlow.getGiftCost());
        userListReply.setConnectedLiveCount(watchRecordResult.getConnectedLiveCount());
        userListReply.setBulletMessageCount(watchRecordResult.getBulletMessageCount());
        userListReply.setPlatformTag(platformUtil.getUserPlatformCreateTag(user));
        if(Objects.nonNull(attribute)) userListReply.setStatue(attribute.getStatue());
        return userListReply;
    }

    public PageInfo<WatchLiveRecordReply> queryUserWatchLiveRecords(UserListRequest request) {
        PageInfo<LiveWatchRecord> records = liveWatchRecordDaoService.findWatchLiveRecordByRequest(request);
        List<WatchLiveRecordReply> watchLiveRecordReplyList = new ArrayList<>();
        User user;
        User roomUser;
        for (LiveWatchRecord liveWatchRecord : records.getContent()) {
            String userName =  Optional.ofNullable(userDaoService.findById(liveWatchRecord.getUserId())).orElse(new User()).getDisplayName();
            String roomLiveUserName = Optional.ofNullable(userDaoService.findByLiveUserId(liveWatchRecord.getRoomLiveUserId())).orElse(new User()).getDisplayName();
            user = userDaoService.findById(liveWatchRecord.getUserId());
            if(Objects.nonNull(user)) liveWatchRecord.setUserId(user.getPlatformShowUserId());
            watchLiveRecordReplyList.add(new WatchLiveRecordReply(liveWatchRecord, userName, roomLiveUserName));
        }
        return new PageInfo<>(watchLiveRecordReplyList, records.getTotal());
     }

    public PageInfo<DailyUserActiveRecord> queryDailyUserActive(UserListRequest request) {
        PageInfo<DailyUserActiveRecord> pageInfo = dailyUserActiveRecordDaoService.queryDailyUserActive(request);
        for (DailyUserActiveRecord dailyUserActiveRecord : pageInfo.getContent()) {
            if(!StringUtils.isNullOrBlank(dailyUserActiveRecord.getUserId()))
                dailyUserActiveRecord.setDisplayName(
                    Optional.ofNullable(userDaoService.findById(dailyUserActiveRecord.getUserId())).orElse(new User()).getDisplayName());
        }
        return pageInfo;
    }

}
