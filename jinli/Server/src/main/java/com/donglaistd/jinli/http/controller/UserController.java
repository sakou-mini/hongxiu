package com.donglaistd.jinli.http.controller;


import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.constant.StatisticEnum;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.dao.backoffice.*;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.*;
import com.donglaistd.jinli.database.entity.race.GoldenFlowerRace;
import com.donglaistd.jinli.database.entity.race.LandlordsRace;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.database.entity.race.TexasRace;
import com.donglaistd.jinli.database.entity.statistic.CommonStatisticInfo;
import com.donglaistd.jinli.database.entity.statistic.DailyBetInfo;
import com.donglaistd.jinli.event.ModifyCoinEvent;
import com.donglaistd.jinli.http.dto.reply.DayUserDataTotalReply;
import com.donglaistd.jinli.http.dto.reply.UserListReply;
import com.donglaistd.jinli.http.dto.reply.WatchLiveRecordReply;
import com.donglaistd.jinli.http.dto.request.PlatformTimeRequest;
import com.donglaistd.jinli.http.dto.request.UserListRequest;
import com.donglaistd.jinli.http.entity.*;
import com.donglaistd.jinli.http.response.GenericResponse;
import com.donglaistd.jinli.http.response.SuccessResponse;
import com.donglaistd.jinli.service.EventPublisher;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.service.statistic.UserManagerPageProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.TimeUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.logging.Logger;

import static com.donglaistd.jinli.util.RandomUtil.random;

@Controller
@RequestMapping("/backOffice/user")
public class UserController {
    private static final Logger logger = Logger.getLogger(BackOfficeController.class.getName());

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;
    @Autowired
    UserCoinOperationRecordDaoService userCoinOperationRecordDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    NewUserReportDataDaoService newUserReportDataDaoService;
    @Autowired
    ActiveUserReportDataDaoService activeUserReportDataDaoService;
    @Autowired
    RetainedUserReportDataDaoService retainedUserReportDataDaoService;
    @Autowired
    MobileDevicesReportDataDaoService mobileDevicesReportDataDaoService;
    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    ChangePasswordRecordDaoService changePasswordRecordDaoService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    UserManagerPageProcess userManagerPageProcess;
    @Autowired
    DailyBetInfoDaoService dailyBetInfoDaoService;

    @RequestMapping("/userList")
    @ResponseBody
    public Object userList() {
        return new ModelAndView("User/UserList.html");
    }

    @RequestMapping("/coinOperationRecord")
    @ResponseBody
    public Object coinOperationRecord() {
        return new ModelAndView("User/CoinOperationRecord.html");
    }


    @RequestMapping("/userData")
    @ResponseBody
    public Object userData() {
        return new ModelAndView("User/userData.html");
    }

    @RequestMapping("/userReport")
    @ResponseBody
    public Object userReport() {
        return new ModelAndView("User/userReport.html");
    }

    @RequestMapping("/monthlyDataDetails")
    @ResponseBody
    public Object monthlyDataDetails() {
        return new ModelAndView("User/monthlyDataDetails.html");
    }

    @RequestMapping("/userBetRecord")
    @ResponseBody
    public Object userBetRecord() {
        return new ModelAndView("User/userBetRecord.html");
    }

    @RequestMapping("/sameDayDataDetails")
    @ResponseBody
    public Object sameDayDataDetails() {
        return new ModelAndView("User/sameDayDataDetails.html");
    }

    @RequestMapping("/banUserList")
    @ResponseBody
    public Object banUserList() {
        return new ModelAndView("User/banUserList.html");
    }

    @RequestMapping("/viewingRecords")
    @ResponseBody
    public Object viewingRecords() {
        return new ModelAndView("User/viewingRecords.html");
    }

    @RequestMapping("/userDetails")
    @ResponseBody
    public Object liveUserDetails(String userId){
        User user = userDaoService.findById(userId);
        var userData = userManagerPageProcess.getUserListDataByUser(user);
        ModelAndView modelAndView = new ModelAndView("User/userDetails.html");
        modelAndView.addObject("data", userData);
        return modelAndView;
    }


    @RequestMapping("/taskAnalysis")
    @ResponseBody
    public Object taskAnalysis() {
        return new ModelAndView("User/taskAnalysis.html");
    }

    @RequestMapping("/getRecordList")
    @ResponseBody
    public Object getRecordList(String accountName,String displayName,int page,int size) {
        var sendData = new HttpURLConnection<UserCoinOperationRecordInfo>();
        if(StringUtils.isNullOrBlank(accountName)&&StringUtils.isNullOrBlank(displayName)){
            var recordPage = userCoinOperationRecordDaoService.findAllByPage(page-1,size);
            sendData.data = formatUserCoinOperationRecordInfo(recordPage);
            sendData.count = (int)recordPage.getTotalElements();
            sendData.code = 200;
            return sendData;
        }
        else if(!StringUtils.isNullOrBlank(accountName) && StringUtils.isNullOrBlank(displayName)){
            var recordPage = userCoinOperationRecordDaoService.findByBackOfficeIdAndPage(backOfficeUserIsNull(accountName),page-1,size);
            sendData.data = formatUserCoinOperationRecordInfo(recordPage);
            sendData.count = (int)recordPage.getTotalElements();
            sendData.code = 200;
            return sendData;
        }
        else if(!StringUtils.isNullOrBlank(displayName) && StringUtils.isNullOrBlank(accountName)){
            var recordPage = userCoinOperationRecordDaoService.findByUserIdAndPage(userIsNull(displayName),page-1,size);
            sendData.data = formatUserCoinOperationRecordInfo(recordPage);
            sendData.count = (int)recordPage.getTotalElements();
            sendData.code = 200;
            return sendData;
        }
        else {
            var recordPage = userCoinOperationRecordDaoService.findByBackOfficeIdAndUserIdAndPage(backOfficeUserIsNull(accountName),userIsNull(displayName),page-1,size);
            sendData.data = formatUserCoinOperationRecordInfo(recordPage);
            sendData.count = (int)recordPage.getTotalElements();
            sendData.code = 200;
            return sendData;
        }
    }

    public String backOfficeUserIsNull(String accountName){
        BackOfficeUser backOffice = backOfficeUserRepository.findByAccountName(accountName);
        if(backOffice == null){
            return "";
        }
        return backOffice.getId();
    }

    public String userIsNull(String displayName){
        User user = userDaoService.findByDisplayName(displayName);
        if(user == null){
            return "";
        }
        return user.getId();
    }

    public List<UserCoinOperationRecordInfo> formatUserCoinOperationRecordInfo(PageImpl<UserCoinOperationRecord> recordPage){
        List<UserCoinOperationRecord> records = recordPage.getContent();
        List<UserCoinOperationRecordInfo> infos = new ArrayList<>();
        BackOfficeUser backOfficeUser;
        for (UserCoinOperationRecord record : records) {
            UserCoinOperationRecordInfo info = new UserCoinOperationRecordInfo();
            backOfficeUser = backOfficeUserDaoService.findById(record.getBackOfficeId());
            if(backOfficeUser==null) continue;
            info.accountName = backOfficeUser.getAccountName();
            info.displayName = userDaoService.findById(record.getUserId()).getDisplayName();
            info.originalCoin = record.getOriginalCoin();
            info.existingCoin = record.getExistingCoin();
            info.operationCoin = record.getOperationCoin();
            info.time = record.getTime();
            infos.add(info);
        }
        return infos;
    }

    @RequestMapping("/operationUserCoin")
    @ResponseBody
    public Object operationUserCoin(String userId ,long gameCoin,int operationType, Principal principal) {
        User user = userDaoService.findById(userId);
        long userGameCoin = user.getGameCoin();
        if(operationType == 2 && userGameCoin < gameCoin){
            return "beyond";
        }
        UserCoinOperationRecord record = new UserCoinOperationRecord();
        record.setOriginalCoin(userGameCoin);
        switch (operationType){
            case 1:
                EventPublisher.publish(new ModifyCoinEvent(user, gameCoin));
                record.setOperationCoin(gameCoin);
                break;
            case 2:
                EventPublisher.publish(new ModifyCoinEvent(user, -gameCoin));
                record.setOperationCoin(-gameCoin);
                break;
        }
        User existingUser = userDaoService.findById(userId);
        record.setExistingCoin(existingUser.getGameCoin());
        record.setBackOfficeId(backOfficeUserRepository.findByAccountName(principal.getName()).getId());
        record.setUserId(userId);
        record.setTime(System.currentTimeMillis());
        userCoinOperationRecordDaoService.save(record);
        return null;
    }

    @RequestMapping("/getUserList")
    @ResponseBody
    public Object getUserList(String userId ,String phone,String ip,Integer statue,int page,int size,int platform){
        if(statue==null || Constant.AccountStatue.forNumber(statue ) == null)
            return  new HttpURLConnection<>(500,"statueNotExit");
        var sendData = new HttpURLConnection<>();
        PageInfo<User> pageInfo = userDaoService.getUserListByCondition(userId, phone,ip, Constant.AccountStatue.forNumber(statue), page, size, Constant.PlatformType.forNumber(platform));
        for (User user : pageInfo.getContent()) {
            sendData.addData(userManagerPageProcess.getUserListDataByUser(user));
        }
        sendData.code=pageInfo.getResultCode();
        sendData.count = (int) pageInfo.getTotal();
        return sendData;
    }

    @RequestMapping("/getOpenRaceNum")
    @ResponseBody
    public RaceInfo getOpenRaceNum(int raceNumber){
        Constant.RaceType raceType = Constant.RaceType.forNumber(raceNumber);
        Map<String, RaceBase> races = DataManager.getRaceMap().get(raceType);
        long count;
        long gameNum;
        switch (raceType){
            case LANDLORDS:
                count = races.values().stream().map(r -> (LandlordsRace) r).filter(r -> r.getGameIds().size()>0).count();
                gameNum = races.values().stream().map(r -> (LandlordsRace) r).mapToInt(race -> race.getGameIds().size()).sum();
                return new RaceInfo(count, gameNum);
            case TEXAS:
                count = races.values().stream().map(r -> (TexasRace) r).filter(r -> r.getGameIds().size()>0).count();
                gameNum = races.values().stream().map(r -> (TexasRace) r).mapToInt(race -> race.getGameIds().size()).sum();
                return new RaceInfo(count, gameNum);
            case GOLDEN_FLOWER:
                count = races.values().stream().map(r -> (GoldenFlowerRace) r).filter(r -> r.getGameIds().size()>0).count();
                gameNum = races.values().stream().map(r -> (GoldenFlowerRace) r).mapToInt(race -> race.getGameIds().size()).sum();
                return new RaceInfo(count, gameNum);
        }
        return new RaceInfo(0, 0);
    }

    @RequestMapping("/getNewUserChartData")
    @ResponseBody
    public Object getNewUserChartData(long startTime,long endTime){
        var sendData = new HttpURLConnection<NewUserReportData>();
        List<NewUserReportData> newUserReportDataList = newUserReportDataDaoService.findByDate(startTime,endTime);
        sendData.code=200;
        sendData.data = newUserReportDataList;
        return  sendData;
    }

    @RequestMapping("/getActiveUserChartData")
    @ResponseBody
    public Object getActiveUserChartData(long startTime,long endTime){
        var sendData = new HttpURLConnection<ActiveUserReportData>();
        List<ActiveUserReportData> activeUserReportDataList = activeUserReportDataDaoService.findByDate(startTime,endTime);
        sendData.code=200;
        sendData.data = activeUserReportDataList;
        return  sendData;
    }

    @RequestMapping("/getGroupMaxActiveDayData")
    @ResponseBody
    public Map<StatisticEnum.ActiveDaysEnum,Long> getGroupMaxActiveDayData(long startTime,long endTime){
        return userDataStatisticsProcess.totalActiveDaysForGroupByTimes(startTime, endTime);
    }

    @RequestMapping("/getRetainedUserChartData")
    @ResponseBody
    public Object getRetainedUserChartData(long startTime,long endTime){
        var sendData = new HttpURLConnection<RetainedUserReportData>();
        List<RetainedUserReportData> retainedUserReportDataList = retainedUserReportDataDaoService.findByDate(startTime,endTime);
        sendData.code=200;
        sendData.data = retainedUserReportDataList;
        return  sendData;
    }

    @RequestMapping("/getNearestRetainedUserReportData")
    @ResponseBody
    public RetainedUserReportData getRetainedUserChartData(){
        long time = TimeUtil.getBeforeDayStartTime(1);
        RetainedUserReportData retainedUserReportData = retainedUserReportDataDaoService.findByData(time);
        return retainedUserReportData == null ? RetainedUserReportData.newInstance(time, BigDecimal.valueOf(0),
                BigDecimal.valueOf(0), BigDecimal.valueOf(0)) : retainedUserReportData;
    }

    @RequestMapping("/banUser")
    @ResponseBody
    public Object banUserByUserIds(@RequestBody List<String> ids,Principal principal){
        List<User> users = userDaoService.findByUserIds(ids);
        if(users.size()!=ids.size()) return new HttpURLConnection<>(200,"userNotFound");
        userAttributeDaoService.updateUserAccountStatue(ids,principal.getName(), Constant.AccountStatue.ACCOUNT_BAN);
        userManagerPageProcess.broadCastUserBanMessageToUser(Constant.AccountStatue.ACCOUNT_BAN,ids);
        return new HttpURLConnection<>(200,"success");
    }


    @RequestMapping("/unsealedUser")
    @ResponseBody
    public Object unsealedUserByUserIds(@RequestBody List<String> ids,Principal principal){
        List<User> users = userDaoService.findByUserIds(ids);
        if(users.size() != ids.size())
            return new HttpURLConnection<>(500,"userNotFound");
        userAttributeDaoService.updateUserAccountStatue(ids,principal.getName(), Constant.AccountStatue.ACCOUNT_NORMAL);
        userManagerPageProcess.broadCastUserBanMessageToUser(Constant.AccountStatue.ACCOUNT_NORMAL,ids);
        return new HttpURLConnection<>(200,"success");
    }

    @PostMapping("/changePassword")
    @ResponseBody
    public Object changeUserPassword(String userId,String newPassword,Principal principal){
        if(StringUtils.isNullOrBlank(newPassword))
            return new HttpURLConnection<>(500, "illegalPassword");
        User user = dataManager.findUser(userId);
        if(user == null) return new HttpURLConnection<>(500, "userNotExit");
        user.setToken(passwordEncoder.encode(newPassword));
        dataManager.saveUser(user);
        changePasswordRecordDaoService.save(new ChangePasswordRecord(System.currentTimeMillis(), principal.getName(), userId, user.getGameCoin()));
        return new HttpURLConnection<>(200, "success");
    }

    @RequestMapping("/getMobileDevicesChartData")
    @ResponseBody
    public Object getMobileDevicesChartData(long startTime,long endTime){
        var sendData = new HttpURLConnection<MobileDevicesReportData>();
        List<MobileDevicesReportData> mobileDevicesReportDataList = mobileDevicesReportDataDaoService.findByDate(startTime,endTime);
        sendData.code=200;
        sendData.data = mobileDevicesReportDataList;
        return  sendData;
    }

    @RequestMapping("/insertChartTestData")
    @ResponseBody
    public Object insertChartTestData(){
        var dateList =  TimeUtil.getDayTimes(-365,8);
        dateList.forEach(aLong -> {
            NewUserReportData newUserReportData = new NewUserReportData();
            newUserReportData.setDate(aLong);
            newUserReportData.setDownloads(random.nextInt(500));
            newUserReportData.setNewUsers(random.nextInt(400));
            newUserReportDataDaoService.save(newUserReportData);
            ActiveUserReportData activeUserReportData = new ActiveUserReportData();
            activeUserReportData.setDate(aLong);
            activeUserReportData.setNewUserDau(random.nextInt(500));
            activeUserReportData.setOldUserDau(random.nextInt(500));
            activeUserReportData.setWau(random.nextInt(4000));
            activeUserReportData.setMau(random.nextInt(10000));
            activeUserReportDataDaoService.save(activeUserReportData);
            RetainedUserReportData retainedUserReportData = new RetainedUserReportData();
            retainedUserReportData.setDate(aLong);
            retainedUserReportData.setNextDayRetainRate(BigDecimal.valueOf(random.nextDouble()));
            retainedUserReportData.setNextWeekRetainRate(BigDecimal.valueOf(random.nextDouble()));
            retainedUserReportData.setNextMonthRetainRate(BigDecimal.valueOf(random.nextDouble()));
            retainedUserReportDataDaoService.save(retainedUserReportData);
            MobileDevicesReportData mobileDevicesReportData = new MobileDevicesReportData();
            Map<Constant.MobilePhoneBrand, Integer> newUserBrand = new HashMap<>();
            for (Constant.MobilePhoneBrand value : Constant.MobilePhoneBrand.values()) {
                newUserBrand.put(value, random.nextInt(500));
            }
            mobileDevicesReportData.setNewUserBrand(newUserBrand);
            Map<Constant.MobilePhoneBrand, Integer> activeUserBrand = new HashMap<>();
            for (Constant.MobilePhoneBrand value : Constant.MobilePhoneBrand.values()) {
                activeUserBrand.put(value, random.nextInt(500));
            }
            mobileDevicesReportData.setActiveUserBrand(activeUserBrand);
            Map<Constant.MobilePhoneBrand, Integer> payingUserBrand = new HashMap<>();
            for (Constant.MobilePhoneBrand value : Constant.MobilePhoneBrand.values()) {
                payingUserBrand.put(value, random.nextInt(500));
            }
            mobileDevicesReportData.setPayingUserBrand(payingUserBrand);
            Map<Constant.MobilePhoneResolution, Integer> payingUserResolution = new HashMap<>();
            for (Constant.MobilePhoneResolution value : Constant.MobilePhoneResolution.values()) {
                payingUserResolution.put(value, random.nextInt(500));
            }
            mobileDevicesReportData.setPayingUserResolution(payingUserResolution);
            Map<Constant.MobilePhoneOS, Integer> payingUserOS = new HashMap<>();
            for (Constant.MobilePhoneOS value : Constant.MobilePhoneOS.values()) {
                payingUserOS.put(value, random.nextInt(500));
            }
            mobileDevicesReportData.setPayingUserOS(payingUserOS);
            mobileDevicesReportData.setDate(aLong);
            mobileDevicesReportDataDaoService.save(mobileDevicesReportData);
        });
        return "success";
    }

    @RequestMapping("/queryUserBetDetail")
    @ResponseBody
    public HttpURLConnection<DailyBetInfo> queryUserBetDetail(String userId, int page, int size){
        PageInfo<DailyBetInfo> pageInfo = dailyBetInfoDaoService.findUserBetInfoPageInfo(userId, page-1, size);
        HttpURLConnection<DailyBetInfo> data = new HttpURLConnection<>(200, "success");
        data.count = (int) pageInfo.getTotal();
        data.data = Lists.newArrayList(pageInfo.getContent());
        return data;
    }

    @RequestMapping("/passwordRecord")
    @ResponseBody
    public PageInfo<Map<String,Object>> passwordRecord(String userId , String displayName, int page, int size){
        return userManagerPageProcess.findChangePasswordRecord(userId, displayName,  PageRequest.of(page - 1, size));
    }

    @RequestMapping("/queryBanUserList")
    @ResponseBody
    public HttpURLConnection<UserListData> queryBanUserList(int platform, String userId, String phoneNumber , String ip , int page, int size, Long startTime , Long endTime){
        PageInfo<User> pageInfo = userDaoService.findBanUser(platform, userId, phoneNumber, ip, page, size, startTime, endTime);
        var sendData = new HttpURLConnection<UserListData>();
        for (User user : pageInfo.getContent()) {
            sendData.addData(userManagerPageProcess.getUserListDataByUser(user));
        }
        sendData.code=pageInfo.getResultCode();
        sendData.count = (int) pageInfo.getTotal();
        return sendData;
    }

    //查询平台在线玩家信息
    @RequestMapping("/queryPlatformUserOnlineInfo")
    @ResponseBody
    public CommonStatisticInfo queryPlatformUserOnlineInfo(int platform){
        return userManagerPageProcess.queryPlatformUserOnlineInfo(Constant.PlatformType.forNumber(platform));
    }
    //查询平台每日用户数据汇总
    @RequestMapping("/queryPlatformDayUserTotalData")
    @ResponseBody
    public DayUserDataTotalReply queryPlatformDayUserTotalData(PlatformTimeRequest request){
        request.setPage(request.getPage()-1);
        return userManagerPageProcess.queryPlatformDayUserTotalData(request);
    }

    // 平台汇总用户列表查询
    @RequestMapping("/queryUserListForTotal")
    @ResponseBody
    public PageInfo<UserListReply> queryUserListForTotal(UserListRequest request){
        return userManagerPageProcess.queryUserListForTotal(request);
    }
    //用户直播观看记录
    @RequestMapping("/userLiveWatchRecord")
    @ResponseBody
    public PageInfo<WatchLiveRecordReply> userLiveWatchRecord(UserListRequest request){
        return userManagerPageProcess.queryUserWatchLiveRecords(request);
    }

    //query DailyUserActive
    @RequestMapping("/dailyUserActive")
    @ResponseBody
    public GenericResponse dailyUserActive(UserListRequest request){
        if(Objects.nonNull(request.getPage())) request.setPage(request.getPage()-1);
        return new SuccessResponse().withData(userManagerPageProcess.queryDailyUserActive(request));
    }
}

